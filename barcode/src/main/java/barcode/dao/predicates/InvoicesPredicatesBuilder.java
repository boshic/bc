package barcode.dao.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.services.InvoiceHandler;
import barcode.dao.utils.SoldItemFilter;

/**
 * Created by xlinux on 20.11.18.
 */
public class InvoicesPredicatesBuilder {

    public BooleanExpression buildByFilter(SoldItemFilter filter) {

        BooleanExpression predicate
                = InvoiceHandler.qInvoice.date.between(filter.getFromDate(), filter.getToDate());

//        if (filter.getStock() != null && !filter.getStock().isAllowAll())
//            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.stock.id.eq(filter.getStock().getId()));
//
//        if(filter.getComment() != null)
//            predicate = predicate.and(SoldItemHandler.qSoldItem.comment.containsIgnoreCase(filter.getComment()));
//
//        if(filter.getSearchString() != null)
//            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.item.name.containsIgnoreCase(filter.getSearchString()));
//
//        if (filter.getEan() != null && filter.getEan().length() == 13)
//            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.item.ean.eq(filter.getEan()));
//
//        if (filter.getItem() != null && filter.getItem().getId() != null)
//            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.item.id.eq(filter.getItem().getId()));

        if (filter.getBuyer().getId() != null)
            predicate = predicate.and(InvoiceHandler.qInvoice.buyer.id.eq(filter.getBuyer().getId()));

        return predicate;
    }
}
