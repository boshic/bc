package barcode.dto;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.Invoice;
import barcode.dao.entities.SoldItem;

import java.util.List;

public class ResponseItem<T> {
    private String text;
    private T entityItem;
    private Boolean success;
    private List<T> entityItems;

    public ResponseItem() {}

    public ResponseItem(String text) {
        this.text = text;
    }

    public ResponseItem(String text, Boolean success) {
        this.text = text;
        this.success = success;
    }

    public ResponseItem(String text, List<T> entityItems, Boolean success) {
        this(text, success);
        prepareItemsForTransfer(entityItems);
        this.entityItems = entityItems;
    }

    public ResponseItem(String text, Boolean success, T entityItem) {
        this(text, success);
        prepareItemForTransfer(entityItem);
        this.entityItem = entityItem;
    }

    public void packItems() {

        if(this.entityItem != null)
            prepareItemForTransfer(this.entityItem);

        if(this.entityItems != null && this.entityItems.size() > 0)
            prepareItemsForTransfer(this.entityItems);

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


    public String getText() {return this.text;}
    public void setText(String text) {this.text = text;}

    public Boolean getSuccess() {return this.success;}
    public void setSuccess(Boolean success) {this.success = success;}

    public T getEntityItem() {
        return entityItem;
    }

    public void setEntityItem(T entityItem) {
        this.entityItem = entityItem;
    }

    public List<T> getEntityItems() {
        return entityItems;
    }

    public void setEntityItems(List<T> entityItems) {
        this.entityItems = entityItems;
    }
}
