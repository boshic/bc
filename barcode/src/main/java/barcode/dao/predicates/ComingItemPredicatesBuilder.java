package barcode.dao.predicates;

import barcode.dao.entities.QComingItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.entities.Stock;
import barcode.dao.utils.ComingItemFilter;

public class ComingItemPredicatesBuilder {

    private QComingItem comingItem = QComingItem.comingItem;

    private PredicateBuilder phraseByWordPredicateBuilder = new PredicateBuilderImpl();

    public BooleanExpression buildByFilter(ComingItemFilter filter) {

        BooleanExpression predicate = comingItem.doc.date.between(filter.getFromDate(), filter.getToDate());

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(comingItem.stock.id.eq(filter.getStock().getId()));

        if(filter.getSearchString() != null)
            predicate = predicate.and(phraseByWordPredicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), comingItem.item.name::containsIgnoreCase));

        if(filter.getSectionPart() != null)
            predicate =
                    predicate.and(comingItem.item.section.name.containsIgnoreCase(filter.getSectionPart()));

        if(filter.getComment() != null) {
            predicate = predicate.and(phraseByWordPredicateBuilder
                    .buildByPhraseAndMethod(filter.getComment(), comingItem.comment::containsIgnoreCase));
        }

        if(filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(comingItem.item.ean.eq(filter.getEan()));

        if(filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(comingItem.item.id.eq(filter.getItem().getId()));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(comingItem.item.section.id.eq(filter.getSection().getId()));

        if (filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(comingItem.doc.supplier.id.eq(filter.getSupplier().getId()));

        if (filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(comingItem.doc.id.eq(filter.getDocument().getId()));

        if(filter.getHideNullQuantity())
            predicate = predicate.and(comingItem.currentQuantity.gt(0));

        return predicate;
    }

    public BooleanExpression getAvailableItemsByStock(Long itemId, Stock stock) {

        BooleanExpression predicate = comingItem.stock.id.eq(stock.getId())
                .and(comingItem.item.id.eq(itemId));

        if(stock.isPriceByAvailable())
            predicate = predicate.and(comingItem.currentQuantity.gt(0));

        return predicate;
    }
}
