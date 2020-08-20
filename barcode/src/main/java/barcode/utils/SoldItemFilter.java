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

//    public enum SoldItemSortingStrategies
//        implements SortingStrategy<DtoForGroupedSoldItemsByItem> {
//        COMING_ITEM_NAME {
//            @Override
//            public void sort(List<DtoForGroupedSoldItemsByItem> items) {
//                items.sort(Comparator.comparing(DtoForGroupedSoldItemsByItem::getComingItem, (c1, c2) -> {
//                    return c1.getItem().getName().compareToIgnoreCase(c2.getItem().getName());
//                }));
//            }
//        },
//        COMING_ITEM_SECTION_NAME {
//            @Override
//            public void sort(List<DtoForGroupedSoldItemsByItem> items) {
//                items.sort(Comparator.comparing(DtoForGroupedSoldItemsByItem::getComingItem, (c1, c2) -> {
//                    return c1.getItem().getSection().getName().compareToIgnoreCase(c2.getItem().getSection().getName());
//                }));
//            }
//        },
//        COMING_STOCK_NAME {
//            @Override
//            public void sort(List<DtoForGroupedSoldItemsByItem> items) {
//                items.sort(Comparator.comparing(DtoForGroupedSoldItemsByItem::getComingItem, (c1, c2) -> {
//                    return c1.getStock().getName().compareToIgnoreCase(c2.getStock().getName());
//                }));
//            }
//        },
//        AVAILQUANTITYBYEAN {
//            @Override
//            public void sort(List<DtoForGroupedSoldItemsByItem> items) {
//                items.sort(Comparator.comparing(DtoForGroupedSoldItemsByItem::getAvailQuantityByEan));
//            }
//        },
//        QUANTITY {
//            @Override
//            public void sort(List<DtoForGroupedSoldItemsByItem> items) {
//                items.sort(Comparator.comparing(DtoForGroupedSoldItemsByItem::getQuantity));
//            }
//        },
//        DATE {
//            @Override
//            public void sort(List<DtoForGroupedSoldItemsByItem> items) {}
//        },
//        PRICE {
//            @Override
//            public void sort(List<DtoForGroupedSoldItemsByItem> items) {
//                items.sort(Comparator.comparing(DtoForGroupedSoldItemsByItem::getPrice));
//            }
//        };
//    }

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
