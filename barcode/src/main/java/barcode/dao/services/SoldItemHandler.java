package barcode.dao.services;

import barcode.dao.utils.ComingItemFilter;
import com.querydsl.core.types.Predicate;
import barcode.dao.entities.*;
import barcode.dao.entities.embeddable.Comment;
import barcode.dao.predicates.SoldItemPredicatesBuilder;
import barcode.dao.repositories.SoldItemsRepository;
import barcode.dao.utils.BasicFilter;
import barcode.dao.utils.SoldItemFilter;
import barcode.dto.ResponseBySoldItems;
import barcode.dto.ResponseItem;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SoldItemHandler extends EntityHandlerImpl {

    public static SoldItemPredicatesBuilder sipb = new SoldItemPredicatesBuilder();

    private SoldItemsRepository soldItemsRepository;

    private final ComingItemHandler comingItemHandler;

    private SoldCompositeItemHandler soldCompositeItemHandler;

    private UserHandler userHandler;

    private StockHandler stockHandler;

    private BuyerHandler buyerHandler;

    private ItemHandler itemHandler;

    private ReceiptHandler receiptHandler;

    public SoldItemHandler (SoldItemsRepository soldItemsRepository,
                            SoldCompositeItemHandler soldCompositeItemHandler,
                            ComingItemHandler comingItemHandler,
                            UserHandler userHandler,
                            StockHandler stockHandler,
                            BuyerHandler buyerHandler,
                            ReceiptHandler receiptHandler,
                            ItemHandler itemHandler) {

        this.soldItemsRepository = soldItemsRepository;
        this.comingItemHandler = comingItemHandler;
        this.userHandler = userHandler;
        this.stockHandler = stockHandler;
        this.buyerHandler = buyerHandler;
        this.receiptHandler = receiptHandler;
        this.itemHandler = itemHandler;
        this.soldCompositeItemHandler = soldCompositeItemHandler;
    }

    public ResponseItem makeAutoSelling(List<SoldItem> sellings) {

        ResponseItem<ResponseItem> responseItem =
                new ResponseItem<ResponseItem>("Обработка автоматических продаж", new ArrayList<ResponseItem>(), true);

        ResponseItem responseItemTemp;

        for (SoldItem selling : sellings) {

            responseItemTemp = this.checkSelling(selling);

            responseItem.getEntityItems().add(responseItemTemp);

            if (!responseItemTemp.getSuccess()) {

                responseItem.setText("Формирование продаж не состоится!");

                return responseItem;
            }
        }

        this.addSellings(sellings);

        return responseItem;
    }

    public List<SoldItem> getSoldItemsByFilter(SoldItemFilter filter) {
        return soldItemsRepository.findAll(SoldItemHandler.sipb.buildByFilter(filter));

    }

    private ResponseItem checkSelling(SoldItem selling) {

        ResponseItem<ResponseItem> responseItem = new ResponseItem<ResponseItem>("результат проверки прихода товара "
                            + selling.getComing().getItem().getName(), new ArrayList<ResponseItem>(), false);

        //user
        User user = userHandler.getUserByName(selling.getUser().getName());

        if (user == null)
            return new ResponseItem("Пользователь из прихода не найден в БД" , false);

        selling.setUser(user);

        //stock
        Stock stock = stockHandler.getStockByName(selling.getComing().getStock().getName());

        if (stock == null)
            return new ResponseItem("Заведите склад со следующим наименованием! - " +
                    selling.getComing().getStock().getName(), false);

        //coming
        ResponseItem<ComingItem> responseByComing = this.comingItemHandler.getComingForSell(
                                                    selling.getComing().getItem().getEan(), stock.getId());

        responseItem.getEntityItems().add(responseByComing);

        if (!responseByComing.getSuccess())
            return responseByComing;

        selling.setComing(responseByComing.getEntityItem());

        selling.getComing().setStock(stock);

        //buyer
        ResponseItem responseByBuyer = new ResponseItem("Покупатель " + selling.getBuyer().getName() + SMTH_FOUND);

        Buyer buyer = this.buyerHandler.getBuyerByName(selling.getBuyer().getName());

        if (buyer == null) {

            buyer = this.buyerHandler.addBuyer(selling.getBuyer()).getEntityItem();

            responseByBuyer.setText("Покупатель " + selling.getBuyer().getName() + SMTH_CREATED);
        }

        selling.setBuyer(buyer);

        responseItem.getEntityItems().add(responseByBuyer);

        QSoldItem qSoldItem = QSoldItem.soldItem;

        Predicate predicate = qSoldItem.comment.eq(selling.getComment()).and(qSoldItem.price.eq(selling.getPrice()))
                .and(qSoldItem.quantity.eq(selling.getQuantity()))
                .and((qSoldItem.coming.item.id.eq(selling.getComing().getItem().getId())));

        if (soldItemsRepository.findAll(predicate).size() > 0) {

            responseItem.getEntityItems().add(new ResponseItem("Соответствующая продажа " +
                    "уже содержится, добавление НЕ состоится"));

            return responseItem;
        }

        responseItem.setSuccess(true);

        return responseItem;
    }

    private BigDecimal getPriceOfSoldItem(SoldItem soldItem, BigDecimal priceIn) {
        return soldItem.getBuyer().getSellByComingPrices() || soldItem.getSoldCompositeItem() != null ? priceIn : soldItem.getPrice();
    }


    void makeSellingByInvoice(List<SoldItem> sellings) {

        // get Sellings by comment with a specified type, buyer, and stock
        // compare by quantity
        // correcting incoming sellings by quantity
        // addSellings

    }

    private BigDecimal getRequeredQuantityForSell(List<SoldItem> soldItems, String ean) {

        BigDecimal quantity = BigDecimal.ZERO;

        for(SoldItem soldItem : soldItems)
            if(soldItem.getComing().getItem().getEan().equals(ean))
                quantity = quantity.add(soldItem.getQuantity());

        return quantity;
    }

    private ResponseItem<SoldItem> checkSoldItemsForComposites(List<SoldItem> soldItems) {

        List<SoldItem> resultSoldItems = new ArrayList<>();

        soldItems.forEach(soldItem -> {

            if(soldItem.getComing().getItem().getComponents().size() > 0) {
                SoldCompositeItem soldCompositeItem = new SoldCompositeItem(
                    soldItem.getComing().getItem(),
                        soldItem.getBuyer().getSellByComingPrices()
                                ? BigDecimal.ZERO : soldItem.getPrice().multiply(soldItem.getQuantity()),
                        soldItem.getBuyer().getSellByComingPrices() ? BigDecimal.ZERO : soldItem.getPrice(),
                        soldItem.getQuantity()
                );
//                soldCompositeItemHandler.save(soldCompositeItem);

//                BigDecimal sum  = soldCompositeItem.getPrice()
//                        .divide(new BigDecimal(soldItem.getComing().getItem().getComponents().size()), 5, RoundingMode.DOWN);

                soldItem.getComing().getItem().getComponents()
                        .forEach(component -> {
                            SoldItem cmpntSoldItem = new SoldItem(
                                    new ComingItem(component.getItem(),soldItem.getComing().getStock()),
//                                    sum.divide(component.getQuantity(), 5, RoundingMode.UP),
                                    BigDecimal.ZERO,
                                    soldItem.getComing().getStock().getOrganization().getVatValue(),
                                    soldItem.getQuantity().multiply(component.getQuantity()),
                                    "",
                                    soldItem.getBuyer(),
                                    soldItem.getUser()
                            );
                            cmpntSoldItem.setSoldCompositeItem(soldCompositeItem);
                            resultSoldItems.add( cmpntSoldItem);
                });

            }
            else
                resultSoldItems.add(soldItem);
        });
        return new ResponseItem<SoldItem>("", resultSoldItems, true);
    }

    private void setSumForComposites(SoldItem soldItem) {

        if(soldItem.getSoldCompositeItem() != null && soldItem.getBuyer().getSellByComingPrices()) {
                soldItem.getSoldCompositeItem()
                        .setPrice(soldItem.getSoldCompositeItem().getPrice()
                                .add(
                                        soldItem.getPrice().multiply(soldItem.getQuantity()
                                                .divide(soldItem.getSoldCompositeItem().getQuantity(), 3, BigDecimal.ROUND_UP)))
                        );
                soldItem.getSoldCompositeItem()
                        .setSum(soldItem.getSoldCompositeItem().getSum()
                                .add(soldItem.getSum()));
        }
    }

    public ResponseItem<SoldItem> addSellings (List<SoldItem> soldItems) {

        Long uuid = new Random().nextLong();

        Receipt receipt = receiptHandler
                            .getReceiptByBuyer(soldItems.iterator().next().getBuyer(),
                                               soldItems.stream()
                                                .map(v -> v.getPrice().multiply(v.getQuantity()))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                                               soldItems.size(),
                                               soldItems.iterator().next().getUser());

        ResponseItem<SoldItem> checkedItems = checkSoldItemsForComposites(soldItems);
        if (!checkedItems.getSuccess())
            return checkedItems;

        synchronized (comingItemHandler) {
            for (SoldItem soldItem : checkedItems.getEntityItems()) {

                //try get from RE<CominItem> after checking
                List<ComingItem> comings = this.comingItemHandler.getComingItemByIdAndStockId(
                        soldItem.getComing().getItem().getId(),
                        soldItem.getComing().getStock().getId());

//                BigDecimal reqForSell = soldItem.getQuantity();
                BigDecimal reqForSell = getRequeredQuantityForSell(
                        checkedItems.getEntityItems(), soldItem.getComing().getItem().getEan());

                BigDecimal availQuantityByEan = comingItemHandler.getAvailQuantityByEan(comings);

                if(reqForSell.compareTo(availQuantityByEan) > 0  || comings.size() == 0)
                    return new ResponseItem<>(getInsufficientQuantityOfGoodsMessage(
                                    reqForSell, availQuantityByEan, soldItem.getComing().getItem().getName()
                    ),
                            false);

                availQuantityByEan = availQuantityByEan.subtract(reqForSell);

                for (ComingItem coming: comings) {

                    BigDecimal availQuant = coming.getCurrentQuantity();

//                    reqForSell = soldItem.getQuantity();

                    if (availQuant.compareTo(BigDecimal.ZERO) > 0) { // проверяем на остаток по приходу

                        SoldItem newSoldItem = new SoldItem();

                        newSoldItem.setComments(new ArrayList<>());

                        if(soldItem.getComment() != null)
                            newSoldItem.setComment(
                                    this.buildComment(newSoldItem.getComments(),
                                            getQuantityForComment(
                                                    (reqForSell.compareTo(availQuant) > 0) ? availQuant : reqForSell)
                                                    + soldItem.getComment(),
                                            userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                                            SALE_COMMENT, newSoldItem.getQuantity())
                            );

                        newSoldItem.setAvailQuantityByEan(
                                availQuantityByEan.compareTo(BigDecimal.ZERO) > 0 ? availQuantityByEan : BigDecimal.ZERO);

                        newSoldItem.setQuantityBeforeSelling(availQuant); // устанавливаем количество до продажи

                        newSoldItem.setBuyer(soldItem.getBuyer());

                        newSoldItem.setUser(soldItem.getUser());

                        newSoldItem.setDate(new Date());

                        Integer discount = soldItem.getBuyer().getDiscount();

                        if(discount !=null && discount > 0)
                            newSoldItem.setComment(
                                    this.buildComment(newSoldItem.getComments(), "",
                                            userHandler.getCurrentUser().getFullName(),
                                            "применена скидка " + discount + "%.", newSoldItem.getQuantity())
                            );


                        coming.setComment(
                                this.buildComment(coming.getComments(),
                                        soldItem.getComment() + " для " + soldItem.getBuyer().getName()
                                                + getQuantityForComment((reqForSell.compareTo(availQuant) > 0) ? availQuant : reqForSell),
                                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                                        SALE_COMMENT, coming.getCurrentQuantity())
                        );

                        if (reqForSell.compareTo(availQuant) > 0) {

                            coming.setCurrentQuantity(BigDecimal.ZERO);

                            soldItem.setQuantity(reqForSell.subtract( availQuant));

                            newSoldItem.setQuantity(availQuant);

                            reqForSell = soldItem.getQuantity();

                        } else {

                            coming.setCurrentQuantity(availQuant.subtract(reqForSell));

                            newSoldItem.setQuantity(reqForSell);

                            reqForSell = BigDecimal.ZERO;
                        }

                        coming.setSum(coming.getPriceIn().multiply(coming.getCurrentQuantity())
                                .setScale(2, BigDecimal.ROUND_HALF_UP));

                        coming.setLastChangeDate(newSoldItem.getDate());

                        newSoldItem.setComing(coming); // - добавляем ссылку на приход новой продажи

                        soldCompositeItemHandler.save(soldItem.getSoldCompositeItem());
                        newSoldItem.setSoldCompositeItem(soldItem.getSoldCompositeItem());

                        newSoldItem.setPrice(
                                getPriceOfSoldItem(soldItem, coming.getPriceIn())
                        );

                        newSoldItem.setSum(
                                newSoldItem.getPrice()
                                .multiply(newSoldItem.getQuantity())
                                .setScale(2, BigDecimal.ROUND_HALF_UP)
                        );

                        setSumForComposites(newSoldItem);

                        newSoldItem.setVat(soldItem.getVat());

                        receiptHandler.save(receipt);
                        newSoldItem.setReceipt(receipt);

                        soldItemsRepository.save(newSoldItem);

                        if (reqForSell.compareTo(BigDecimal.ZERO) == 0) break;

                    }
                }
            }

            return new ResponseItem<SoldItem>(SALE_COMPLETED_SUCCESSFULLY, true);
        }


    }

    private ResponseBySoldItems groupByItems(
            List<SoldItem> soldItems, SoldItemFilter filter) {

        Map<Item, List<SoldItem>> groupedSoldItems =
                soldItems.stream()
                        .collect(Collectors.groupingBy(n -> n.getComing().getItem()));

        List<SoldItem> result = new ArrayList<SoldItem>();

        groupedSoldItems.forEach(
                (item, sellings) -> {
                    result.add(new SoldItem(
                            new ComingItem(item, filter.getStock()),
                            sellings
                                    .stream()
                                    .map(SoldItem::getPrice)
                                    .reduce(BigDecimal::max).get(),
                            sellings
                                    .stream()
                                    .map(SoldItem::getQuantity)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add),
                            comingItemHandler
                                    .getComingItemByIdAndStockId(
                                            item.getId(),
                                            filter.getStock().getId())
                                    .stream().map(ComingItem::getCurrentQuantity)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                            ));
                }
        );

        sortGroupedItems(result,
                filter.getSortDirection(),
                SoldItemFilter.SoldItemSortingStrategies
                        .valueOf(filter.getSortField()
                                .replace(".","_")
                                .toUpperCase()));

        PagedListHolder<SoldItem> page = new PagedListHolder<SoldItem>(result);
        page.setPageSize(filter.getRowsOnPage());
        page.setPage(filter.getPage() - 1);

        ResponseBySoldItems ribysi =
                new ResponseBySoldItems("сгрупированные данные", page.getPageList(),
                        true, page.getPageCount());

        if(filter.getCalcTotal())
            ribysi.calcTotals(soldItems);

        return ribysi;
    }

    public ResponseBySoldItems findByFilter(SoldItemFilter filter) {

        itemHandler.checkEanInFilter(filter);

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());

        if(filter.getGroupByItems())
            return groupByItems(soldItemsRepository.findAll(sipb.buildByFilter(filter), sort), filter);

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<SoldItem> page =  soldItemsRepository.findAll(sipb.buildByFilter(filter), pageRequest);

        List<SoldItem> result = page.getContent();

        if (result.size() > 0) {

            ResponseBySoldItems ribysi =
                    new ResponseBySoldItems(ELEMENTS_FOUND, result, true, page.getTotalPages());

            if(filter.getCalcTotal())
                ribysi.calcTotals(soldItemsRepository.findAll(sipb.buildByFilter(filter)));

            return ribysi;
        }

        return new ResponseBySoldItems(NOTHING_FOUND, new ArrayList<>(), false, 0);
    }

    public ResponseItem<SoldItem> changeDate(SoldItem soldItem) {

        this.changeDate(soldItem.getId(), soldItem.getDate(),
                soldItemsRepository, userHandler.getCurrentUser().getFullName());

        return null;
    }

    public synchronized ResponseItem<SoldItem> returnSoldItem(SoldItem soldItem) {

        SoldItem newSoldItem = soldItemsRepository.findOne(soldItem.getId());

        if(soldItem.getQuantity().compareTo(newSoldItem.getQuantity()) > 0)
            return new ResponseItem<SoldItem>(getInsufficientQuantityOfGoodsMessage(
                    soldItem.getQuantity(), newSoldItem.getQuantity(), soldItem.getComing().getItem().getName()
            ), false);

        ComingItem comingItem = comingItemHandler.getComingItemById(soldItem.getComing().getId());

        comingItem.setLastChangeDate(new Date());

        comingItem.setCurrentQuantity(comingItem.getCurrentQuantity().add(soldItem.getQuantity()));

        comingItem.setSum(comingItem.getPriceIn().multiply(comingItem.getCurrentQuantity())
                .setScale(2, BigDecimal.ROUND_HALF_UP));

        comingItem.setComment(
                this.buildComment(comingItem.getComments(),
                        " от " + newSoldItem.getBuyer().getName() +
                                getQuantityForComment(soldItem.getQuantity())  + soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                        RETURN_COMMENT, comingItem.getCurrentQuantity()));

        newSoldItem.setQuantity(newSoldItem.getQuantity().subtract(soldItem.getQuantity()));

        newSoldItem.setSum(newSoldItem.getPrice().multiply(newSoldItem.getQuantity())
                        .setScale(2, BigDecimal.ROUND_HALF_UP));

        newSoldItem.setAvailQuantityByEan(newSoldItem.getAvailQuantityByEan().add(soldItem.getQuantity()));

        newSoldItem.setComment(
                this.buildComment(newSoldItem.getComments(),
                        getQuantityForComment(soldItem.getQuantity()) + soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                        RETURN_COMMENT, newSoldItem.getQuantity()));

        soldItemsRepository.save(newSoldItem);

        return new ResponseItem<>(RETURN_COMPLETED_SUCCESSFULLY, true);
    }

//    public synchronized ResponseItem addOneSelling(SoldItem soldItem) {
public ResponseItem addOneSelling(SoldItem soldItem) {

    synchronized(comingItemHandler) {

        soldItem.setAvailQuantityByEan(comingItemHandler
                .getComingItemByIdAndStockId(
                        soldItem.getComing().getItem().getId(),
                        soldItem.getComing().getStock().getId())
                .stream().map(ComingItem::getCurrentQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(soldItem.getQuantity())
        );

        ComingItem comingItem = comingItemHandler.getComingItemById(soldItem.getComing().getId());

        if(soldItem.getQuantity().compareTo(comingItem.getCurrentQuantity()) > 0
                || soldItem.getAvailQuantityByEan().compareTo(BigDecimal.ZERO) < 0)
            return new ResponseItem(getInsufficientQuantityOfGoodsMessage(
                    soldItem.getQuantity(), comingItem.getCurrentQuantity(), comingItem.getItem().getName()
            ), false);

        soldItem.setQuantityBeforeSelling(comingItem.getCurrentQuantity());

        comingItem.setCurrentQuantity(comingItem.getCurrentQuantity().subtract(soldItem.getQuantity()));

        comingItem.setLastChangeDate(new Date());

        comingItem.setComment(
                this.buildComment(comingItem.getComments(),
                        soldItem.getComment() + " для " + soldItem.getBuyer().getName()
                                + getQuantityForComment(soldItem.getQuantity()),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                        SALE_COMMENT, comingItem.getCurrentQuantity())
        );

        soldItem.setDate(new Date());

        soldItem.setComments(new ArrayList<>());
        soldItem.setComment(
                this.buildComment(soldItem.getComments(), getQuantityForComment(soldItem.getQuantity())
                                + soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                        SALE_COMMENT, soldItem.getQuantity())
        );

        soldItem.setPrice(getPriceOfSoldItem(soldItem, comingItem.getPriceIn()));

        comingItem.setSum(comingItem.getPriceIn().multiply(comingItem.getCurrentQuantity())
                .setScale(2, BigDecimal.ROUND_HALF_UP));

        soldItem.setSum(soldItem.getPrice().multiply(soldItem.getQuantity())
                .setScale(2, BigDecimal.ROUND_HALF_UP));

        Receipt receipt = receiptHandler
                .getReceiptByBuyer(soldItem.getBuyer(), soldItem.getSum(), 1, soldItem.getUser());

        receiptHandler.save(receipt);
        soldItem.setReceipt(receipt);

        soldItemsRepository.save(soldItem);

        return new ResponseItem(SALE_COMPLETED_SUCCESSFULLY, true);
    }

    }

    public void addComment(Comment comment, Long id) {
        if(id != null) {
            SoldItem soldItem = soldItemsRepository.findOne(id);
            soldItem.setComment(
                    this.buildComment(soldItem.getComments(), comment.getText(),
                            userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                            comment.getAction(), soldItem.getQuantity())
            );
            soldItemsRepository.save(soldItem);
        }
    }

    public SoldItem getItemById(Long id) {
        return soldItemsRepository.findOne(id);
    }

    public ResponseItem<SoldItem> changeSoldItem (SoldItem soldItem) {

        ResponseItem<SoldItem> responseItem = returnSoldItem(soldItem);
        if(!responseItem.getSuccess())
            return responseItem;

        return addSellings(Stream.of(soldItem).collect(Collectors.toList()));
    }

    public ResponseItem applyInventoryResults(ComingItemFilter filter, Long buyerId) {

        List<ComingItem> inputComings = comingItemHandler.findByFilter(filter).getEntityItems();

        List<SoldItem> sellings = new ArrayList<SoldItem>();
        List<ComingItem> comings = new ArrayList<ComingItem>();

        Buyer buyer = buyerHandler.getBuyerById(buyerId);
        if(buyer == null)
            return new ResponseItem(BUYER_FOR_INVENTORY_NOT_FOUND, false);

        Document document;

        ResponseItem<Document> documentResponseItem = comingItemHandler.getDocForInventory();
        if(documentResponseItem.getSuccess())
            document = documentResponseItem.getEntityItem();
        else
            return documentResponseItem;

        inputComings.forEach(coming -> {
            if(coming.getQuantity().compareTo(coming.getCurrentQuantity()) > 0) {
                sellings.add(new SoldItem(
                        coming,
                        coming.getPriceOut(),
                        coming.getStock().getOrganization().getVatValue(),
                        coming.getQuantity().subtract(coming.getCurrentQuantity()),
                        INVENTORY_SHORTAGE_DETECTED,
                        buyer,
                        userHandler.getCurrentUser()
                ));

            }

            if(coming.getCurrentQuantity().compareTo(coming.getQuantity()) > 0) {
                comings.add(new ComingItem(
                        coming.getPriceIn(),
                        coming.getPriceOut(),
                        coming.getCurrentQuantity().subtract(coming.getQuantity()),
                        new ArrayList<Comment>() {{
                                add(new Comment(
                                    "",
                                    userHandler.getCurrentUser().getFullName(),
                                        INVENTORY_SURPLUS_DETECTED,
                                    new Date(),
                                    coming.getCurrentQuantity().subtract(coming.getQuantity())
                                )
                            );
                        }}
                        ,
                        document,
                        coming.getItem(),
                        coming.getStock(),
                        userHandler.getCurrentUser()
                ));

            }

            System.out.println(coming.getItem().getName());
        });

        if(sellings.size() > 0)
            addSellings(sellings);

        if(comings.size() > 0)
            comings.forEach(comingItemHandler::addItem);

        return new ResponseItem(INVENTORY_DONE, true);
    }

}

