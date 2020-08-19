package barcode.dto;

import barcode.dao.entities.Item;
import barcode.dao.entities.Stock;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoComingItemWithItemAndStock {

    @JsonProperty("item")
    private Item item;

    @JsonProperty("stock")
    private Stock stock;

    public DtoComingItemWithItemAndStock() {}

    public DtoComingItemWithItemAndStock(Item item, Stock stock) {
        this.item = item;
        this.stock = stock;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
