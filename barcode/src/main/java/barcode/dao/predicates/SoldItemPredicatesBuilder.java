package barcode.dao.predicates;

import barcode.dao.entities.QComingItem;
import barcode.dao.entities.QDocument;
import barcode.dao.entities.QItem;
import barcode.dao.entities.QSoldItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.utils.SoldItemFilter;

public class SoldItemPredicatesBuilder {

    private QSoldItem soldItem = QSoldItem.soldItem;
    private QComingItem comingItem = soldItem.coming;
    private QItem item = comingItem.item;
    private QDocument doc = comingItem.doc;

    private PredicateBuilder predicateBuilder = new PredicateBuilderImpl();

    public BooleanExpression buildByFilter(SoldItemFilter filter) {

        BooleanExpression predicate = soldItem.date.between(filter.getFromDate(), filter.getToDate());

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(comingItem.stock.id.eq(filter.getStock().getId()));

        if(filter.getComment() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getComment(), soldItem.comment::containsIgnoreCase));

        if(filter.getSearchString() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), item.name::containsIgnoreCase));

        if (filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(item.ean.eq(filter.getEan()));

        if (filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(item.id.eq(filter.getItem().getId()));

        if (filter.getCompositeItem() != null && filter.getCompositeItem().getId() != null)
            predicate = predicate.and(soldItem.soldCompositeItem.item.id.eq(filter.getCompositeItem().getId()));

        if (filter.getBuyer().getId() != null)
            predicate = predicate.and(soldItem.buyer.id.eq(filter.getBuyer().getId()));

        if(filter.getSectionPart() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSectionPart(), item.section.name::containsIgnoreCase));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(item.section.id.eq(filter.getSection().getId()));

        if (filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(doc.supplier.id.eq(filter.getSupplier().getId()));

        if (filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(doc.id.eq(filter.getDocument().getId()));

        if(filter.getHideNullQuantity())
            predicate = predicate.and(soldItem.quantity.gt(0));

        return predicate;
    }

}
