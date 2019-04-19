package barcode.dao.entities.embeddable;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.Document;
import barcode.dao.entities.Item;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by xlinux on 30.10.18.
 */
@Embeddable
public class ComingReportRow {

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "doc_id")
    @JsonProperty("doc")
    private Document document;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal quantity;

    @JsonProperty("price")
    @Column(columnDefinition="Decimal(19,2)")
    private BigDecimal price;

    public ComingReportRow() {}

    public ComingReportRow(ComingItem comingItem) {

        this.item = comingItem.getItem();

        this.document = comingItem.getDoc();

        this.quantity = comingItem.getCurrentQuantity();

        this.price = comingItem.getPriceOut();
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
