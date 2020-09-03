package barcode.utils;

import barcode.dao.entities.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class ComingItemFilter extends  BasicFilter {

//    public enum ComingItemSortingStrategies implements SortingStrategy<ComingItem>{
//
//        ITEM_NAME {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::getItem, (i1, i2) -> {
//                    return i1.getName().compareToIgnoreCase(i2.getName());
//                }));
//            }
//        },
//        ITEM_SECTION_NAME {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::getItem, (i1, i2) -> {
//                    return i1.getSection().getName().compareToIgnoreCase(i2.getSection().getName());
//                }));
//            }
//        },
//        STOCK_NAME {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::getStock, (s1, s2) -> {
//                    return s1.getName().compareToIgnoreCase(s2.getName());
//                }));
//            }
//        },
//        SUM {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::getSum));
//            }
//        },
//        QUANTITY {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::getQuantity));
//            }
//        },
//        CURRENTQUANTITY {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::getCurrentQuantity));
//            }
//        },
//        CURRENTSUM {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::new, (c1, c2) -> {
//                    return (c1.getCurrentQuantity()
//                                .multiply(c1.getSum().divide(c1.getQuantity(), 5, RoundingMode.CEILING))
//                                .setScale(2, BigDecimal.ROUND_HALF_UP).subtract(c1.getSum()))
//                            .compareTo(
//                                    (c2.getCurrentQuantity()
//                                        .multiply(c2.getSum().divide(c2.getQuantity(), 5, RoundingMode.CEILING))
//                                            .setScale(2, BigDecimal.ROUND_HALF_UP).subtract(c2.getSum())
//                            ));
//                }));
//            }
//        },
//        LASTCHANGEDATE {
//            @Override
//            public void sort(List<ComingItem> comingItems) {
//                comingItems.sort(Comparator.comparing(ComingItem::getLastChangeDate));
//            }
//        },
//        DOC_DATE {
//            @Override
//            public void sort(List<ComingItem> comingItems) {}
//        };
//    }

    public enum SortingFieldsForInventoryRows implements SortingStrategy{
        ITEM_NAME("item.name"),
        STOCK_NAME("stock.name"),
        ITEM_SECTION_NAME("item.section.name"),
        SUMM("summ"),
        CURRENTQUANTITY("currentQuantity"),
        QUANTITY("quantity"),
        LASTINVENTORYCHANGEDATE("lastInventoryChangeDate"),
        INVENTORYSUM("inventorySum");

        private String value;
        SortingFieldsForInventoryRows(String value) {this.value = value;}
        public String getValue() {return this.value;}

        @Override
        public void checkSortField (String field) {
            SortingFieldsForInventoryRows.valueOf(CommonUtils.toEnumStyle(field));
        }
    }

    private String ean;
    private String comment;
    private String sectionPart;
    private Item item;
    private ItemSection section;
    private Stock stock;
    private Integer page;
    private Integer rowsOnPage;
    private Document document;
    private Supplier supplier;
    private Boolean calcTotal;
    private Boolean hideNullQuantity;
    private Boolean inventoryModeEnabled;
    private Boolean strictCommentSearch;

    public ComingItemFilter() {}

    public ComingItemFilter(ComingItemFilter comingItemFilter) {
        super(comingItemFilter.getSearchString(), comingItemFilter.getFromDate(), comingItemFilter.getToDate());
        this.ean = comingItemFilter.getEan();
        this.comment = comingItemFilter.getComment();
        this.sectionPart = comingItemFilter.getSectionPart();
        this.item = comingItemFilter.getItem();
        this.section = comingItemFilter.getSection();
        this.stock = comingItemFilter.getStock();
        this.page = comingItemFilter.getPage();
        this.rowsOnPage = comingItemFilter.getRowsOnPage();
        this.document = comingItemFilter.getDocument();
        this.supplier = comingItemFilter.getSupplier();
        this.calcTotal = comingItemFilter.getCalcTotal();
        this.hideNullQuantity = comingItemFilter.getHideNullQuantity();
        this.inventoryModeEnabled = comingItemFilter.getInventoryModeEnabled();
    }

    public Integer getPage() {
        return page;
    }
    public void setPage(Integer page) {
        this.page = page;
    }

    public Document getDocument() {
        return document;
    }
    public void setDocument(Document document) {
        this.document = document;
    }

    public Supplier getSupplier() {
        return supplier;
    }
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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

    public ItemSection getSection() {
        return section;
    }

    public void setSection(ItemSection section) {
        this.section = section;
    }

    public Stock getStock() {
        return stock;
    }
    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Boolean getCalcTotal() {
        return calcTotal;
    }
    public void setCalcTotal(Boolean calcTotal) {
        this.calcTotal = calcTotal;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getHideNullQuantity() {
        return hideNullQuantity;
    }
    public void setHideNullQuantity(Boolean hideNullQuantity) {
        this.hideNullQuantity = hideNullQuantity;
    }

    public Integer getRowsOnPage() {
        return rowsOnPage;
    }

    public void setRowsOnPage(Integer rowsOnPage) {
        this.rowsOnPage = rowsOnPage;
    }

    public String getSectionPart() {
        return sectionPart;
    }

    public void setSectionPart(String sectionPart) {
        this.sectionPart = sectionPart;
    }

    public Boolean getInventoryModeEnabled() {
        return inventoryModeEnabled;
    }

    public void setInventoryModeEnabled(Boolean inventoryModeEnabled) {
        this.inventoryModeEnabled = inventoryModeEnabled;
    }

    public Boolean getStrictCommentSearch() {
        return strictCommentSearch;
    }

    public void setStrictCommentSearch(Boolean strictCommentSearch) {
        this.strictCommentSearch = strictCommentSearch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComingItemFilter)) return false;
        if (!super.equals(o)) return false;

        ComingItemFilter that = (ComingItemFilter) o;

        if (getEan() != null ? !getEan().equals(that.getEan()) : that.getEan() != null) return false;
        if (getComment() != null ? !getComment().equals(that.getComment()) : that.getComment() != null) return false;
        if (getSectionPart() != null ? !getSectionPart().equals(that.getSectionPart()) : that.getSectionPart() != null)
            return false;
        if (getItem() != null ? !getItem().equals(that.getItem()) : that.getItem() != null) return false;
        if (getSection() != null ? !getSection().equals(that.getSection()) : that.getSection() != null) return false;
        if (getStock() != null ? !getStock().equals(that.getStock()) : that.getStock() != null) return false;
        if (getPage() != null ? !getPage().equals(that.getPage()) : that.getPage() != null) return false;
        if (getRowsOnPage() != null ? !getRowsOnPage().equals(that.getRowsOnPage()) : that.getRowsOnPage() != null)
            return false;
        if (getDocument() != null ? !getDocument().equals(that.getDocument()) : that.getDocument() != null)
            return false;
        if (getSupplier() != null ? !getSupplier().equals(that.getSupplier()) : that.getSupplier() != null)
            return false;
        if (getCalcTotal() != null ? !getCalcTotal().equals(that.getCalcTotal()) : that.getCalcTotal() != null)
            return false;
        if (getHideNullQuantity() != null ? !getHideNullQuantity().equals(that.getHideNullQuantity()) : that.getHideNullQuantity() != null)
            return false;
        if (getInventoryModeEnabled() != null ? !getInventoryModeEnabled().equals(that.getInventoryModeEnabled()) : that.getInventoryModeEnabled() != null)
            return false;
        return getStrictCommentSearch() != null ? getStrictCommentSearch().equals(that.getStrictCommentSearch()) : that.getStrictCommentSearch() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getEan() != null ? getEan().hashCode() : 0);
        result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
        result = 31 * result + (getSectionPart() != null ? getSectionPart().hashCode() : 0);
        result = 31 * result + (getItem() != null ? getItem().hashCode() : 0);
        result = 31 * result + (getSection() != null ? getSection().hashCode() : 0);
        result = 31 * result + (getStock() != null ? getStock().hashCode() : 0);
        result = 31 * result + (getPage() != null ? getPage().hashCode() : 0);
        result = 31 * result + (getRowsOnPage() != null ? getRowsOnPage().hashCode() : 0);
        result = 31 * result + (getDocument() != null ? getDocument().hashCode() : 0);
        result = 31 * result + (getSupplier() != null ? getSupplier().hashCode() : 0);
        result = 31 * result + (getCalcTotal() != null ? getCalcTotal().hashCode() : 0);
        result = 31 * result + (getHideNullQuantity() != null ? getHideNullQuantity().hashCode() : 0);
        result = 31 * result + (getInventoryModeEnabled() != null ? getInventoryModeEnabled().hashCode() : 0);
        result = 31 * result + (getStrictCommentSearch() != null ? getStrictCommentSearch().hashCode() : 0);
        return result;
    }
}

