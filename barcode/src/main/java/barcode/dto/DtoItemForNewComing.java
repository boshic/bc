package barcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.Item;

import java.math.BigDecimal;

/**
 * Created by xlinux on 25.01.19.
 */
public class DtoItemForNewComing {

    @JsonProperty("item")
    private Item item;

    private BigDecimal price, priceOut;

    public DtoItemForNewComing() {
        this.price = BigDecimal.ZERO;
        this.priceOut = BigDecimal.ZERO;
        this.item = null;
    }

    public DtoItemForNewComing(Item item, BigDecimal price, BigDecimal priceOut) {
        this.price = price;
        this.priceOut = priceOut;
        this.item = item;
    }


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceOut() {
        return priceOut;
    }

    public void setPriceOut(BigDecimal priceOut) {
        this.priceOut = priceOut;
    }
}
