package barcode.dao.services;

import barcode.api.EntityHandler;
import barcode.dao.entities.embeddable.ItemComponent;
import barcode.utils.ComingItemFilter;
import barcode.enums.CommentAction;
import barcode.utils.CommonUtils;
import barcode.utils.SortingField;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import barcode.dao.entities.*;
import barcode.dao.entities.embeddable.Comment;
import barcode.dao.predicates.SoldItemPredicatesBuilder;
import barcode.dao.repositories.SoldItemsRepository;
import barcode.utils.SoldItemFilter;
import barcode.dto.ResponseBySoldItems;
import barcode.dto.ResponseItem;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.querydsl.core.types.dsl.Expressions.stringPath;

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
    private AbstractEntityManager abstractEntityManager;


    public SoldItemHandler (SoldItemsRepository soldItemsRepository,
                            SoldCompositeItemHandler soldCompositeItemHandler,
                            ComingItemHandler comingItemHandler,
                            UserHandler userHandler,
                            StockHandler stockHandler,
                            BuyerHandler buyerHandler,
                            ReceiptHandler receiptHandler,
                            ItemHandler itemHandler,
                            AbstractEntityManager abstractEntityManager) {

        this.soldItemsRepository = soldItemsRepository;
        this.comingItemHandler = comingItemHandler;
        this.userHandler = userHandler;
        this.stockHandler = stockHandler;
        this.buyerHandler = buyerHandler;
        this.receiptHandler = receiptHandler;
        this.itemHandler = itemHandler;
        this.soldCompositeItemHandler = soldCompositeItemHandler;
        this.abstractEntityManager = abstractEntityManager;
    }

    public ResponseItem makeAutoSelling(List<SoldItem> sellings) {

        ResponseItem<ResponseItem> responseItem =
                new ResponseItem<ResponseItem>("Обработка автоматических продаж",
                    new ArrayList<ResponseItem>(), true);

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
        ResponseItem<ComingItem>
            responseByComing = this
            .comingItemHandler.getComingForSell(
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
        return soldItem.getBuyer().getSellByComingPrices() ? priceIn : soldItem.getPrice();
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

               List<ItemComponent> components = soldItem.getComing().getItem().getComponents();
               components.sort(Comparator.comparing(ItemComponent::getQuantity).reversed());
               components.forEach(component -> {
                            SoldItem cmpntSoldItem = new SoldItem(
                                    new ComingItem(component.getItem(),soldItem.getComing().getStock()),
                                    getComponentPrice(component, soldItem.getPrice(), components),
                                    soldItem.getComing().getStock().getOrganization().getVatValue(),
                                    soldItem.getQuantity().multiply(component.getQuantity()),
                                    soldItem.getComment(),
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

    private BigDecimal getComponentPrice (ItemComponent component, BigDecimal price, List<ItemComponent> components) {

        if(component.getPrice().compareTo(BigDecimal.ZERO) > 0)
            return component.getPrice();

        BigDecimal componentsQuantity = components.stream()
            .map(ItemComponent::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int componentNumber = 0;

        for(ItemComponent c : components) {
            componentNumber += 1;
           if (c.getItem().getId().equals(component.getItem().getId()))
               break;
        };

        BigDecimal componentPrice = price.divide(componentsQuantity, 2, RoundingMode.CEILING);

        if(componentPrice.multiply(componentsQuantity).compareTo(price) != 0
            && componentNumber == components.size())
            return price
                .subtract(componentPrice
                    .multiply(componentsQuantity
                        .subtract(component.getQuantity())))
                .divide(component.getQuantity(), 2, RoundingMode.DOWN);

        return componentPrice;
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
                BigDecimal availQuantityByEanAfterSell = availQuantityByEan.subtract(reqForSell);

                if(reqForSell.compareTo(availQuantityByEan) > 0  || comings.size() == 0)
                    return new ResponseItem<>(getInsufficientQuantityOfGoodsMessage(
                                    reqForSell, availQuantityByEan, soldItem.getComing().getItem().getName()
                    ),
                            false);

//                availQuantityByEan = availQuantityByEan.subtract(reqForSell);

                for (ComingItem coming: comings) {
                    BigDecimal availQuantByComing = coming.getCurrentQuantity();
                    if (availQuantByComing.compareTo(BigDecimal.ZERO) > 0) { // проверяем на остаток по приходу

                        SoldItem newSoldItem = new SoldItem();
                        newSoldItem.setComments(new ArrayList<>());

                        if(soldItem.getComment() != null)
                            newSoldItem.setComment(
                                    this.buildComment(newSoldItem.getComments(),
                                            getQuantityForComment(
                                                    (reqForSell.compareTo(availQuantByComing) > 0) ? availQuantByComing : reqForSell)
                                                    + soldItem.getComment(),
                                            userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                                            CommentAction.SALE_COMMENT.getAction(), newSoldItem.getQuantity())
                            );

                        newSoldItem.setAvailQuantityByEan(
                                availQuantityByEanAfterSell.compareTo(BigDecimal.ZERO) > 0
                                    ? availQuantityByEanAfterSell : BigDecimal.ZERO);

                        newSoldItem.setQuantityBeforeSelling(availQuantByComing); // устанавливаем количество до продажи
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

                        newSoldItem.setMayBeError(
                            (soldItem.getMayBeError() == null) ?
                                !comingItemHandler
                                    .checkComingCurrentQuantity(availQuantityByEan, soldItemsRepository
                                        .findTopDateByComingItemEanAndComingStockIdOrderByIdDesc(
                                            coming.getItem().getEan(),coming.getStock().getId()))
                                : soldItem.getMayBeError());

                        coming.setComment(
                            this.buildComment(coming.getComments(),
                                soldItem.getComment() + " для " + soldItem.getBuyer().getName()
                                    + getQuantityForComment((reqForSell.compareTo(availQuantByComing) > 0) ? availQuantByComing : reqForSell),
                                userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                                CommentAction.SALE_COMMENT.getAction(), coming.getCurrentQuantity())
                        );

                        if (reqForSell.compareTo(availQuantByComing) > 0) {

                            coming.setCurrentQuantity(BigDecimal.ZERO);
                            soldItem.setQuantity(reqForSell.subtract( availQuantByComing));
                            newSoldItem.setQuantity(availQuantByComing);
                            availQuantityByEan = availQuantityByEan.subtract(availQuantByComing);
                            newSoldItem.setAvailQuantityByEan(availQuantityByEan);
                            reqForSell = soldItem.getQuantity();

                        } else {

                            coming.setCurrentQuantity(availQuantByComing.subtract(reqForSell));
                            newSoldItem.setQuantity(reqForSell);
                            reqForSell = BigDecimal.ZERO;
                        }

                        coming.setSum(
                            coming.getPriceIn()
                                .multiply(coming.getCurrentQuantity())
                                .setScale(2, BigDecimal.ROUND_HALF_UP));

                        coming.setLastChangeDate(newSoldItem.getDate());

                        newSoldItem.setComing(coming); // - добавляем ссылку на приход новой продажи

                        soldCompositeItemHandler.save(soldItem.getSoldCompositeItem());

                        newSoldItem.setSoldCompositeItem(soldItem.getSoldCompositeItem());
                        newSoldItem.setPrice(getPriceOfSoldItem(soldItem, coming.getPriceIn()));
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


    private Predicate buildAvailQuantByEanPredicate( QComingItem coming,
                                                     QComingItem comingItem,
                                                     SoldItemFilter filter) {
        if (filter.getGroupByItems())
        return
            filter.getStock().isAllowAll() ?
            coming.item.id.eq(comingItem.item.id) :
            coming.item.id.eq(comingItem.item.id).and(comingItem.stock.id.eq(filter.getStock().getId()));
        else
        return
            filter.getStock().isAllowAll() ? coming.item.section.id.eq(comingItem.item.section.id)
                : coming.item.section.id.eq(comingItem.item.section.id).and(comingItem.stock.id.eq(filter.getStock().getId()));
    }

    private JPAQuery<Tuple> getQueryForGroupedItems(SoldItemFilter filter,
                                                    PageRequest pageRequest,
                                                    QComingItem coming,
                                                    QComingItem comingItem,
                                                    QSoldItem soldItem) {

        BooleanBuilder predicate = sipb.buildByFilter(filter);
        EntityManager em = abstractEntityManager.getEntityManager();
        OrderSpecifier orderSpecifier = filter.getOrderSpec(filter.getSortField(),
            filter.getSortDirection(), soldItem, QSoldItem.class);

        JPAQuery<BigDecimal> incomeQuery =  new JPAQuery<BigDecimal>(em).from(soldItem).where(predicate);
        BigDecimal totalIncome = CommonUtils.validateBigDecimal(
            incomeQuery
                .select(soldItem.sum.sum().subtract(soldItem.coming.priceIn.multiply(soldItem.quantity).sum()))
                .fetchOne()
        );

        BigDecimal excludeFromIncome = CommonUtils.validateBigDecimal(
            incomeQuery
                .select(soldItem.coming.priceIn.multiply(soldItem.quantity).sum())
                .where(soldItem.buyer.excludeExpensesFromIncome.isTrue()).fetchOne()
        );

        Predicate availQuantityByEanPredicate =
            buildAvailQuantByEanPredicate(coming, comingItem, filter);

        if (filter.getGroupByItems())
            return new JPAQuery<Tuple>(em)
            .select(
                coming.item,
                soldItem.quantity.sum()
                    .as(SoldItemFilter.SortingFieldsForGroupedByItemSoldItems.QUANTITY.getValue()),
                soldItem.price.max()
                    .as(SoldItemFilter.SortingFieldsForGroupedByItemSoldItems.PRICE.getValue()),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(comingItem.currentQuantity.sum()).from(comingItem)
                        .where(availQuantityByEanPredicate),
                    SoldItemFilter.SortingFieldsForGroupedByItemSoldItems.AVAILQUANTITYBYEAN.getValue()
                ),
                soldItem.sum.subtract(sipb.getSumForExcludeFromIncome(soldItem)).sum()
                    .as(SoldItemFilter.SortingFieldsForGroupedByItemSoldItems.INCOMESUM.getValue()),
                soldItem.sum.subtract(sipb.getSumForExcludeFromIncome(soldItem))
                    .sum()
                    .divide(totalIncome.subtract(excludeFromIncome))
                    .as(SoldItemFilter.SortingFieldsForGroupedByItemSoldItems.INCOMESUMPERCENT.getValue())
            )
            .from(soldItem).where(predicate)
            .groupBy(coming.item)
            .orderBy(orderSpecifier)
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize());
        else
            return new JPAQuery<Tuple>(em)
                .select(
                    coming.item.section,
                    soldItem.quantity.sum()
                        .as(SoldItemFilter.SortingFieldsForGroupedBySectionSoldItems.QUANTITY.getValue()),
                    soldItem.sum.sum()
                        .as(SoldItemFilter.SortingFieldsForGroupedBySectionSoldItems.SUMM.getValue()),
                    ExpressionUtils.as(
                        JPAExpressions
                            .select(comingItem.currentQuantity.sum()).from(comingItem)
                            .where(availQuantityByEanPredicate),
                        SoldItemFilter.SortingFieldsForGroupedBySectionSoldItems.AVAILQUANTITYBYEAN.getValue()
                    ),
                    soldItem.sum.subtract(sipb.getSumForExcludeFromIncome(soldItem)).sum()
                        .as(SoldItemFilter.SortingFieldsForGroupedBySectionSoldItems.INCOMESUM.getValue()),
                    soldItem.sum.subtract(sipb.getSumForExcludeFromIncome(soldItem))
                        .sum()
                        .divide(totalIncome.subtract(excludeFromIncome))
                        .as(SoldItemFilter.SortingFieldsForGroupedBySectionSoldItems.INCOMESUMPERCENT.getValue())
                )
                .from(soldItem).where(predicate)
                .groupBy(coming.item.section)
                .orderBy(orderSpecifier)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());
    }

    private List<SoldItem> getDataByGroupingQuery(JPAQuery<Tuple> query,
                                                  SoldItemFilter filter,
                                                  QComingItem coming) {

        List<SoldItem> result = new ArrayList<>();
        if (filter.getGroupByItems())
        query.fetch().forEach(item -> {
            result.add(
                new SoldItem(
                    new ComingItem(item.get(coming.item), filter.getStock()),
                    CommonUtils.validateBigDecimal(item.get(2, BigDecimal.class)),
                    CommonUtils.validateBigDecimal(item.get(1, BigDecimal.class)),
                    CommonUtils.validateBigDecimal(item.get(3, BigDecimal.class)),
                    CommonUtils.validateBigDecimal(item.get(4, BigDecimal.class)),
                    CommonUtils.validateBigDecimal(item.get(5, BigDecimal.class))
                )
            );
        });
        else {
            query.fetch().forEach(item -> {
                    result.add(
                    new SoldItem(
                        CommonUtils.validateBigDecimal(item.get(2, BigDecimal.class)),
                        new ComingItem(
                            new Item(CommonUtils.validateItemSection(item.get(coming.item.section))), filter.getStock()),
                        CommonUtils.validateBigDecimal(item.get(1, BigDecimal.class)),
                        CommonUtils.validateBigDecimal(item.get(3, BigDecimal.class)),
                        CommonUtils.validateBigDecimal(item.get(4, BigDecimal.class)),
                        CommonUtils.validateBigDecimal(item.get(5, BigDecimal.class))
                    )
                );
            });
        }

        return result;
    }

    private ResponseBySoldItems groupByItemsNew(
        SoldItemFilter filter) {

        QSoldItem soldItem = QSoldItem.soldItem;
        QComingItem coming = QSoldItem.soldItem.coming;
        QComingItem comingItem = QComingItem.comingItem;

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage());

        filter.validateFilterSortField(filter.getDefSortingField());

        JPAQuery<Tuple> query =
            getQueryForGroupedItems(filter, pageRequest, coming, comingItem, soldItem);

        return getResults(new PageImpl<SoldItem>(
            getDataByGroupingQuery(query, filter, coming),
            pageRequest, query.fetchCount()), filter);
    }

    private ResponseBySoldItems
    getResults(Page<SoldItem> page, SoldItemFilter filter) {

        ResponseBySoldItems response =
            new ResponseBySoldItems(ELEMENTS_FOUND, page.getContent(), true, page.getTotalPages());

        if(checkResponse(response.getEntityItems().size(), response))
            calcTotals(filter, abstractEntityManager, response);

        return response;
    }

    public ResponseBySoldItems findByFilter(SoldItemFilter filter) {

        itemHandler.checkEanInFilter(filter);

        if(filter.getGroupByItems() || filter.getGroupBySections())
            return groupByItemsNew(filter);


        filter.validateFilterSortField(filter.getDefSortingField());
        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());
        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);
        Page<SoldItem> page =  soldItemsRepository.findAll(sipb.buildByFilter(filter), pageRequest);

        return getResults(page, filter);
    }

    public ResponseItem<SoldItem> changeDate(SoldItem soldItem) {

        this.changeDate(soldItem.getId(), soldItem.getDate(),
                soldItemsRepository, userHandler.getCurrentUser().getFullName());

        return null;
    }

    public ResponseItem<SoldItem> addDeletedSelings (List<SoldItem> soldItems) {

        ResponseItem<SoldItem> checkedItems = checkSoldItemsForComposites(soldItems);
        if (!checkedItems.getSuccess())
            return checkedItems;

        for (SoldItem soldItem : checkedItems.getEntityItems()) {
            List<ComingItem> comings = this.comingItemHandler.getComingItemByIdAndStockId(
                soldItem.getComing().getItem().getId(),
                soldItem.getComing().getStock().getId());

            if (comings.size() == 0)
                return null;

            BigDecimal availQuantityByEan = comingItemHandler.getAvailQuantityByEan(comings);

            SoldItem newSoldItem = new SoldItem();
            newSoldItem.setComments(new ArrayList<>());

            if(soldItem.getComment() != null)
                newSoldItem.setComment(
                    this.buildComment(newSoldItem.getComments(),
                        soldItem.getQuantity()+ "ед. " + soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                        CommentAction.DELETED_SALE_COMMENT.getAction(), newSoldItem.getQuantity())
                );

            newSoldItem.setAvailQuantityByEan(availQuantityByEan);
            newSoldItem.setQuantityBeforeSelling(BigDecimal.ZERO);
            newSoldItem.setBuyer(soldItem.getBuyer());
            newSoldItem.setUser(soldItem.getUser());
            newSoldItem.setDate(new Date());
            newSoldItem.setMayBeError(false);
            newSoldItem.setQuantity(BigDecimal.ZERO);
            newSoldItem.setComing(comings.get(0));

            newSoldItem.setPrice(getPriceOfSoldItem(soldItem, comings.get(0).getPriceIn()));
            newSoldItem.setSum(
                newSoldItem.getPrice()
                    .multiply(newSoldItem.getQuantity())
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
            );
            newSoldItem.setVat(soldItem.getVat());

            soldItemsRepository.save(newSoldItem);



        }

        return  null;
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
                    CommentAction.RETURN_COMMENT.getAction(), comingItem.getCurrentQuantity()));

        newSoldItem.setQuantity(newSoldItem.getQuantity().subtract(soldItem.getQuantity()));
        newSoldItem.setSum(newSoldItem.getPrice().multiply(newSoldItem.getQuantity())
                        .setScale(2, BigDecimal.ROUND_HALF_UP));
        newSoldItem.setAvailQuantityByEan(newSoldItem.getAvailQuantityByEan().add(soldItem.getQuantity()));
        newSoldItem.setComment(
                this.buildComment(newSoldItem.getComments(),
                        getQuantityForComment(soldItem.getQuantity()) + soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                    CommentAction.RETURN_COMMENT.getAction(), newSoldItem.getQuantity()));

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
                    CommentAction.SALE_COMMENT.getAction(), comingItem.getCurrentQuantity())
        );

        soldItem.setDate(new Date());
        soldItem.setComments(new ArrayList<>());
        soldItem.setComment(
                this.buildComment(soldItem.getComments(), getQuantityForComment(soldItem.getQuantity())
                                + soldItem.getComment(),
                        userHandler.checkUser(soldItem.getUser(), null ).getFullName(),
                    CommentAction.SALE_COMMENT.getAction(), soldItem.getQuantity())
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

        soldItem.setMayBeError(false);
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

            BigDecimal priceOut = comingItemHandler
                .getItemPriceOutByIdAndStock(coming.getItem().getId(), coming.getStock().getId()),
                comingSum = CommonUtils.validateBigDecimal(coming.getSum());


            if(coming.getQuantity().compareTo(coming.getCurrentQuantity()) > 0) {
                sellings.add(new SoldItem(
                        coming,
                        priceOut,
                        coming.getStock().getOrganization().getVatValue(),
                        coming.getQuantity().subtract(coming.getCurrentQuantity()),
                    CommentAction.INVENTORY_SHORTAGE_DETECTED.getAction(),
                        buyer,
                        userHandler.getCurrentUser()
                ));

            }

            if(coming.getCurrentQuantity().compareTo(coming.getQuantity()) > 0) {
                comings.add(new ComingItem(
                        comingSum.compareTo(BigDecimal.ZERO) == 0 ?
                            comingItemHandler.getPriceInMaxForComing(coming.getItem().getId()):
                            comingSum.divide(coming.getQuantity(), 2, BigDecimal.ROUND_CEILING),
                        priceOut,
                        coming.getCurrentQuantity().subtract(coming.getQuantity()),
                        new ArrayList<Comment>() {{
                                add(new Comment(
                                    "",
                                        userHandler.getCurrentUser().getFullName(),
                                    CommentAction.INVENTORY_SURPLUS_DETECTED.getAction(),
                                        generateCommentSearchString(CommentAction.INVENTORY_SURPLUS_DETECTED.getAction(), "", userHandler.getCurrentUser().getFullName(), new Date()),
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

