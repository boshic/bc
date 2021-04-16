package barcode.dao.predicates;

import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;

import barcode.dao.entities.QItem;
import barcode.dao.entities.embeddable.QInvoiceRow;
import barcode.dao.services.AbstractEntityManager;
import com.querydsl.core.BooleanBuilder;
import barcode.dao.services.InvoiceHandler;
import barcode.utils.SoldItemFilter;
import com.querydsl.jpa.impl.JPAQuery;

/**
 * Created by xlinux on 20.11.18.
 */

public class InvoicesPredicatesBuilder {

    private PredicateBuilder predicateBuilder = new PredicateBuilder();

    public BooleanBuilder buildByFilter(SoldItemFilter filter, AbstractEntityManager abstractEntityManager) {

        QInvoiceRow qInvoiceRow = QInvoiceRow.invoiceRow;
        QInvoice invoice = InvoiceHandler.qInvoice;
        QItem qItem = QItem.item;
        BooleanBuilder predicate = new BooleanBuilder();
        JPAQuery<Invoice> invoiceRowItemQuery = new JPAQuery<Invoice>(abstractEntityManager.getEntityManager());
        invoiceRowItemQuery = invoiceRowItemQuery
                                .select(invoice)
                                .from(invoice)
                                    .leftJoin(invoice.invoiceRows, qInvoiceRow)
                                    .leftJoin(qInvoiceRow.item, qItem);

        predicate.and(invoice.date.between(filter.getFromDate(), filter.getToDate()));

        if(filter.getHideNullQuantity())
            predicate = predicate.and(invoice.isDeleted.isFalse());

        if (filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(invoice.in(
                invoiceRowItemQuery.where(qItem.id.eq(filter.getItem().getId())).fetch()
            ));

        if(filter.getSearchString() != null)
            predicate = predicate.and(invoice.in(
                invoiceRowItemQuery
                    .where(predicateBuilder
                            .buildByPhraseAndMethod(filter.getSearchString(), qItem.name::containsIgnoreCase))
                    .fetch()
            ));

        if (filter.getBuyer().getId() != null)
            predicate = predicate.and(invoice.buyer.id.eq(filter.getBuyer().getId()));

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(invoice.stock.id.eq(filter.getStock().getId()));


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
