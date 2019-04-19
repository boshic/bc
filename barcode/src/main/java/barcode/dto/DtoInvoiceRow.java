package barcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.embeddable.InvoiceRow;

import java.math.BigDecimal;

/**
 * Created by xlinux on 14.09.18.
 */
public class DtoInvoiceRow {

    @JsonProperty("name")
    private String itemName;

    @JsonProperty("ean")
    private String itemEan;

    @JsonProperty("quantity")
    private BigDecimal itemQuantity;

    @JsonProperty("vat")
    private BigDecimal vat;

    @JsonProperty("price")
    private BigDecimal price;

    public DtoInvoiceRow() {}

    public DtoInvoiceRow(InvoiceRow invoiceRow) {

        this.setItemName(invoiceRow.getItem().getName());

        this.setItemEan(invoiceRow.getItem().getEan());

        this.setItemQuantity(invoiceRow.getQuantity());

        this.setPrice(invoiceRow.getPrice());

        this.setVat(invoiceRow.getVat());
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemEan() {
        return itemEan;
    }

    public void setItemEan(String itemEan) {
        this.itemEan = itemEan;
    }

    public BigDecimal getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(BigDecimal itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
