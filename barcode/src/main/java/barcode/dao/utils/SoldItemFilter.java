package barcode.dao.utils;

import barcode.dao.entities.*;

import java.util.Comparator;
import java.util.List;

public class SoldItemFilter extends BasicFilter {

    public enum SortingStrategies {
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

        public abstract void sort(List<SoldItem> soldItems);
    }

    private Buyer buyer;
    private User user;
    private String comment;
    private Integer rowsOnPage;
    private Item item;
    private Stock stock;
    private Integer page;
    private String ean;
    private Boolean calcTotal;
    private Boolean groupByItems;
    private String sectionPart;
    private ItemSection section;
    private Supplier supplier;
    private Document document;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Boolean getCalcTotal() {
        return calcTotal;
    }

    public void setCalcTotal(Boolean calcTotal) {
        this.calcTotal = calcTotal;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Integer getRowsOnPage() {
        return rowsOnPage;
    }

    public void setRowsOnPage(Integer rowsOnPage) {
        this.rowsOnPage = rowsOnPage;
    }

    public Boolean getGroupByItems() {
        return groupByItems;
    }

    public void setGroupByItems(Boolean groupByItems) {
        this.groupByItems = groupByItems;
    }

    public String getSectionPart() {
        return sectionPart;
    }

    public void setSectionPart(String sectionPart) {
        this.sectionPart = sectionPart;
    }

    public ItemSection getSection() {
        return section;
    }

    public void setSection(ItemSection section) {
        this.section = section;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
