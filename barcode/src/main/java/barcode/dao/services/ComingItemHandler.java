package barcode.dao.services;

import barcode.dao.entities.embeddable.InventoryRow;
import barcode.dao.repositories.SoldItemsRepository;
import barcode.enums.CommentAction;
import barcode.enums.SystemMessage;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import barcode.dao.entities.*;
import barcode.dao.entities.embeddable.Comment;
import barcode.dao.predicates.ComingItemPredicatesBuilder;
import barcode.dao.repositories.ComingItemRepository;
import barcode.dao.utils.BasicFilter;
import barcode.dao.utils.ComingItemFilter;
import barcode.dao.utils.DocumentFilter;
import barcode.dto.DtoItemForNewComing;
import barcode.dto.ResponseByComingItems;
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

@Service
public class ComingItemHandler extends EntityHandlerImpl {

    public static ComingItemPredicatesBuilder cipb = new ComingItemPredicatesBuilder();

//    public static QComingItem qComingItem = QComingItem.comingItem;

    private BasicFilter filter;

    private ComingItemRepository comingItemRepository;


    private DocumentHandler documentHandler;

    private ItemHandler itemHandler;

    private ItemSectionHandler itemSectionHandler;

    private UserHandler userHandler;

    private StockHandler stockHandler;

    private SupplierHandler supplierHandler;

    private AbstractEntityManager abstractEntityManager;

    public ComingItemHandler(ComingItemRepository comingItemRepository,
                             DocumentHandler documentHandler,
                             ItemHandler itemHandler,
                             UserHandler userHandler,
                             StockHandler stockHandler,
                             SupplierHandler supplierHandler,
                             ItemSectionHandler itemSectionHandler,
                             AbstractEntityManager abstractEntityManager) {

        this.comingItemRepository = comingItemRepository;
        this.documentHandler = documentHandler;
        this.itemHandler = itemHandler;
        this.itemSectionHandler = itemSectionHandler;
        this.userHandler = userHandler;
        this.stockHandler = stockHandler;
        this.supplierHandler = supplierHandler;
        this.abstractEntityManager = abstractEntityManager;
    }

    public void saveComingItem(ComingItem comingItem) { comingItemRepository.save(comingItem);}

    private ResponseItem<ComingItem> update(ComingItem srcComing, ComingItem coming) {

        ResponseItem<ComingItem> responseItem = new ResponseItem<ComingItem>("", false);

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

            if(coming.getSellings() != null && coming.getSellings().size() > 0)
                return new ResponseItem<>(TRY_TO_CHANGE_SOLD_COMING_ERROR, false);

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

//        responseItem.packItems();

        return responseItem;
    }

    public ResponseItem addItem(ComingItem newComing) {

        ComingItem coming = new ComingItem();

        return update(newComing, coming);
    }

    public ResponseItem updateItem(ComingItem updComing) {

        ComingItem coming = this.getComingItemById(updComing.getId());

        return update(updComing, coming);
    }

    public ComingItem getComingItemById(Long id) {
        return comingItemRepository.findOne(id);
    }

    public List<ComingItem> getComingItemByIdAndStockId(Long itemId, Long stockId) {

        return comingItemRepository.findAll(cipb.getAvailableItemsByStock(itemId, stockHandler.getStockById(stockId)),
                                                                    new Sort(Sort.Direction.ASC, "doc.date"));
    }

    public synchronized ResponseItem<ComingItem> deleteItem(long id) {

        ComingItem comingItem = this.getComingItemById(id);

        if(comingItem.getSellings() != null && comingItem.getSellings().size() > 0)
            return new ResponseItem<>(TRY_TO_CHANGE_SOLD_COMING_ERROR, false);

        User checkedUser = userHandler.checkUser(userHandler.getCurrentUser(), null);

        if (checkedUser != null && checkedUser.getRole().equals(ROLE_ADMIN)) {

            comingItem.setCurrentQuantity(BigDecimal.ZERO);

            comingItem.setComment(
                    this.buildComment(comingItem.getComments(),
                            getQuantityForComment(comingItem.getCurrentQuantity()),
                            checkedUser.getFullName(),
                            CommentAction.SMTH_DELETED.getAction(),
                            comingItem.getCurrentQuantity()));

            comingItem.setQuantity(BigDecimal.ZERO);

            comingItem.setSum(BigDecimal.ZERO);

            comingItemRepository.save(comingItem);

//            comingItemRepository.delete(comingItem);

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
                    comings.stream().max(Comparator.comparing(ComingItem::getPriceOut)).get().getPriceOut(),
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
                getGroupedComingsForRelease(comingItemRepository.findAll(cipb.buildByFilterForRelease(filter)));

        if(comings.size() > 0)
            return new ResponseItem<ComingItem>("", comings, true);
        else
            return new ResponseItem<ComingItem>(NOTHING_FOUND, false);
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
        Stock stock = stockHandler.getStockByName(coming.getStock().getName());

        if (stock == null)
            return new ResponseItem<>("Заведите склад со следующим наименованием! - " + coming.getStock().getName(), false);

        coming.setStock(stock);

        //section
        if(coming.getItem().getSection() == null)
            coming.getItem().setSection(new ItemSection(DEAFULT_SECTION_NAME));

        ItemSection section = itemSectionHandler.getItemByName(coming.getItem().getSection().getName());

        ResponseItem responseBySection = new ResponseItem(CHECK_SECTION_LOG_MESS
                                                    + coming.getItem().getSection().getName() + SMTH_FOUND);

        if(section == null) {

            section = itemSectionHandler.addItem(coming.getItem().getSection()).getEntityItem();

            responseBySection.setText(CHECK_SECTION_LOG_MESS + coming.getItem().getName() + SMTH_CREATED);
        }

        coming.getItem().setSection(section);

        responseItem.getEntityItems().add(responseBySection);

        //item
        ResponseItem responseByItem = new ResponseItem(CHECK_ITEM_LOG_MESS + coming.getItem().getName() + SMTH_FOUND);

//        Item item = itemHandler.getItemByEan(coming.getItem().getEan());
        Item item = itemHandler.getItemByEanSynonym(coming.getItem().getEan());

        if (item == null) {

            item = itemHandler.addItem(coming.getItem()).getEntityItem();

            responseByItem.setText(CHECK_ITEM_LOG_MESS + coming.getItem().getName() + SMTH_CREATED);
        }
        else {

            item.setSection(section);

            item.setName(coming.getItem().getName());
        }




        coming.setItem(item);

        responseItem.getEntityItems().add(responseByItem);

        //supplier
        ResponseItem responseBySupplier = new ResponseItem("Поставщик " + coming.getDoc().getSupplier().getName()
                                                                                                        + SMTH_FOUND);
        Supplier supplier = supplierHandler.getSupplierByName(coming.getDoc().getSupplier().getName());

        if (supplier == null) {

            supplier = supplierHandler.addSupplier(coming.getDoc().getSupplier()).getEntityItem();

            responseBySupplier.setText("Поставщик " + coming.getDoc().getSupplier().getName() + SMTH_CREATED);
        }

        coming.getDoc().setSupplier(supplier);

        responseItem.getEntityItems().add(responseBySupplier);

        //doc
        ResponseItem responseBydoc = new ResponseItem("Документ " + coming.getDoc().getName() + " от "
                                                                            + coming.getDoc().getDate() + SMTH_FOUND);

        Document document = documentHandler
                .findOneDocumentByFilter(new DocumentFilter
                                                (new BasicFilter(
                                                        coming.getDoc().getName(),
                                                        coming.getDoc().getDate(),
                                                        coming.getDoc().getDate()),
                                                        coming.getDoc().getSupplier()));

        if (document == null ) {

            document = documentHandler.addItem(coming.getDoc()).getEntityItem();

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

    private ResponseByComingItems getInventoryItems(List<ComingItem> comingItems, ComingItemFilter filter) {

        Map<Item, List<ComingItem>> groupedItems = comingItems.stream()
                                                    .collect(Collectors.groupingBy(ComingItem::getItem));

        List<ComingItem> result = new ArrayList<ComingItem>();

        //item, stock, sum, quantity, currentQuantity
        groupedItems.forEach((item, comings) -> {
            InventoryRow inventoryRow = itemHandler.getInventoryRowByStock(item, filter.getStock().getId());
            ComingItem coming = new ComingItem(
                    item,
                    filter.getStock(),
                    comings.stream()
                            .map(ComingItem::getSum)
                            .reduce(BigDecimal.ZERO, BigDecimal::add),
                    comings.stream()
                            .map(ComingItem::getCurrentQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add),
                    comings.stream().max(Comparator.comparing(ComingItem::getPriceIn)).get().getPriceIn(),
                    comings.stream().max(Comparator.comparing(ComingItem::getPriceOut)).get().getPriceOut(),
                    inventoryRow == null ? BigDecimal.ZERO : inventoryRow.getQuantity(),
                    inventoryRow == null ? null : inventoryRow.getDate()
            );
            if(coming.getSum().compareTo(BigDecimal.ZERO) > 0)
                coming.setPriceIn(
                    coming.getQuantity().compareTo(BigDecimal.ZERO) > 0 ?
                    coming.getSum().divide(coming.getQuantity(), 5, RoundingMode.CEILING) : BigDecimal.ZERO
                );
            result.add(coming);
        });

        sortGroupedItems(result,
                         filter.getSortDirection(),
                         ComingItemFilter
                                 .ComingItemSortingStrategies
                                 .valueOf(filter.getSortField()
                                         .replace(".","_")
                                         .toUpperCase()));

//
        PagedListHolder<ComingItem> page = new PagedListHolder<ComingItem>(result);
        page.setPageSize(filter.getRowsOnPage());
        page.setPage(filter.getPage() - 1);

        ResponseByComingItems ribyci =
                new ResponseByComingItems(ELEMENTS_FOUND, page.getPageList(),
                        true, page.getPageCount());

        if(filter.getCalcTotal())
            ribyci.calcInventoryTotals(result);
//
        return ribyci;
    }

    public ResponseByComingItems findByFilter(ComingItemFilter filter) {

//        Sort sort = new Sort(Sort.Direction.DESC, "doc.date");

//        abstractEntityManager.test();

        itemHandler.checkEanInFilter(filter);

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());

        BooleanBuilder predicate = cipb.buildByFilter(filter, abstractEntityManager);

        if(filter.getInventoryModeEnabled())
                    return getInventoryItems(comingItemRepository.findAll(predicate), filter);

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<ComingItem> page =  comingItemRepository.findAll(predicate, pageRequest);

        List<ComingItem> result = page.getContent();

        if (result.size() > 0) {

            ResponseByComingItems ribyci =
                    new ResponseByComingItems(ELEMENTS_FOUND, result, true, page.getTotalPages());

            if(filter.getCalcTotal()) {

                ribyci.calcTotals(abstractEntityManager, predicate);
//                ribyci.calcTotals(comingItemRepository.findAll(predicate));
            }

            return ribyci;
        }

        return new ResponseByComingItems(NOTHING_FOUND, new ArrayList<ComingItem>(), false, 0);
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

            InventoryRow inventoryRow = itemHandler.getInventoryRowByStock(item, coming.getStock().getId());

            if(inventoryRow == null)
                item.setInventoryRows(new ArrayList<InventoryRow>() {
                    {
                        add(new InventoryRow(userHandler.getCurrentUser(),
                                coming.getStock(), coming.getCurrentQuantity()));
                    }
                });
            else {
                inventoryRow.setDate(new Date());
                inventoryRow.setUser(userHandler.getCurrentUser());
                inventoryRow.setQuantity(coming.getCurrentQuantity());
            }
            itemHandler.saveItem(item);

        }
    }

    ResponseItem<Document> getDocForInventory() {

        Supplier supplier = supplierHandler.getSupplierForInventory();
        if(supplier == null)
            return new ResponseItem<>(SUPPLIER_FOR_INVENTORY_NOT_FOUND, false);

        Document document = documentHandler.findOneDocumentByFilter(
                new DocumentFilter
                    (new BasicFilter(
                            INVENTORY_DOC_NAME,
                            new Date(),
                            new Date())
                                , supplier));
        if(document == null) {
            document = new Document(supplier, new Date(), INVENTORY_DOC_NAME);
            documentHandler.saveDocument(document);
        }

        return new ResponseItem<>(INVENTORY_DOC_NAME, true, document);
    }

    BigDecimal getAvailQuantityByEan(List<ComingItem> comingItems) {

        return comingItems.stream().map(ComingItem::getCurrentQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal getComingPrice(ComingItem coming) {
        return coming.getItem().getPrice().compareTo(BigDecimal.ZERO) > 0 ? coming.getItem().getPrice() : coming.getPriceOut();
    }

}