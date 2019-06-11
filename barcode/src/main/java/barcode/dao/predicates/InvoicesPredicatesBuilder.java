package barcode.dao.predicates;

import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;

import barcode.dao.entities.embeddable.InvoiceRow;
import barcode.dao.entities.embeddable.QInvoiceRow;
import barcode.dao.services.AbstractEntityManager;
import barcode.dao.services.AbstractEntityManagerImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.services.InvoiceHandler;
import barcode.dao.utils.SoldItemFilter;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by xlinux on 20.11.18.
 */

public class InvoicesPredicatesBuilder {

//    private AbstractEntityManager abstractEntityManager;
//
//    public InvoicesPredicatesBuilder() {}
//
//    public InvoicesPredicatesBuilder(AbstractEntityManager abstractEntityManager) {
//        this.abstractEntityManager = abstractEntityManager;
//    }

    public BooleanExpression buildByFilter(SoldItemFilter filter, AbstractEntityManager abstractEntityManager) {

        QInvoiceRow qInvoiceRow = QInvoiceRow.invoiceRow;
        QInvoice invoice = InvoiceHandler.qInvoice;

        abstractEntityManager.test();

        EntityManager em = abstractEntityManager.getEntityManager();

        BooleanExpression predicate
                = (InvoiceHandler.qInvoice.date.between(filter.getFromDate(), filter.getToDate()));


        ///!!!! Yes
//        List<Invoice> invoiceRows = new JPAQuery<Invoice>(
//                abstractEntityManager.getEntityManager())
//                .select(invoice)
//                .from(invoice)
//                .innerJoin(invoice.invoiceRows, qInvoiceRow)
//                .where(qInvoiceRow.quantity.gt(100).and(qInvoiceRow.price.gt(5)))
//                .fetch();

        List<Invoice> rows = new JPAQuery<Invoice>(
                abstractEntityManager.getEntityManager())
                .select(invoice)
                .from(invoice)
                .innerJoin(invoice.invoiceRows, qInvoiceRow)
                .where(qInvoiceRow.quantity.gt(3)
                        .and(qInvoiceRow.price.gt(10)))
                .fetch();

        predicate = predicate.and(invoice.in(rows));

//        predicate = predicate.and(invoice.invoiceRows.any()
//                .in(
//                        new JPAQuery<InvoiceRow>(abstractEntityManager.getEntityManager())
//                .select(qInvoiceRow)
//                .from(invoice)
////                .innerJoin(invoice.invoiceRows, qInvoiceRow)
//                                .where(qInvoiceRow.quantity.gt(2))
//                ));

//                (
//                new JPAQuery<>(abstractEntityManager.getEntityManager())
//                .select(invoice)
//                .from(invoice)
//                .innerJoin(invoice.invoiceRows, qInvoiceRow)
//                .where(qInvoiceRow.quantity.gt(2)).fetch()
//        ));


        return predicate;

//        JPAExpressions
//                .select(InvoiceHandler.qInvoice)
//                .from(invoice)
//                .where(invoice.sum.goe(20))


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

//        BooleanExpression subq = InvoiceHandler.qInvoice.invoiceRows.any().in(rows);

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

//        if (filter.getBuyer().getId() != null)
//            predicate = predicate.and(InvoiceHandler.qInvoice.buyer.id.eq(filter.getBuyer().getId()));

    }
}
