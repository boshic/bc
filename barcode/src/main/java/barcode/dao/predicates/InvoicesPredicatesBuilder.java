package barcode.dao.predicates;

import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;

import barcode.dao.entities.QItem;
import barcode.dao.entities.QItemSection;
import barcode.dao.entities.embeddable.QInvoiceRow;
import com.querydsl.core.BooleanBuilder;
import barcode.dao.services.InvoiceHandler;
import barcode.utils.SoldItemFilter;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xlinux on 20.11.18.
 */

public class InvoicesPredicatesBuilder {

    private PredicateBuilder predicateBuilder = new PredicateBuilder();

    public BooleanBuilder buildByFilter(SoldItemFilter filter, EntityManager em) {

        List<Invoice> invoices = new ArrayList<>();
        QInvoiceRow qInvoiceRow = QInvoiceRow.invoiceRow;
        QInvoice invoice = InvoiceHandler.qInvoice;
        QItem qItem = QItem.item;
        QItemSection qItemSection = QItemSection.itemSection;
        BooleanBuilder predicate = new BooleanBuilder();
        JPAQuery<Invoice> invoiceRowItemQuery = new JPAQuery<Invoice>(em);
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
                invoiceRowItemQuery.where(qItem.id.eq(filter.getItem().getId()))));

        if(filter.getSearchString() != null && filter.getSearchString().length() > 0) {
            predicate = predicate.and(invoice.in(invoiceRowItemQuery
                .where(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), qItem.name::containsIgnoreCase))));
        }

        if(filter.getSectionPart() != null && filter.getSectionPart().length() > 0)
            predicate = predicate.and(invoice.in(
                invoiceRowItemQuery
                    .leftJoin(qItem.section, qItemSection)
                    .where(predicateBuilder
                        .buildByPhraseAndMethod(filter.getSectionPart(), qItemSection.name::containsIgnoreCase))
            ));

        if (filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(invoice.in(
                invoiceRowItemQuery.where(qItem.ean.eq(filter.getEan()))));

        if (filter.getBuyer().getId() != null)
            predicate = predicate.and(invoice.buyer.id.eq(filter.getBuyer().getId()));

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(invoice.stock.id.eq(filter.getStock().getId()));


        return predicate;


    }
}
