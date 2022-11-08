package barcode.dao.services;

import barcode.dao.entities.embeddable.InventoryRow;
import barcode.dao.entities.embeddable.InvoiceRow;
import barcode.dao.entities.embeddable.QInventoryRow;
import barcode.dto.*;
import barcode.enums.CommentAction;
import barcode.enums.SystemMessage;
import barcode.utils.CommonUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import barcode.dao.entities.*;
import barcode.dao.entities.embeddable.Comment;
import barcode.dao.predicates.ComingItemPredicatesBuilder;
import barcode.dao.repositories.ComingItemRepository;
import barcode.utils.BasicFilter;
import barcode.utils.ComingItemFilter;
import barcode.utils.DocumentFilter;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.ApplicationContext;
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

@Service
public class ComingItemHandler extends EntityHandlerImpl {

    public static ComingItemPredicatesBuilder cipb = new ComingItemPredicatesBuilder();

    private BasicFilter filter;

    private ComingItemRepository comingItemRepository;
    private ItemHandler itemHandler;
    private UserHandler userHandler;
    private AbstractEntityManager abstractEntityManager;
    private ApplicationContext context;

    public ComingItemHandler(ComingItemRepository comingItemRepository,
                             ItemHandler itemHandler,
                             UserHandler userHandler,
                             ApplicationContext context,
                             AbstractEntityManager abstractEntityManager) {
        super(context);
        this.comingItemRepository = comingItemRepository;
        this.itemHandler = itemHandler;
        this.userHandler = userHandler;
        this.abstractEntityManager = abstractEntityManager;
    }

    public void saveComingItem(ComingItem comingItem) { comingItemRepository.save(comingItem);}

    private ResponseItem<ComingItem> update(ComingItem srcComing, ComingItem coming) {

        ResponseItem<ComingItem> responseItem = new ResponseItem<ComingItem>("", false);

            if(isEditingDisallowed(coming))
                return new ResponseItem<>(TRY_TO_CHANGE_SOLD_COMING_ERROR, false);

            coming.setPriceIn(srcComing.getPriceIn());
            coming.setPriceOut(srcComing.getPriceOut());
            coming.setQuantity(srcComing.getQuantity());
            coming.setSum(srcComing.getSum() == null ?
                (srcComing.getPriceIn().multiply(srcComing.getQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP) : srcComing.getSum());

            coming.setCurrentQuantity(coming.getQuantity());
            coming.setLastChangeDate(new Date());
            coming.setDoc(srcComing.getDoc());
            coming.setItem(srcComing.getItem());
            coming.setStock(srcComing.getStock());

            User checkedUser = userHandler.checkUser(srcComing.getUser(), AUTO_COMING_MAKER);
            responseItem.setText(SystemMessage.CHANGING_OF_COMING.getMessage() + srcComing.getItem().getName() + NUMBER + srcComing.getId());

            if (coming.getId() == null) {

                coming.setComments(srcComing.getComments() == null? new ArrayList<Comment>() : srcComing.getComments());

                responseItem.setText(
                    SystemMessage.MAKING_OF_COMING.getMessage() + srcComing.getItem().getName() + NUMBER + srcComing.getId());

                coming.setDate(new Date());
                coming.setComment(
                    this.buildComment(coming.getComments(),
                        srcComing.getStock().getName() + getQuantityForComment(srcComing.getQuantity()),
                        checkedUser.getFullName(),
                        CommentAction.MAKING_OF_COMING.getAction(), coming.getCurrentQuantity()));
            } else {

                coming.setComment(
                    this.buildComment(coming.getComments(), "",
                        checkedUser.getFullName(),
                        CommentAction.CHANGING_OF_COMING.getAction(), coming.getCurrentQuantity()));
            }

            if(coming.getItem().getComponents() != null && coming.getItem().getComponents().size() > 0)

                return new ResponseItem<>(ITEM_IS_COMPOSITE_ERROR + coming.getItem().getName(), false, coming);


            if (checkedUser != null && checkedUser.getRole().equals(ROLE_ADMIN)) {
                coming.setUser(checkedUser);
                comingItemRepository.save(coming);
                responseItem.setSuccess(true);
                responseItem.setEntityItem(coming);
            } else {
                responseItem.setText(CHANGING_DENIED);
                responseItem.setEntityItem(srcComing);
            }

        return responseItem;
    }

    private Boolean isEditingDisallowed(ComingItem comingItem) {

        return !(CommonUtils.validateBigDecimal(comingItem.getQuantity())
            .compareTo(CommonUtils.validateBigDecimal(comingItem.getCurrentQuantity())) == 0);
    }

    public ResponseItem addItem(ComingItem newComing) {

        return update(newComing, new ComingItem());
    }

    public ResponseItem updateItem(ComingItem updComing) {
        synchronized (this) {
            return update(updComing, this.getComingItemById(updComing.getId()));
        }
    }

    public ComingItem getComingItemById(Long id) {
            return comingItemRepository.findOne(id);
    }

    public List<ComingItem> getComingItemByIdAndStockId(Long itemId, Long stockId) {

        return comingItemRepository
            .findAll(cipb.getAvailableItemsByStock(itemId,
                getStockHandler().getStockById(stockId)),
                new Sort(Sort.Direction.ASC, "doc.date"));
    }

    public synchronized ResponseItem<ComingItem> deleteItem(long id) {

        ComingItem comingItem = this.getComingItemById(id);

        if(isEditingDisallowed(comingItem))
            return new ResponseItem<>(TRY_TO_CHANGE_SOLD_COMING_ERROR, false);

        User checkedUser = userHandler.checkUser(userHandler.getCurrentUser(), null);

        if (checkedUser != null && checkedUser.getRole().equals(ROLE_ADMIN)) {

            comingItem.setComment(
                    this.buildComment(comingItem.getComments(),
                            getQuantityForComment(comingItem.getCurrentQuantity()),
                            checkedUser.getFullName(),
                            CommentAction.SMTH_DELETED.getAction(),
                            comingItem.getCurrentQuantity()));

            comingItem.setCurrentQuantity(BigDecimal.ZERO);
            comingItem.setQuantity(BigDecimal.ZERO);
            comingItem.setSum(BigDecimal.ZERO);
            comingItemRepository.save(comingItem);

            return new ResponseItem<ComingItem>(SystemMessage.SMTH_DELETED.getMessage(), true, comingItem);

        } else
            return new ResponseItem<ComingItem>(DELETING_FAILED, true, comingItem);
    }

    public List<ComingItem> getComingBySpec() {
//        ComingItemSpecification spec =
//                new ComingItemSpecification(new SearchCriteria("supplier", ":", "банк"));
        return null;
//        return comingItemRepository.findAll(spec);
    }

    private void setQuantityAndPriceFromComings(Item item,
                                                     Long stockId,
                                                     ComingItem comingItem) {

        List<ComingItem> comings = getComingItemByIdAndStockId(item.getId(), stockId);

        if ( comings.size() > 0 ) {
            comingItem.setQuantity(item.getPredefinedQuantity());

            for (ComingItem c : comings) {

                if (c.getCurrentQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    comingItem.setCurrentQuantity(comingItem.getCurrentQuantity().add(c.getCurrentQuantity()));
                    if(comingItem.getDoc() == null)
                        comingItem.setDoc(c.getDoc());

                }

                if (c.getPriceOut().compareTo( comingItem.getPriceOut()) > 0)
                    comingItem.setPriceOut(c.getPriceOut());

            }
        }

    }

    private void
    setQuantityAndPriceFromComingsForCompositeItem(Long stockId, ComingItem comingItem) {
        List<ComingItem> componentComings = new ArrayList<>();
        comingItem.getItem().getComponents().forEach(
                (component) -> {

                    ComingItem coming =
                            new ComingItem(component.getItem(), BigDecimal.ZERO, BigDecimal.ZERO);

                    setQuantityAndPriceFromComings(component.getItem(), stockId, coming);

                    coming.setCurrentQuantity(
                            coming.getCurrentQuantity().divide(component.getQuantity(), 0, RoundingMode.DOWN)
                    );

                    componentComings.add(coming);
                }
        );

        comingItem.setCurrentQuantity(
                componentComings.stream()
                        .min(Comparator.comparing(ComingItem::getCurrentQuantity))
                        .orElse(new ComingItem(comingItem.getItem(), BigDecimal.ZERO, BigDecimal.ZERO))
                        .getCurrentQuantity()
        );

    }

    private ResponseItem<ComingItem> getComingForSellSelector(String ean,
                                                              Long stockId,
                                                              Boolean isCompositeAllowed) {

        Item item = itemHandler.getItemByEanSynonym(ean);
        if (item == null)
            return new ResponseItem<ComingItem>(ITEM_NOT_FOUND_WITH_SUCH_EAN + ean, false);

        if (!isCompositeAllowed && item.getComponents() != null && item.getComponents().size() > 0)
            return new ResponseItem<ComingItem>(COMPOSITE_ITEMS_CASTING_IS_NOT_ALLOWED + item.getName(), false);

        ComingItem comingItem = new ComingItem();
        comingItem.setCurrentQuantity(BigDecimal.ZERO);
        comingItem.setPriceOut(new BigDecimal(0));
        comingItem.setItem(item);
        comingItem.setStock(getStockHandler().getStockById(stockId));
        if (comingItem.getItem().getComponents().size() > 0)
            setQuantityAndPriceFromComingsForCompositeItem(stockId, comingItem);
        else
            setQuantityAndPriceFromComings(item, stockId, comingItem);

        if(comingItem.getItem().getPrice().compareTo(BigDecimal.ZERO) > 0)
            comingItem.setPriceOut(comingItem.getItem().getPrice());

        if (comingItem.getCurrentQuantity().compareTo(BigDecimal.ZERO) == 0)
            return new ResponseItem<ComingItem>(item.getName() + NOT_ENOUGH_ITEMS, false);

        return new ResponseItem<ComingItem>(ITEMS_FOUND +
                comingItem.getCurrentQuantity(),true, comingItem);
    }


    public ResponseItem<ComingItem> getComingForSellNonComposite(String ean, Long stockId) {

        return getComingForSellSelector(ean, stockId, false);
    }


    public ResponseItem<ComingItem> getComingForSell(String ean, Long stockId) {

        return getComingForSellSelector(ean, stockId, true);
    }

    private List<ComingItem>
    getGroupedComingsForRelease(List<ComingItem> comingItems) {

        Map<Item, List<ComingItem>> groupedItems = comingItems.stream()
                .collect(Collectors.groupingBy(ComingItem::getItem));

        List<ComingItem> result = new ArrayList<ComingItem>();

        //item, stock, priceIn, currentQuantity, quantity
        groupedItems.forEach((item, comings) -> {
            ComingItem coming = new ComingItem(
                    item,
                    comings.get(0).getStock(),
                    item.getPrice().compareTo(BigDecimal.ZERO) > 0 ? item.getPrice()
                    : comings.stream().max(Comparator.comparing(ComingItem::getPriceOut)).get().getPriceOut(),
                    comings.stream()
                            .map(ComingItem::getCurrentQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add),
                    BigDecimal.ZERO
            );
            result.add(coming);
        });

        return result;
    };

    public ResponseItem<ComingItem> getComingsForReleaseByFilter(ComingItemFilter filter) {

        List<ComingItem> comings =
            (filter.getInvoiceNumber() != null && filter.getInvoiceNumber() > 0 ) ?
            getComingsFromInvoiceByNumber(filter)
            : getGroupedComingsForRelease(comingItemRepository.findAll(cipb.buildByFilterForRelease(filter)));

        if(comings.size() > 0)
            return new ResponseItem<ComingItem>("", comings, true);
        else
            return new ResponseItem<ComingItem>(NOTHING_FOUND, false);
    }

    private List<ComingItem> getComingsFromInvoiceByNumber(ComingItemFilter filter) {

        List<ComingItem> comings = new ArrayList<>();

        Invoice invoice = getInvoiceHandler().getItemById(filter.getInvoiceNumber());
        if(invoice != null) {
            ComingItem comingItem;
            for(InvoiceRow row : invoice.getInvoiceRows())
                {
                    ResponseItem<ComingItem> responseItem
                        = getComingForSellSelector( row.getItem().getEan(), filter.getStock().getId(),true);
                    comingItem = responseItem.getEntityItem();
                    if(comingItem != null)
                    {
                        comingItem.setPriceIn(row.getPrice());
                        comingItem.setQuantity(
                            comingItem.getCurrentQuantity().compareTo(row.getQuantity()) > 0 ?
                                row.getQuantity() : comingItem.getCurrentQuantity()
                        );
                        comings.add(comingItem);
                    }
                }
        }

        return comings;
    }

    public ResponseItem addItems(Set<ComingItem> comings) {

        ResponseItem<ResponseItem> responseItem =
                new ResponseItem<ResponseItem>("Обработка прихода по нескольким позициям",
                                                new ArrayList<ResponseItem>(), true);

        ResponseItem<ResponseItem> responseItemTemp;

        for (ComingItem coming : comings) {

            responseItemTemp = this.checkComing(coming);
            responseItem.getEntityItems().add(responseItemTemp);

            if (responseItemTemp.getSuccess())
                responseItem.getEntityItems().add(update(coming, new ComingItem()));
        }

        responseItem.setEntityItem(buildResponseEntityByAddItems(responseItem.getEntityItems()));

        return responseItem;
    }

    private ResponseItem<ResponseItem> buildResponseEntityByAddItems(List<ResponseItem> responses) {

        for(ResponseItem responseItem : responses)
            if(!responseItem.getSuccess())
                return new ResponseItem<>(responseItem.getText(), false);

        return new ResponseItem<>("Успешно! Приход добавлен по всем позициям", true);
    }


    private ResponseItem<ResponseItem> checkComing(ComingItem coming) {

        ResponseItem<ResponseItem> responseItem = new ResponseItem<ResponseItem>("результат проверки прихода товара "
                                            + coming.getItem().getName(), new ArrayList<ResponseItem>(), false);

        //user
        User user = userHandler.getUserByName(coming.getUser().getName());

        if (user == null)
            return new ResponseItem<>("Пользователь из прихода не найден в БД" , false);

        coming.setUser(user);

        //stock
        Stock stock = getStockHandler().getStockByName(coming.getStock().getName());

        if (stock == null)
            return new ResponseItem<>("Заведите склад со следующим наименованием! - " + coming.getStock().getName(), false);

        coming.setStock(stock);

        //section
        if(coming.getItem().getSection() == null)
            coming.getItem().setSection(new ItemSection(DEFAULT_SECTION_NAME));


        ItemSection section = getItemSectionHandler()
            .getItemByName(coming.getItem().getSection().getName());
        ResponseItem responseBySection = new ResponseItem(CHECK_SECTION_LOG_MESS
                                                    + coming.getItem().getSection().getName() + SMTH_FOUND);
        if(section == null) {
            section = getItemSectionHandler().addItem(coming.getItem().getSection()).getEntityItem();
            responseBySection.setText(CHECK_SECTION_LOG_MESS + coming.getItem().getName() + SMTH_CREATED);
        }
        coming.getItem().setSection(section);
        responseItem.getEntityItems().add(responseBySection);

        //item
        ResponseItem<Item> responseByItem = new ResponseItem<Item>(CHECK_ITEM_LOG_MESS + coming.getItem().getName() + SMTH_FOUND);
        Item item = itemHandler.getItemByEanSynonym(coming.getItem().getEan());
        if (item == null) {

            responseByItem = itemHandler.addItem(coming.getItem());
            if (!responseByItem.getSuccess())
                return new ResponseItem<>(responseByItem.getText(), false);

            item = responseByItem.getEntityItem();

       }
        else {

            item.setSection(section);
            item.setUnit(coming.getItem().getUnit());
            item.setName(coming.getItem().getName());
        }
        coming.setItem(item);

        responseItem.getEntityItems().add(responseByItem);

        //supplier
        ResponseItem responseBySupplier = new ResponseItem("Поставщик " + coming.getDoc().getSupplier().getName()
                                                                                                        + SMTH_FOUND);
        Supplier supplier = getSupplierHandler().getSupplierByName(coming.getDoc().getSupplier().getName());
        if (supplier == null) {

            supplier = getSupplierHandler().addSupplier(coming.getDoc().getSupplier()).getEntityItem();

            responseBySupplier.setText("Поставщик " + coming.getDoc().getSupplier().getName() + SMTH_CREATED);
        }

        coming.getDoc().setSupplier(supplier);

        responseItem.getEntityItems().add(responseBySupplier);

        //doc
        ResponseItem responseBydoc = new ResponseItem("Документ " + coming.getDoc().getName() + " от "
                                                                            + coming.getDoc().getDate() + SMTH_FOUND);

        Document document = getDocumentHandler()
                .findOneDocumentByFilter(new DocumentFilter
                                                (new BasicFilter(
                                                        coming.getDoc().getName(),
                                                        coming.getDoc().getDate(),
                                                        coming.getDoc().getDate()),
                                                        coming.getDoc().getSupplier()));

        if (document == null ) {
            document = getDocumentHandler().addItem(coming.getDoc()).getEntityItem();
            responseBydoc.setText("Документ " + coming.getDoc().getName() + " от "
                                              + coming.getDoc().getDate() + SMTH_CREATED);
        }

        coming.setDoc(document);
        responseItem.getEntityItems().add(responseBydoc);
        QComingItem qComingItem = QComingItem.comingItem;
        Predicate predicate = qComingItem.doc.eq(coming.getDoc())
                                            .and(qComingItem.quantity.gt(0))
                                            .and(qComingItem.item.eq(coming.getItem()))
                                            .and(qComingItem.priceIn.eq(coming.getPriceIn()));

        if (comingItemRepository.findAll(predicate).size() > 0) {

            responseItem.getEntityItems().add(new ResponseItem(CHECK_COMING_INVALID_DOC));

            responseItem.setText(CHECK_COMING_INVALID_DOC);

            return responseItem;
        }

        responseItem.setSuccess(true);

        return responseItem;
    }

    private ResponseByInventory getInventoryItemsNew(
        ComingItemFilter filter) {

        List<ComingItem> result = new ArrayList<>();
        Long stockId = filter.getStock().getId();

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage());

        QComingItem comingItem = QComingItem.comingItem;
        QInventoryRow inventoryRow = QInventoryRow.inventoryRow;

        EntityManager em = abstractEntityManager.getEntityManager();
        BooleanBuilder predicate = cipb.buildByFilter(filter, abstractEntityManager);

        filter.validateFilterSortField(filter.getDefSortingField());

        OrderSpecifier orderSpecifier = filter.getOrderSpec(filter.getSortField(),
            filter.getSortDirection(), comingItem, QComingItem.class);

        JPAQuery<Tuple> query =  new JPAQuery<Tuple>(em)
            .select(
                comingItem.item,
                comingItem.sum.sum().as(ComingItemFilter.SortingFieldsForInventoryRows.SUMM.getValue()),
                comingItem.currentQuantity.sum().as(ComingItemFilter.SortingFieldsForInventoryRows.QUANTITY.getValue()),
                ExpressionUtils.as(
                    cipb.getSubQueryFromInventoryRowsByComingAndStockId(
                            comingItem, inventoryRow, stockId, inventoryRow.quantity),
                    ComingItemFilter.SortingFieldsForInventoryRows.CURRENTQUANTITY.getValue()),
                ExpressionUtils.as(
                    cipb.getSubQueryFromInventoryRowsByComingAndStockId(
                        comingItem, inventoryRow, stockId,
                        inventoryRow.quantity.multiply(cipb.getMaxPriceInForInventoryCase(comingItem)).subtract(comingItem.sum.sum())),
                    ComingItemFilter.SortingFieldsForInventoryRows.INVENTORYSUM.getValue()),
                ExpressionUtils.as(
                    cipb.getSubQueryFromInventoryRowsByComingAndStockId(
                        comingItem, inventoryRow, stockId, inventoryRow.date),
                    ComingItemFilter.SortingFieldsForInventoryRows.LASTINVENTORYCHANGEDATE.getValue())
            )
            .from(comingItem)
            .where(predicate)
            .groupBy(comingItem.item)
            .orderBy(orderSpecifier)
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize());

        List<Tuple> groupedItems = query.fetch();

        groupedItems.forEach(coming -> {
            result.add(
                new ComingItem(
                    coming.get(comingItem.item),
                    filter.getStock(),
                    coming.get(1, BigDecimal.class),
                    coming.get(2, BigDecimal.class),
                    CommonUtils.validateBigDecimal(coming.get(3, BigDecimal.class)),
                    CommonUtils.validateBigDecimal(coming.get(4, BigDecimal.class)),
                    CommonUtils.validateDate(coming.get(5, Date.class))
                )
            );
        });

        Page<ComingItem> page = new PageImpl<ComingItem>(result, pageRequest, query.fetchCount());

        ResponseByInventory response =
            new ResponseByInventory(ELEMENTS_FOUND, page.getContent(), true, page.getTotalPages());

        if(checkResponse(response.getEntityItems().size(), response))
            calcTotals(filter, abstractEntityManager, response);

        return response;

    }

    public ResponseItemExt<ComingItem> findByFilter(ComingItemFilter filter) {

        itemHandler.checkEanInFilter(filter);

        if(filter.getInventoryModeEnabled())
            return getInventoryItemsNew(filter);

        filter.validateFilterSortField(filter.getDefSortingField());
        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());
        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<ComingItem> page =  comingItemRepository.findAll(cipb.buildByFilter(filter, abstractEntityManager), pageRequest);

        ResponseByComingItems response =
                new ResponseByComingItems(ELEMENTS_FOUND, page.getContent(), true, page.getTotalPages());

        if(checkResponse(page.getContent().size(), response))
            calcTotals(filter, abstractEntityManager, response);

        return response;
    }

    public DtoItemForNewComing getItemForNewComing(String ean, Long stockId) {

        Item item = itemHandler.getItemByEanSynonym(ean);
        if(item == null)
            return new DtoItemForNewComing(null, BigDecimal.ZERO, BigDecimal.ZERO);

        ComingItem coming
                = comingItemRepository
            .findTopPriceOutByItemEanAndStockIdOrderByIdDesc(item.getEan(), stockId);

        if(coming == null)
            return new DtoItemForNewComing(item, item.getPrice(), item.getPrice());

        return new DtoItemForNewComing(coming.getItem(), coming.getPriceIn(), getComingPrice(coming));
    }

    public void setInventoryItems(Set<ComingItem> comingItems) {

        for (ComingItem coming : comingItems) {

            Item item = itemHandler.getItemById(coming.getItem().getId());
            InventoryRow newInventoryRow = new InventoryRow(
                                            userHandler.getCurrentUser(),
                                            coming.getStock(),
                                            coming.getCurrentQuantity());

            if(item.getInventoryRows().size() == 0)
                item.setInventoryRows(new ArrayList<InventoryRow>() {{add(newInventoryRow);}});
            else {
                InventoryRow inventoryRow = itemHandler.getInventoryRowByStock(item, coming.getStock().getId());
                if(inventoryRow == null)
                    item.getInventoryRows().add(newInventoryRow);
                else {
                    inventoryRow.setDate(new Date());
                    inventoryRow.setUser(userHandler.getCurrentUser());
                    inventoryRow.setQuantity(coming.getCurrentQuantity());
                }
            }

            itemHandler.saveItem(item);

        }
    }

    ResponseItem<Document> getDocForInventory() {

        Supplier supplier = getSupplierHandler().getSupplierForInventory();
        if(supplier == null)
            return new ResponseItem<>(SUPPLIER_FOR_INVENTORY_NOT_FOUND, false);

        Document document = getDocumentHandler().findOneDocumentByFilter(
                new DocumentFilter
                    (new BasicFilter(
                            INVENTORY_DOC_NAME,
                            new Date(),
                            new Date()) , supplier));
        if(document == null) {
            document = getDocumentHandler()
                .saveAndGetDocument(new Document(supplier, new Date(), INVENTORY_DOC_NAME));
        }

        return new ResponseItem<>(INVENTORY_DOC_NAME, true, document);
    }

    BigDecimal getAvailQuantityByEan(List<ComingItem> comingItems) {

        return comingItems.stream().map(ComingItem::getCurrentQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal getComingPrice(ComingItem coming) {
        return coming.getItem().getPrice().compareTo(BigDecimal.ZERO) > 0 ? coming.getItem().getPrice() : coming.getPriceOut();
    }

    BigDecimal getItemPriceOutByIdAndStock(Long itemId, Long stockId) {

        return cipb
            .getQueryForMaxItemPriceOutByIdAndStockId(abstractEntityManager, itemId, stockId)
            .fetchOne();
    }

    BigDecimal getPriceInMaxForComing(Long itemId) {

        return comingItemRepository.findTopPriceInByItemId(itemId).getPriceIn();
    }

    Boolean checkComingCurrentQuantity(BigDecimal availQuantity,
                                       SoldItem lastSoldItemByStock) {
        if(lastSoldItemByStock == null)
            return true;

        QComingItem qComingItem = QComingItem.comingItem;
        BigDecimal cameAfterLastSelling = CommonUtils.validateBigDecimal(
            new JPAQuery<BigDecimal>(abstractEntityManager.getEntityManager())
            .select(qComingItem.currentQuantity.sum()).from(qComingItem)
            .where(qComingItem.item.id.eq(lastSoldItemByStock.getComing().getItem().getId())
                .and(qComingItem.stock.id.eq(lastSoldItemByStock.getComing().getStock().getId()))
                .and(qComingItem.date.between(lastSoldItemByStock.getDate(), new Date())))
            .fetchOne()
        );

        return availQuantity.compareTo(lastSoldItemByStock.getAvailQuantityByEan().add(cameAfterLastSelling)) <= 0;
    }

}