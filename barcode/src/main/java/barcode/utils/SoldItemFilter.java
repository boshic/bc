package barcode.utils;

import barcode.dao.entities.*;

public class SoldItemFilter extends ComingItemFilter {

    public enum SortingFieldsForGroupedByItemSoldItems {
        COMING_ITEM_NAME("coming.item.name"),
        COMING_STOCK_NAME("coming.stock.name"),
        COMING_ITEM_SECTION_NAME("coming.item.section.name"),
        AVAILQUANTITYBYEAN("availQuantityByEan"), QUANTITY("quantity"), PRICE("price");
        private String value;
        SortingFieldsForGroupedByItemSoldItems(String value) {this.value = value;}
        public String getValue() {return this.value;};
    }

    private Buyer buyer;
    private User user;
    private Boolean groupByItems;
    private Item compositeItem;

    public Buyer getBuyer() {
        return buyer;
    }
    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Boolean getGroupByItems() {
        return groupByItems;
    }
    public void setGroupByItems(Boolean groupByItems) {
        this.groupByItems = groupByItems;
    }
    public Item getCompositeItem() {
        return compositeItem;
    }
    public void setCompositeItem(Item compositeItem) {
        this.compositeItem = compositeItem;
    }
}
