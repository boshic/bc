package barcode.dto;

import barcode.dao.entities.*;
import barcode.dao.services.AbstractEntityManager;
import com.querydsl.core.BooleanBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xlinux on 21.11.18.
 */
public abstract class ResponseItemExt<T> extends ResponseItem<T> {

    final String
    SUMM = "Сумма",
    PRICE = " цена",
    SOLD = "выбыло",
    QUANTITY = "кол-во",
    PRICE_IN = PRICE + " учетн.",
    PRICE_OUT = PRICE + " отп.",
    BY_FACT = " по факту",
    BY_SELECTION = " по выборке",
    SOLD_BY_PRICE_IN = SOLD + PRICE_IN,
    SOLD_BY_PRICE_OUT = SOLD + PRICE_OUT;


    private Integer numberOfPages;
    private List<ResultRowByItemsCollection> totals = new ArrayList<ResultRowByItemsCollection>();
    private List<Buyer> buyers;
    private List<ItemSection> sections;
    private List<Supplier> suppliers;

    private List<Item> goods;

//    public abstract void calcTotals(List<T> items);
    public abstract void calcTotals(AbstractEntityManager abstractEntityManager, BooleanBuilder predicate);

    ResponseItemExt() {}
    ResponseItemExt(String text, List<T> items, Boolean success, Integer number) {
        super(text, items, success);
        this.numberOfPages = number;
    }

    //_deprecated. must be deleted1
//    void setSuppliersByComings(List<ComingItem> comings) {
//        this.setSuppliers(comings
//                        .stream()
//                        .map(s -> s.getDoc().getSupplier()).collect(Collectors.toSet())
//                        .stream()
//                        .sorted(Comparator.comparing(Supplier::getName))
//                        .collect(Collectors.toList()));
//    }

    //_deprecated. must be deleted1
    void setSectionsByComings(List<ComingItem> comings) {
        this.setSections(comings
                .stream()
                .map(s -> s.getItem().getSection()).collect(Collectors.toSet())
                .stream()
                .sorted(Comparator.comparing(ItemSection::getName))
                .collect(Collectors.toList()));
    }

//    void setGoodsBySellings(List<SoldItem> sellings) {
//        this.setGoods(sellings
//                .stream()
//                .map(s -> s.getComing().getItem()).collect(Collectors.toSet())
//                .stream()
//                .sorted(Comparator.comparing(Item::getName))
//                .collect(Collectors.toList()));
//    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<ResultRowByItemsCollection> getTotals() {
        return totals;
    }

    public void setTotals(List<ResultRowByItemsCollection> totals) {
        this.totals = totals;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public List<Buyer> getBuyers() {
        return buyers;
    }

    public void setBuyers(List<Buyer> buyers) {
        this.buyers = buyers;
    }

    public List<ItemSection> getSections() {
        return sections;
    }

    public void setSections(List<ItemSection> sections) {
        this.sections = sections;
    }

    public List<Item> getGoods() {
        return goods;
    }

    public void setGoods(List<Item> goods) {
        this.goods = goods;
    }
}
