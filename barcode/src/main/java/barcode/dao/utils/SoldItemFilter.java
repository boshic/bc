package barcode.dao.utils;

import barcode.dao.entities.*;

import java.util.Comparator;
import java.util.List;

public class SoldItemFilter extends ComingItemFilter {

    public enum SoldItemSortingStrategies implements SortingStrategy<SoldItem>{
        COMING_ITEM_NAME {
            @Override
            public void sort(List<SoldItem> soldItems) {
                soldItems.sort(Comparator.comparing(SoldItem::getComing, (c1, c2) -> {
                    return c1.getItem().getName().compareToIgnoreCase(c2.getItem().getName());
                }));
            }
        },
        COMING_ITEM_SECTION_NAME {
            @Override
            public void sort(List<SoldItem> soldItems) {
                soldItems.sort(Comparator.comparing(SoldItem::getComing, (c1, c2) -> {
                    return c1.getItem().getSection().getName().compareToIgnoreCase(c2.getItem().getSection().getName());
                }));
            }
        },
        COMING_STOCK_NAME {
            @Override
            public void sort(List<SoldItem> soldItems) {
                soldItems.sort(Comparator.comparing(SoldItem::getComing, (c1, c2) -> {
                    return c1.getStock().getName().compareToIgnoreCase(c2.getStock().getName());
                }));
            }
        },
        AVAILQUANTITYBYEAN {
            @Override
            public void sort(List<SoldItem> soldItems) {
                soldItems.sort(Comparator.comparing(SoldItem::getAvailQuantityByEan));
            }
        },
        QUANTITY {
            @Override
            public void sort(List<SoldItem> soldItems) {
                soldItems.sort(Comparator.comparing(SoldItem::getQuantity));
            }
        },
        DATE {
            @Override
            public void sort(List<SoldItem> soldItems) {}
        },
        PRICE {
            @Override
            public void sort(List<SoldItem> soldItems) {
                soldItems.sort(Comparator.comparing(SoldItem::getPrice));
            }
        };

    }

    private Buyer buyer;
    private User user;
    private Boolean groupByItems;

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
}
