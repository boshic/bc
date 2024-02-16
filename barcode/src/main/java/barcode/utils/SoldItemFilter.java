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
        SUMM("summ"),
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
        DATE(SortingFields.DATE.value),
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
    public enum SortingFieldsForGroupedBySectionSoldItems implements SortingField {
        COMING_STOCK_NAME(SortingFields.STOCK_NAME.value),
        COMING_ITEM_SECTION_NAME(SortingFields.SECTION_NAME.value),
        SUMM("summ"),
        INCOMESUM(SortingFields.INCOMESUM.value),
        INCOMESUMPERCENT(SortingFields.INCOMESUMPERCENT.value),
        AVAILQUANTITYBYEAN(SortingFields.AVAILQUANTITYBYEAN.value),
        QUANTITY(SortingFields.QUANTITY.value);

        private String value;
        SortingFieldsForGroupedBySectionSoldItems(String value) {this.value = value;}

        @Override
        public String getValue() {return this.value;}

        @Override
        public void checkSortField (String field) {
            SortingFieldsForGroupedBySectionSoldItems
                .valueOf(CommonUtils.toEnumStyle(field));
        }
    }

    private Buyer buyer;
    private User user;
    private Boolean groupByItems;
    private Boolean groupBySections;
    private Boolean mayBeError;
    private Boolean showNotForDeductions;
    private Item compositeItem;

    public SortingField getDefSortingField() {
        if(this.getGroupBySections())
            return SoldItemFilter.SortingFieldsForGroupedBySectionSoldItems.SUMM;
        if(this.getGroupByItems())
            return SoldItemFilter.SortingFieldsForGroupedByItemSoldItems.AVAILQUANTITYBYEAN;
        return SoldItemFilter.SortingFieldsForSoldItemPane.DATE;
    }

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

    public Boolean getGroupBySections() {
        return groupBySections;
    }

    public void setGroupBySections(Boolean groupBySections) {
        this.groupBySections = groupBySections;
    }

    public Boolean getShowNotForDeductions() {
        return showNotForDeductions;
    }

    public void setShowNotForDeductions(Boolean showNotForDeductions) {
        this.showNotForDeductions = showNotForDeductions;
    }
}
