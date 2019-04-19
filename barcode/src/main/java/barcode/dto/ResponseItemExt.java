package barcode.dto;

import barcode.dao.entities.Buyer;
import barcode.dao.entities.ItemSection;
import barcode.dao.entities.Supplier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xlinux on 21.11.18.
 */
public abstract class ResponseItemExt<T> extends ResponseItem<T> {

    private Integer numberOfPages;
    private List<ResultRowByItemsCollection> totals = new ArrayList<ResultRowByItemsCollection>();
    private List<Buyer> buyers;
    private List<ItemSection> sections;
    private List<Supplier> suppliers;

    public abstract void calcTotals(List<T> items);

    ResponseItemExt() {}
    ResponseItemExt(String text, List<T> items, Boolean success, Integer number) {
        super(text, items, success);
        this.numberOfPages = number;
    }

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
}
