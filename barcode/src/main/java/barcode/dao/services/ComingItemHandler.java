package barcode.dao.services;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class ComingItemHandler extends EntityHandlerImpl {

    private static final String AUTO_COMING_MAKER = "Автоприход";

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

    public ComingItemHandler(ComingItemRepository comingItemRepository, DocumentHandler documentHandler,
                             ItemHandler itemHandler, UserHandler userHandler, StockHandler stockHandler,
                             SupplierHandler supplierHandler, ItemSectionHandler itemSectionHandler,
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

    private ResponseItem<ComingItem> update(ComingItem newComing, ComingItem coming) {

        ResponseItem<ComingItem> responseItem = new ResponseItem<ComingItem>();

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

        responseItem.setText("Изменен приход товара " + newComing.getItem().getName() + " номер " + newComing.getId());

        if (coming.getId() == null) {

            coming.setComments(new ArrayList<Comment>());

            responseItem.setText("Создан приход товара " + newComing.getItem().getName() + " номер " + newComing.getId());

            coming.setFactDate(new Date());

            coming.setComment(
                    this.buildComment(coming.getComments(),
                            newComing.getStock().getName() + " " + newComing.getQuantity() + " ед., ",
                            checkedUser.getFullName(),
                            "Оприходование"));
        } else {
            coming.setComment(
                    this.buildComment(coming.getComments(), "",
                            checkedUser.getFullName(),
                            "Изменение прихода"));
        }


        if (checkedUser != null && checkedUser.getRole().equals("ROLE_ADMIN")) {

            coming.setUser(checkedUser);

            comingItemRepository.save(coming);

            responseItem.setItem(coming);

        } else {

            responseItem.setText("Недостаточно прав для изменения/создания прихода");

            responseItem.setItem(newComing);
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

        User checkedUser = userHandler.checkUser(userHandler.getCurrentUser(), null);

        if (checkedUser != null && checkedUser.getRole().equals("ROLE_ADMIN")) {

            comingItem.setComment(
                    this.buildComment(comingItem.getComments(),  comingItem.getCurrentQuantity() + " ед.",
                            checkedUser.getFullName(),
                            "удален "));

            comingItem.setCurrentQuantity(BigDecimal.ZERO);

            comingItem.setQuantity(BigDecimal.ZERO);

            comingItem.setSum(BigDecimal.ZERO);

            comingItemRepository.save(comingItem);

//            comingItemRepository.delete(comingItem);

            return new ResponseItem<ComingItem>("Удален элемент", true, comingItem);

        } else
            return new ResponseItem<ComingItem>("Недостаточно прав для удаления", true, comingItem);
    }

    public List<ComingItem> getComingBySpec() {
//        ComingItemSpecification spec =
//                new ComingItemSpecification(new SearchCriteria("supplier", ":", "банк"));
        return null;
//        return comingItemRepository.findAll(spec);
    }


    public ResponseItem<ComingItem> getComingForSell(String ean, Long stockId) {

        Item item = itemHandler.getItemByEanSynonim(ean);

        if (item == null)
            return new ResponseItem<ComingItem>("не найден товар с заданным ШК: " + ean, false);

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

    public ResponseItem addItems(Set<ComingItem> comings) {

        ResponseItem<ResponseItem> responseItem =
                new ResponseItem<ResponseItem>("Обработка приход по нескольким позициям",
                                                new ArrayList<ResponseItem>(), true);

        ResponseItem<ResponseItem> responseItemTemp;

        for (ComingItem coming : comings) {

            responseItemTemp = this.checkComing(coming);

            if (responseItemTemp.getSuccess())
                responseItemTemp.getItems().add(update(coming, new ComingItem()));

             else {

                String resp = responseItemTemp.getText();

                responseItemTemp.setText("Приход НЕ добавлен по товару " + coming.getItem().getName() + ", т.к. " + resp);

            }

            responseItem.getItems().add(responseItemTemp);
        }

        return responseItem;
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
            coming.getItem().setSection(new ItemSection("Секция не задана"));

        ItemSection section = itemSectionHandler.getItemByName(coming.getItem().getSection().getName());

        ResponseItem responseBySection = new ResponseItem("Секция с именем "
                                                    + coming.getItem().getSection().getName() + " найдена");

        if(section == null) {

            section = itemSectionHandler.addItem(coming.getItem().getSection()).getItem();

            responseBySection.setText("Секция с именем " + coming.getItem().getName() + " создана");
        }

        coming.getItem().setSection(section);

        responseItem.getItems().add(responseBySection);

        //item
        ResponseItem responseByItem = new ResponseItem("Товар с именем " + coming.getItem().getName() + " найден");

        Item item = itemHandler.getItemByEan(coming.getItem().getEan());

        if (item == null) {

            item = itemHandler.addItem(coming.getItem()).getItem();

            responseByItem.setText("Новый товар с именем " + coming.getItem().getName() + " создан");
        }
        else {

            item.setSection(section);

            item.setName(coming.getItem().getName());
        }


        coming.setItem(item);

        responseItem.getItems().add(responseByItem);

        //supplier
        ResponseItem responseBySupplier = new ResponseItem("Поставщик " + coming.getDoc().getSupplier().getName()
                                                                                                        + " найден");
        Supplier supplier = supplierHandler.getSupplierByName(coming.getDoc().getSupplier().getName());

        if (supplier == null) {

            supplier = supplierHandler.addSupplier(coming.getDoc().getSupplier()).getItem();

            responseBySupplier.setText("Поставщик " + coming.getDoc().getSupplier().getName() + " создан");
        }

        coming.getDoc().setSupplier(supplier);

        responseItem.getItems().add(responseBySupplier);

        //doc
        ResponseItem responseBydoc = new ResponseItem("Документ " + coming.getDoc().getName() + " от "
                                                                            + coming.getDoc().getDate() +" найден");

        Document document = documentHandler.findOneDocumentByFilter(new DocumentFilter
                                                (new BasicFilter(coming.getDoc().getName(),coming.getDoc().getDate(),
                                                            coming.getDoc().getDate()), coming.getDoc().getSupplier()));

        if (document == null ) {

            document = documentHandler.addItem(coming.getDoc()).getItem();

            responseBydoc.setText("Документ " + coming.getDoc().getName() + " от "
                                              + coming.getDoc().getDate() +" создан");
        }

        coming.setDoc(document);

        responseItem.getItems().add(responseBydoc);

        QComingItem qComingItem = QComingItem.comingItem;

        Predicate predicate = qComingItem.doc.eq(coming.getDoc())
                                            .and(qComingItem.quantity.gt(0))
                                            .and(qComingItem.item.eq(coming.getItem()))
                                            .and(qComingItem.priceIn.eq(coming.getPriceIn()));

        if (comingItemRepository.findAll(predicate).size() > 0) {

            responseItem.getItems().add(new ResponseItem(
                 "Приход этого наименования с таким же документом и ценой " +"уже содержится, добавление НЕ состоится"));

            return responseItem;
        }

        responseItem.setSuccess(true);

        return responseItem;
    }

    public ResponseByComingItems findByFilter(ComingItemFilter filter) {

//        Sort sort = new Sort(Sort.Direction.DESC, "doc.date");

        abstractEntityManager.test();

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<ComingItem> page =  comingItemRepository.findAll(cipb.buildByFilter(filter), pageRequest);

        List<ComingItem> result = page.getContent();

        if (result.size() > 0) {

            ResponseByComingItems ribyci =
                    new ResponseByComingItems("найдены элементы", result, true, page.getTotalPages());

            if(filter.getCalcTotal()) {
//                 && (this.filter == null || !this.filter.equals(filter))

                ribyci.calcTotals(comingItemRepository.findAll(cipb.buildByFilter(filter)));

//                this.filter = new ComingItemFilter(filter);
            }

            return ribyci;
        }

        return new ResponseByComingItems("ничего не найдено", new ArrayList<ComingItem>(), false, 0);
    }


    public DtoItemForNewComing getItemForNewComing(String ean) {

        Item item = itemHandler.getItemByEanSynonim(ean);
        if(item == null)
            return new DtoItemForNewComing(null, BigDecimal.ZERO, BigDecimal.ZERO);

        ComingItem coming
                = comingItemRepository.findTopPriceOutByItemEanOrderByIdDesc(item.getEan());

        if(coming == null)
            return new DtoItemForNewComing(item, BigDecimal.ZERO, BigDecimal.ZERO);

        return new DtoItemForNewComing(coming.getItem(), coming.getPriceIn(), coming.getPriceOut());
    }

}