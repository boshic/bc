package barcode.dao.services;

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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SoldItemHandler extends EntityHandlerImpl {

//    public static QSoldItem qSoldItem = QSoldItem.soldItem;

    public static SoldItemPredicatesBuilder sipb = new SoldItemPredicatesBuilder();

    private SoldItemsRepository soldItemsRepository;

    private ComingItemHandler comingItemHandler;

    private UserHandler userHandler;

    private StockHandler stockHandler;

    private BuyerHandler buyerHandler;

    private RecipeHandler recipeHandler;

    public SoldItemHandler (SoldItemsRepository soldItemsRepository, ComingItemHandler comingItemHandler,
                            UserHandler userHandler, StockHandler stockHandler, BuyerHandler buyerHandler,
                            RecipeHandler recipeHandler) {

        this.soldItemsRepository = soldItemsRepository;

        this.comingItemHandler = comingItemHandler;

        this.userHandler = userHandler;

        this.stockHandler = stockHandler;

        this.buyerHandler = buyerHandler;

        this.recipeHandler = recipeHandler;
    }

    public ResponseItem makeAutoSelling(Set<SoldItem> sellings) {

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
        ResponseItem responseByBuyer = new ResponseItem("Покупатель " + selling.getBuyer().getName() + " найден");

        Buyer buyer = this.buyerHandler.getBuyerByName(selling.getBuyer().getName());

        if (buyer == null) {

            buyer = this.buyerHandler.addBuyer(selling.getBuyer()).getEntityItem();

            responseByBuyer.setText("Покупатель " + selling.getBuyer().getName() + " создан");
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
        if(soldItem.getBuyer().getSellByComingPrices())
            return priceIn;
        return soldItem.getPrice();
    }

    void makeSellingByInvoice(List<SoldItem> sellings) {

        // get Sellings by comment with a specified type, buyer, and stock
        // compare by quantity
        // correcting incoming sellings by quantity
        // addSellings

    }

    public synchronized ResponseItem addSellings (Set<SoldItem> soldItems) {

        ResponseItem<SoldItem> responseItem = new ResponseItem<SoldItem>("",
                new ArrayList<SoldItem>(),false);

        Long uuid = new Random().nextLong();

        Recipe recipe = new Recipe(new Date(),
                soldItems.stream().map(v -> v.getPrice().multiply(v.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add),
                soldItems.size(), soldItems.iterator().next().getUser(), soldItems.iterator().next().getBuyer()
        );

        recipeHandler.save(recipe);

        for (SoldItem soldItem : soldItems) {

            List<ComingItem> comings = this.comingItemHandler.getComingItemByIdAndStockId(
                    soldItem.getComing().getItem().getId(),
                    soldItem.getComing().getStock().getId());

            BigDecimal reqForSell = BigDecimal.ZERO;
            BigDecimal availQuantityByEan =
                    comings
                            .stream().map(ComingItem::getCurrentQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .subtract(soldItem.getQuantity());

            for (ComingItem coming: comings) {

                BigDecimal availQuant = coming.getCurrentQuantity();

                reqForSell = soldItem.getQuantity();

                if (availQuant.compareTo(BigDecimal.ZERO) > 0) { // проверяем на остаток по приходу

                    SoldItem newSoldItem = new SoldItem();
                    newSoldItem.setComments(new ArrayList<>());

                    if(soldItem.getComment() != null)
                        newSoldItem.setComment(
                            this.buildComment(newSoldItem.getComments(), soldItem.getComment(),
                                userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                                "Продажа")
                        );

                    newSoldItem.setAvailQuantityByEan(availQuantityByEan);

                    newSoldItem.setQuantityBeforeSelling(availQuant); // устанавливаем количество до продажи

                    newSoldItem.setBuyer(soldItem.getBuyer());

                    newSoldItem.setUser(soldItem.getUser());

                    newSoldItem.setDate(new Date());

                    Integer discount = soldItem.getBuyer().getDiscount();

                    if(discount !=null && discount > 0)

                        newSoldItem.setComment(
                                this.buildComment(newSoldItem.getComments(), "",
                                        userHandler.getCurrentUser().getFullName(),
                                        "применена скидка " + discount + "%.")
                        );

                    responseItem.setSuccess(true);

                        if (reqForSell.compareTo(availQuant) > 0) {

                            coming.setCurrentQuantity(BigDecimal.ZERO);

                            soldItem.setQuantity(reqForSell.subtract( availQuant));

                            newSoldItem.setQuantity(availQuant);

                        } else {

                            coming.setCurrentQuantity(availQuant.subtract(reqForSell));

                            newSoldItem.setQuantity(reqForSell);

                            reqForSell = BigDecimal.ZERO;
                        }


                        coming.setSum(coming.getPriceIn().multiply(coming.getCurrentQuantity())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));

                        coming.setLastChangeDate(newSoldItem.getDate());

                        newSoldItem.setComing(coming); // - добавляем ссылку на приход новой продажи

                        newSoldItem.setPrice(getPriceOfSoldItem(soldItem, coming.getPriceIn()));

                        newSoldItem.setSum(newSoldItem.getPrice()
                                .multiply(newSoldItem.getQuantity())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));

                        newSoldItem.setVat(soldItem.getVat());

//                        newSoldItem.setUuid(uuid);

                        newSoldItem.setRecipe(recipe);

                        soldItemsRepository.save(newSoldItem);

                        responseItem.getEntityItems().add(newSoldItem);

                    if (reqForSell.compareTo(BigDecimal.ZERO) == 0) break;

                }
            }

            if(reqForSell.compareTo(BigDecimal.ZERO) > 0)
                return new ResponseItem("увы продано до Вас... с другой кассы, не хватает - " + reqForSell, false);

        }

        return new ResponseItem("Продажа завершена успешно", true);
    }

    public static void SortSoldItemsList(List<SoldItem> soldItems, String field, String direction) {

        SoldItemFilter.GroupedSoldItemSortingStrategies strategy
                = SoldItemFilter.GroupedSoldItemSortingStrategies.valueOf(field.replace(".","_").toUpperCase());
        strategy.sort(soldItems);

        if(direction.equalsIgnoreCase(BasicFilter.SORT_DIRECTION_DESC))
            Collections.reverse(soldItems);
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


        SortSoldItemsList(result, filter.getSortField(), filter.getSortDirection());

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

//        SoldItemPredicatesBuilder sipb = new SoldItemPredicatesBuilder();

        comingItemHandler.checkEanInFilter(filter);

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());

        if(filter.getGroupByItems())
            return groupByItems(soldItemsRepository.findAll(sipb.buildByFilter(filter), sort), filter);

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<SoldItem> page =  soldItemsRepository.findAll(sipb.buildByFilter(filter), pageRequest);

        List<SoldItem> result = page.getContent();

        if (result.size() > 0) {

            ResponseBySoldItems ribysi =
                    new ResponseBySoldItems("найдены элементы", result, true, page.getTotalPages());

            if(filter.getCalcTotal())
                ribysi.calcTotals(soldItemsRepository.findAll(sipb.buildByFilter(filter)));

            return ribysi;
        }

        return new ResponseBySoldItems("ничего не найдено", new ArrayList<>(), false, 0);
    }

    public synchronized ResponseItem<SoldItem> changeDate(SoldItem soldItem) {

        SoldItem changedSoldItem = soldItemsRepository.findOne(soldItem.getId());

        changedSoldItem.setComment(
                this.buildComment(changedSoldItem.getComments(), "",
                        userHandler.getCurrentUser().getFullName(),
                        "Изменена дата")
        );

        changedSoldItem.setDate(soldItem.getDate());

        soldItemsRepository.save(changedSoldItem);

        return null;
    }

    public synchronized ResponseItem returnSoldItem(SoldItem soldItem) {

        SoldItem newSoldItem = soldItemsRepository.findOne(soldItem.getId());

        ComingItem comingItem = comingItemHandler.getComingItemById(soldItem.getComing().getId());

        comingItem.setLastChangeDate(new Date());

        comingItem.setCurrentQuantity(comingItem.getCurrentQuantity().add(soldItem.getQuantity()));

        comingItem.setSum(comingItem.getPriceIn().multiply(comingItem.getCurrentQuantity())
                .setScale(2, BigDecimal.ROUND_HALF_UP));

        newSoldItem.setQuantity(newSoldItem.getQuantity().subtract(soldItem.getQuantity()));

        newSoldItem.setSum(newSoldItem.getPrice().multiply(newSoldItem.getQuantity())
                        .setScale(2, BigDecimal.ROUND_HALF_UP));

        newSoldItem.setAvailQuantityByEan(newSoldItem.getAvailQuantityByEan().add(soldItem.getQuantity()));

        newSoldItem.setComment(
                this.buildComment(newSoldItem.getComments(), soldItem.getQuantity() + " ед. " + soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                        "Возврат")
        );

        soldItemsRepository.save(newSoldItem);

        return null;
    }

    public synchronized ResponseItem addOneSelling(SoldItem soldItem) {

        soldItem.setAvailQuantityByEan(comingItemHandler
                .getComingItemByIdAndStockId(
                        soldItem.getComing().getItem().getId(),
                        soldItem.getComing().getStock().getId())
                .stream().map(ComingItem::getCurrentQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(soldItem.getQuantity())
        );

        ComingItem comingItem = comingItemHandler.getComingItemById(soldItem.getComing().getId());

        soldItem.setQuantityBeforeSelling(comingItem.getCurrentQuantity());

        comingItem.setCurrentQuantity(comingItem.getCurrentQuantity().subtract(soldItem.getQuantity()));

        comingItem.setLastChangeDate(new Date());

        soldItem.setDate(new Date());

        soldItem.setComments(new ArrayList<>());
        soldItem.setComment(
                this.buildComment(soldItem.getComments(), soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                        "Продажа")
        );

        soldItem.setPrice(getPriceOfSoldItem(soldItem, comingItem.getPriceIn()));

        comingItem.setSum(comingItem.getPriceIn().multiply(comingItem.getCurrentQuantity())
                .setScale(2, BigDecimal.ROUND_HALF_UP));

        soldItem.setSum(soldItem.getPrice().multiply(soldItem.getQuantity())
                .setScale(2, BigDecimal.ROUND_HALF_UP));

        Recipe recipe = new Recipe(new Date(), soldItem.getSum(), 1, soldItem.getUser(), soldItem.getBuyer());
        recipeHandler.save(recipe);

        soldItem.setRecipe(recipe);

        soldItemsRepository.save(soldItem);

        return new ResponseItem("Продано", true);
    }

    public void addComment(Comment comment, Long id) {
        if(id != null) {
            SoldItem soldItem = soldItemsRepository.findOne(id);
            soldItem.setComment(
                    this.buildComment(soldItem.getComments(), comment.getText(),
                            userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                            comment.getAction())
            );
            soldItemsRepository.save(soldItem);
        }
    }

}

