package barcode.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

import java.util.Date;

import static com.querydsl.core.types.dsl.Expressions.stringPath;

public class BasicFilter {

    public static final String SORT_DIRECTION_ASC = "ASC";
    public static final String SORT_DIRECTION_DESC = "DESC";

    private String searchString;
    private Date fromDate;
    private Date toDate;

    private String sortDirection;

    private String sortField;

    public BasicFilter() {}

    public BasicFilter(String searchString, Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.searchString = searchString;
    }

    public BasicFilter(BasicFilter filter) {

        this(filter.getSearchString(), filter.getFromDate(), filter.getToDate());
    }

    public <T> OrderSpecifier getOrderSpec(
        String sortField,
        String sortDirection,
        Path<?> parent,
        Class<? extends T> type) {

        Path<T> fieldPath =  Expressions.path(type, parent, sortField);
        Order order = Order.valueOf(sortDirection);

        return (sortField.contains(".")) ?
            new OrderSpecifier(order, fieldPath)
            : new OrderSpecifier(order, stringPath(sortField));
    }

    public <T extends ComingItemFilter>
    void validateFilterSortField(T filter, SortingField sortingField) {
        try {
            sortingField.checkSortField(filter.getSortField());
        } catch (IllegalArgumentException e) {
            filter.setSortField(sortingField.getValue());
        }
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
    public String getSearchString() {
        return searchString;
    }
    public Date getFromDate() {
        return fromDate;
    }
    public Date getToDate() {
        return toDate;
    }
    public String getSortDirection() {
        return sortDirection;
    }
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    public String getSortField() {
        return sortField;
    }
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicFilter)) return false;

        BasicFilter that = (BasicFilter) o;

        if (getSearchString() != null ? !getSearchString().equals(that.getSearchString()) : that.getSearchString() != null)
            return false;
        if (getFromDate() != null ? !getFromDate().equals(that.getFromDate()) : that.getFromDate() != null)
            return false;
        return getToDate() != null ? getToDate().equals(that.getToDate()) : that.getToDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getSearchString() != null ? getSearchString().hashCode() : 0;
        result = 31 * result + (getFromDate() != null ? getFromDate().hashCode() : 0);
        result = 31 * result + (getToDate() != null ? getToDate().hashCode() : 0);
        return result;
    }
}
