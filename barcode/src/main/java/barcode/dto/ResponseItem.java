package barcode.dto;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.Invoice;
import barcode.dao.entities.SoldItem;

import java.util.List;

public class ResponseItem<T> {
    private String text;
    private T item;
    private Boolean success;
    private List<T> items;

    public ResponseItem() {}

    public ResponseItem(String text) {
        this.text = text;
    }

    public ResponseItem(String text, Boolean success) {
        this.text = text;
        this.success = success;
    }

    public ResponseItem(String text, List<T> items, Boolean success) {
        this(text, success);
        prepareItemsForTransfer(items);
        this.items = items;
    }

    public ResponseItem(String text, Boolean success, T item) {
        this(text, success);
        prepareItemForTransfer(item);
        this.item = item;
    }

    public void packItems() {

        if(this.item != null)
            prepareItemForTransfer(this.item);

        if(this.items != null && this.items.size() > 0)
            prepareItemsForTransfer(this.items);

    }

    private void prepareItemsForTransfer (List<T> objs) {

        for(Object o: objs)
            prepareItemForTransfer(o);

    }

    private void prepareItemForTransfer(Object o) {

        if (o instanceof ComingItem)
            if (((ComingItem) o).getUser() != null)
                ((ComingItem) o).getUser().setStamp(null);

        if (o instanceof SoldItem)
            if (((SoldItem) o).getUser() != null)
                ((SoldItem) o).getUser().setStamp(null);

        if (o instanceof Invoice)
            if (((Invoice) o).getUser() != null)
            ((Invoice) o).getUser().setStamp(null);
    }

    public T getItem() {return this.item;}
    public void setItem(T item) {this.item = item;}

    public String getText() {return this.text;}
    public void setText(String text) {this.text = text;}

    public Boolean getSuccess() {return this.success;}
    public void setSuccess(Boolean success) {this.success = success;}

    public List<T> getItems() { return this.items;}
    public void setItems(List<T> items) { this.items = items;}

}
