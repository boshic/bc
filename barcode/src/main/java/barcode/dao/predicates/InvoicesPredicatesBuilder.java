package barcode.dao.predicates;

import barcode.dao.entities.QInvoice;

import barcode.dao.entities.embeddable.QInvoiceRow;
import barcode.dao.services.AbstractEntityManager;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.services.InvoiceHandler;
import barcode.dao.utils.SoldItemFilter;

/**
 * Created by xlinux on 20.11.18.
 */

public class InvoicesPredicatesBuilder {

    public BooleanExpression buildByFilter(SoldItemFilter filter, AbstractEntityManager abstractEntityManager) {

        QInvoiceRow qInvoiceRow = QInvoiceRow.invoiceRow;
        QInvoice invoice = InvoiceHandler.qInvoice;

        BooleanExpression predicate
                = (invoice.date.between(filter.getFromDate(), filter.getToDate()));


        ///!!!! Yes

//        List<Invoice> rows = new JPAQuery<Invoice>(
//                abstractEntityManager.getEntityManager())
//                .select(invoice)
//                .from(invoice).
//                .innerJoin(invoice.invoiceRows, qInvoiceRow)
//                .where(invoice.date.between(filter.getFromDate(), filter.getToDate())
//                        .and(qInvoiceRow.quantity.gt(2))
//                        .and(qInvoiceRow.price.gt(5)))
//                .fetch();
//
//        predicate = predicate.and(invoice.in(rows));

//        predicate = predicate.and(invoice.invoiceRows.size().gt(4));

        if (filter.getBuyer().getId() != null)
            predicate = predicate.and(invoice.buyer.id.eq(filter.getBuyer().getId()));

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(invoice.stock.id.eq(filter.getStock().getId()));

//        predicate = predicate.and(invoice.invoiceRows.size().eq(0));

        return predicate;

//        BooleanExpression predicate
//                =(InvoiceHandler.qInvoice.date.between(filter.getFromDate(), filter.getToDate()));

//        return predicate;

//        JPAQuery<Invoice> query = new JPAQuery<Invoice>(em);

//        List<Invoice> inv = query
//                .from(invoice)
////                .where(invoice.date.after(filter.getFromDate()))
//                .fetch();

//        List<Invoice> rows = new JPAQuery<Invoice>()
//                .select(invoice)
//                .from(invoice)
////                .innerJoin(invoice.invoiceRows, qInvoiceRow)
////                .where(qInvoiceRow.quantity.gt(2))
//                .fetch();

//        List<InvoiceRow> rows = new JPAQuery<InvoiceRow>()
//                .from(InvoiceHandler.qInvoice)
////                .innerJoin(InvoiceHandler.qInvoice.invoiceRows, qInvoiceRow)
////                .where(qInvoiceRow.quantity.gt(2))
////                .select(qInvoiceRow)
//                .fetch();

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

    }
}
