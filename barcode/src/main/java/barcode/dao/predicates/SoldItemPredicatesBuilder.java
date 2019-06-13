package barcode.dao.predicates;

import barcode.dao.entities.QSoldItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.utils.SoldItemFilter;

public class SoldItemPredicatesBuilder {

    private QSoldItem soldItem = QSoldItem.soldItem;

    private PredicateBuilder phraseByWordPredicateBuilder = new PredicateBuilderImpl();

    public BooleanExpression buildByFilter(SoldItemFilter filter) {

        BooleanExpression predicate = soldItem.date.between(filter.getFromDate(), filter.getToDate());

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(soldItem.coming.stock.id.eq(filter.getStock().getId()));

        if(filter.getComment() != null)
            predicate = predicate.and(phraseByWordPredicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), soldItem.comment::containsIgnoreCase));

        if(filter.getSearchString() != null)
            predicate = predicate.and(phraseByWordPredicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), soldItem.coming.item.name::containsIgnoreCase));

        if (filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(soldItem.coming.item.ean.eq(filter.getEan()));

        if (filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(soldItem.coming.item.id.eq(filter.getItem().getId()));

        if (filter.getBuyer().getId() != null)
            predicate = predicate.and(soldItem.buyer.id.eq(filter.getBuyer().getId()));

        if(filter.getSectionPart() != null)
            predicate =
                    predicate.and(soldItem.coming.item.section.name.containsIgnoreCase(filter.getSectionPart()));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(soldItem.coming.item.section.id.eq(filter.getSection().getId()));

        if (filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(soldItem.coming.doc.supplier.id.eq(filter.getSupplier().getId()));

        if (filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(soldItem.coming.doc.id.eq(filter.getDocument().getId()));


        return predicate;
    }

}
