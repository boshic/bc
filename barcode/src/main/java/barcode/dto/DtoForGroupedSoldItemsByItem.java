package barcode.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class DtoForGroupedSoldItemsByItem {

    @JsonProperty("coming")
    private DtoComingItemWithItemAndStock comingItem;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("availQuantityByEan")
    private BigDecimal availQuantityByEan;

    public DtoForGroupedSoldItemsByItem() {}
    public DtoForGroupedSoldItemsByItem(
        DtoComingItemWithItemAndStock comingItem,
        BigDecimal price,
        BigDecimal quantity,
        BigDecimal availQuantityByEan) {

        this.comingItem = comingItem;
        this.price = price;
        this.quantity = quantity;
        this.availQuantityByEan = availQuantityByEan;

    }

    public DtoComingItemWithItemAndStock getComingItem() {
        return comingItem;
    }

    public void setComingItem(DtoComingItemWithItemAndStock comingItem) {
        this.comingItem = comingItem;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvailQuantityByEan() {
        return availQuantityByEan;
    }

    public void setAvailQuantityByEan(BigDecimal availQuantityByEan) {
        this.availQuantityByEan = availQuantityByEan;
    }
}


