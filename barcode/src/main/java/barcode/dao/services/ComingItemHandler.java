package barcode.dao.services;

import barcode.dao.entities.embeddable.InventoryRow;
import barcode.dao.repositories.SoldItemsRepository;
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

    private static final String AUTO_COMING_MAKER = "Автоприход";

    private static final String CHECK_COMING_INVALID_DOC=
            "Приход с указанным товаром, ценой и документом уже содержится! ";

    private static final String ITEM_IS_COMPOSITE_ERROR=
            "Неудачно! Для компонентных товаров приход создать нельзя! ";

    private static final String TRY_TO_CHANGE_SOLD_COMING_ERROR=
            "Неудачно! Нельзя изменить приход, который уже продавался! ";

    private static final String MAKING_OF_COMING= "Оприходование ";

    private static final String CHANGING_OF_COMING= "Изменение прихода ";

    public static ComingItemPredicatesBuilder cipb = new ComingItemPredicatesBuilder();

//    public static QComingItem qComingItem = QComingItem.comingItem;

    private BasicFilter filter;

    private ComingItemRepository comingItemRepository;

    private SoldItemsRepository soldItemsRepository;

    private DocumentHandler documentHandler;

    private ItemHandler itemHandler;

    private ItemSectionHandler itemSectionHandler;

    private UserHandler userHandler;

    private StockHandler stockHandler;

    private SupplierHandler supplierHandler;

    private AbstractEntityManager abstractEntityManager;

    public ComingItemHandler(ComingItemRepository comingItemRepository, DocumentHandler documentHandler,
                             ItemHandler itemHandler, UserHandler userHandler, StockHandler stockHandler,
                             SupplierHandler supplierHandler, ItemSectionHandler itemSectionHandler,
                             AbstractEntityManager abstractEntityManager, SoldItemsRepository soldItemsRepository) {

        this.comingItemRepository = comingItemRepository;

        this.soldItemsRepository = soldItemsRepository;

        this.documentHandler = documentHandler;

        this.itemHandler = itemHandler;

        this.itemSectionHandler = itemSectionHandler;

        this.userHandler = userHandler;

        this.stockHandler = stockHandler;

        this.supplierHandler = supplierHandler;

        this.abstractEntityManager = abstractEntityManager;
    }

    public void saveComingItem(ComingItem comingItem) { comingItemRepository.save(comingItem);}

    private ResponseItem<ComingItem> update(ComingItem newComing, ComingItem coming) {

        ResponseItem<ComingItem> responseItem = new ResponseItem<ComingItem>("", false);

        coming.setPriceIn(newComing.getPriceIn());

        coming.setPriceOut(newComing.getPriceOut());

        coming.setQuantity(newComing.getQuantity());

        coming.setSum(newComing.getSum() == null ?
                (newComing.getPriceIn().multiply(newComing.getQuantity()))
                        .setScale(2, BigDecimal.ROUND_HALF_UP) : newComing.getSum());

        coming.setCurrentQuantity(coming.getQuantity());

        coming.setLastChangeDate(new Date());

        coming.setDoc(newComing.getDoc());

        coming.setItem(newComing.getItem());

        coming.setStock(newComing.getStock());

        User checkedUser = userHandler.checkUser(newComing.getUser(), AUTO_COMING_MAKER);

        responseItem.setText(CHANGING_OF_COMING + newComing.getItem().getName() + " номер " + newComing.getId());

        if (coming.getId() == null) {

            coming.setComments(new ArrayList<Comment>());

            responseItem.setText(MAKING_OF_COMING + newComing.getItem().getName() + " номер " + newComing.getId());

            coming.setDate(new Date());

            coming.setComment(
                    this.buildComment(coming.getComments(),
                            newComing.getStock().getName() + getQuantityForComment(newComing.getQuantity()),
                            checkedUser.getFullName(),
                            MAKING_OF_COMING, coming.getCurrentQuantity()));
        } else {

            if(coming.getSellings() != null && coming.getSellings().size() > 0)
                return new ResponseItem<>(TRY_TO_CHANGE_SOLD_COMING_ERROR, false);

            coming.setComment(
                    this.buildComment(coming.getComments(), "",
                            checkedUser.getFullName(),
                            CHANGING_OF_COMING, coming.getCurrentQuantity()));
        }

        if(coming.getItem().getComponents() != null && coming.getItem().getComponents().size() > 0)

            return new ResponseItem<>(ITEM_IS_COMPOSITE_ERROR + coming.getItem().getName(), false, coming);


        if (checkedUser != null && checkedUser.getRole().equals("ROLE_ADMIN")) {

            coming.setUser(checkedUser);

            comingItemRepository.save(coming);

            responseItem.setSuccess(true);

            responseItem.setEntityItem(coming);

        } else {

            responseItem.setText("Неудачно! Недостаточно прав для изменения/создания прихода");

            responseItem.setEntityItem(newComing);
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

        if (checkedUser != null && checkedUser.getRole().equals("ROLE_ADMIN")) {

            comingItem.setCurrentQuantity(BigDecimal.ZERO);

            comingItem.setComment(
                    this.buildComment(comingItem.getComments(),
                            getQuantityForComment(comingItem.getCurrentQuantity()),
                            checkedUser.getFullName(), "удален ", comingItem.getCurrentQuantity()));

            comingItem.setQuantity(BigDecimal.ZERO);

            comingItem.setSum(BigDecimal.ZERO);

            comingItemRepository.save(comingItem);

//            comingItemRepository.delete(comingItem);

            return new ResponseItem<ComingItem>("Удален ", true, comingItem);

        } else
            return new ResponseItem<ComingItem>("Неудачно. Удаление не удалось!", true, comingItem);
    }

    public List<ComingItem> getComingBySpec() {
//        ComingItemSpecification spec =
//                new ComingItemSpecification(new SearchCriteria("supplier", ":", "банк"));
        return null;
//        return comingItemRepository.findAll(spec);
    }

    private ResponseItem<ComingItem> getComingForSellSelector(String ean, Long stockId, Boolean isCompositeAllowed) {

        Item item = itemHandler.getItemByEanSynonym(ean);

        if (item == null)
            return new ResponseItem<ComingItem>("не найден товар с заданным ШК: " + ean, false);

        if (!isCompositeAllowed && item.getComponents() != null && item.getComponents().size() > 0)
            return new ResponseItem<ComingItem>("Неудачно! Запрещен подбор компонентных товаров : " + item.getName(), false);

        ComingItem comingItem = new ComingItem();

        comingItem.setCurrentQuantity(BigDecimal.ZERO);

        comingItem.setPriceOut(new BigDecimal(0));

        comingItem.setItem(item);

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

        if (comingItem.getCurrentQuantity().compareTo(BigDecimal.ZERO) == 0)
            return new ResponseItem<ComingItem>("Товара - " + item.getName() + " нет в наличии!", false);

        return new ResponseItem<ComingItem>("Товар найден в остатках в количестве - " +
                comingItem.getCurrentQuantity(),true, comingItem);
    }


    public ResponseItem<ComingItem> getComingForSellNonComposite(String ean, Long stockId) {

        return getComingForSellSelector(ean, stockId, false);
    }


    public ResponseItem<ComingItem> getComingForSell(String ean, Long stockId) {

        return getComingForSellSelector(ean, stockId, true);
    }

    public ResponseItem addItems(Set<ComingItem> comings) {

        ResponseItem<ResponseItem> responseItem =
                new ResponseItem<ResponseItem>("Обработка приход по нескольким позициям",
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

        Document document = documentHandler.findOneDocumentByFilter(new DocumentFilter
                                                (new BasicFilter(coming.getDoc().getName(),coming.getDoc().getDate(),
                                                            coming.getDoc().getDate()), coming.getDoc().getSupplier()));

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

        SortGroupedComingItems(result, filter.getSortField(), filter.getSortDirection());
//
        PagedListHolder<ComingItem> page = new PagedListHolder<ComingItem>(result);
        page.setPageSize(filter.getRowsOnPage());
        page.setPage(filter.getPage() - 1);

        ResponseByComingItems ribyci =
                new ResponseByComingItems("сгрупированные данные", page.getPageList(),
                        true, page.getPageCount());

        if(filter.getCalcTotal())
            ribyci.calcInventoryTotals(result);
//
        return ribyci;
    }

    private static void SortGroupedComingItems(List<ComingItem> comingItems, String field, String direction) {

        ComingItemFilter.ComingItemSortingStrategies strategy
                = ComingItemFilter.ComingItemSortingStrategies.valueOf(field.replace(".","_").toUpperCase());
        strategy.sort(comingItems);

        if(direction.equalsIgnoreCase(BasicFilter.SORT_DIRECTION_DESC))
            Collections.reverse(comingItems);
    }


    public ResponseByComingItems findByFilter(ComingItemFilter filter) {

//        Sort sort = new Sort(Sort.Direction.DESC, "doc.date");

//        abstractEntityManager.test();

        itemHandler.checkEanInFilter(filter);

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());

        if(filter.getInventoryModeEnabled())
                    return getInventoryItems(comingItemRepository.findAll(cipb.buildByFilter(filter)), filter);

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<ComingItem> page =  comingItemRepository.findAll(cipb.buildByFilter(filter), pageRequest);

        List<ComingItem> result = page.getContent();

        if (result.size() > 0) {

            ResponseByComingItems ribyci =
                    new ResponseByComingItems(ELEMENTS_FOUND, result, true, page.getTotalPages());

            if(filter.getCalcTotal()) {

                ribyci.calcTotals(comingItemRepository.findAll(cipb.buildByFilter(filter)));
            }

            return ribyci;
        }

        return new ResponseByComingItems(NOTHING_FOUND, new ArrayList<ComingItem>(), false, 0);
    }


    public DtoItemForNewComing getItemForNewComing(String ean) {

        Item item = itemHandler.getItemByEanSynonym(ean);
        if(item == null)
            return new DtoItemForNewComing(null, BigDecimal.ZERO, BigDecimal.ZERO);

        ComingItem coming
                = comingItemRepository.findTopPriceOutByItemEanOrderByIdDesc(item.getEan());

        if(coming == null)
            return new DtoItemForNewComing(item, item.getPrice(), item.getPrice());

        return new DtoItemForNewComing(coming.getItem(), coming.getPriceIn(), coming.getPriceOut());
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

    BigDecimal getAvailQuantityByEan(List<ComingItem> comingItems) {

        return comingItems.stream().map(ComingItem::getCurrentQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}