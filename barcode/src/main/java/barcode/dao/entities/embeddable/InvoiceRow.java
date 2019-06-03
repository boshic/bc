package barcode.dao.entities.embeddable;


import barcode.dao.entities.Item;
import barcode.dao.entities.SoldItem;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by xlinux on 08.04.18.
 */
@Embeddable
public class InvoiceRow {

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal quantity;

    @Column(columnDefinition="Decimal(19,2)")
    private BigDecimal price;

    @Column(columnDefinition="Decimal(19,2)")
    private BigDecimal vat;

    @Column(name = "sum", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal sum;

    @Column(name = "comment", columnDefinition="varchar(2000) COLLATE utf8_general_ci default ''")
    private String comment;

    public InvoiceRow() {}

    public InvoiceRow(SoldItem soldItem) {

        this.item = soldItem.getComing().getItem();

        this.quantity = soldItem.getQuantity();

        this.vat = soldItem.getVat();

        this.sum = soldItem.getSum();

    }

    public InvoiceRow(SoldItem soldItem, String comment, BigDecimal price) {

        this(soldItem);

        this.sum = price.multiply(soldItem.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP);

        this.price = price;

        this.comment = comment;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
