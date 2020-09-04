package barcode.dto;
import java.util.List;

public class ResponseItem<T> {
    private String text;
    private T entityItem;
    private Boolean success;
//    private Integer success;
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
        this.entityItems = entityItems;
    }

    public ResponseItem(String text, Boolean success, T entityItem) {
        this(text, success);
        this.entityItem = entityItem;
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
