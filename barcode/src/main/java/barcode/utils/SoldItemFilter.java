package barcode.utils;

import barcode.dao.entities.*;

public class SoldItemFilter extends ComingItemFilter {

    private enum SortingFields {

        ID("id"),
        DATE("date"),
        ITEM_NAME("coming.item.name"),
        STOCK_NAME("coming.stock.name"),
        SECTION_NAME("coming.item.section.name"),
        AVAILQUANTITYBYEAN("availQuantityByEan"),
        QUANTITY("quantity"),
        PRICE("price"),
        SUM("sum"),
        BUYER_NAME("buyer.name"),
        INCOMESUM("incomeSum"),
        INCOMESUMPERCENT("incomeSumPercent");


        private String value;
        SortingFields(String value) {this.value = value;}
    }

    public enum SortingFieldsForSoldItemPane implements SortingField {
        ID(SortingFields.ID.value),
        DATE(SortingFields.DATE.value),
        COMING_ITEM_NAME(SortingFields.ITEM_NAME.value),
        COMING_STOCK_NAME(SortingFields.STOCK_NAME.value),
        COMING_ITEM_SECTION_NAME(SortingFields.SECTION_NAME.value),
        AVAILQUANTITYBYEAN(SortingFields.AVAILQUANTITYBYEAN.value),
        QUANTITY(SortingFields.QUANTITY.value),
        PRICE(SortingFields.PRICE.value),
        SUM(SortingFields.SUM.value),
        BUYER_NAME(SortingFields.BUYER_NAME.value);

        private String value;
        SortingFieldsForSoldItemPane(String value) {this.value = value;}

        @Override
        public String getValue() {return this.value;}

        @Override
        public void checkSortField (String field) {
            SortingFieldsForSoldItemPane
                .valueOf(CommonUtils.toEnumStyle(field));
        }
    }

    public enum SortingFieldsForGroupedByItemSoldItems implements SortingField {
        COMING_ITEM_NAME(SortingFields.ITEM_NAME.value),
        COMING_STOCK_NAME(SortingFields.STOCK_NAME.value),
        COMING_ITEM_SECTION_NAME(SortingFields.SECTION_NAME.value),
        INCOMESUM(SortingFields.INCOMESUM.value),
        INCOMESUMPERCENT(SortingFields.INCOMESUMPERCENT.value),
        AVAILQUANTITYBYEAN(SortingFields.AVAILQUANTITYBYEAN.value),
        QUANTITY(SortingFields.QUANTITY.value), PRICE(SortingFields.PRICE.value);

        private String value;
        SortingFieldsForGroupedByItemSoldItems(String value) {this.value = value;}

        @Override
        public String getValue() {return this.value;}

        @Override
        public void checkSortField (String field) {
            SortingFieldsForGroupedByItemSoldItems
                .valueOf(CommonUtils.toEnumStyle(field));
        }
    }

    private Buyer buyer;
    private User user;
    private Boolean groupByItems;
    private Boolean mayBeError;
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

    public Boolean getMayBeError() {
        return mayBeError;
    }

    public void setMayBeError(Boolean mayBeError) {
        this.mayBeError = mayBeError;
    }
}
