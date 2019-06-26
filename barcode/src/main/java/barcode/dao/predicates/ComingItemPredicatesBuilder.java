package barcode.dao.predicates;

import barcode.dao.entities.QComingItem;
import barcode.dao.entities.QDocument;
import barcode.dao.entities.QItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.entities.Stock;
import barcode.dao.utils.ComingItemFilter;

public class ComingItemPredicatesBuilder {

    private QComingItem comingItem = QComingItem.comingItem;
    private QItem item  = comingItem.item;
    private QDocument doc = comingItem.doc;

    private PredicateBuilder predicateBuilder = new PredicateBuilderImpl();

    public BooleanExpression buildByFilter(ComingItemFilter filter) {

        BooleanExpression predicate = doc.date.between(filter.getFromDate(), filter.getToDate());

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(comingItem.stock.id.eq(filter.getStock().getId()));

        if(filter.getSearchString() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), item.name::containsIgnoreCase));

        if(filter.getSectionPart() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSectionPart(), item.section.name::containsIgnoreCase));

//            predicate =
//                    predicate.and(item.section.name.containsIgnoreCase(filter.getSectionPart()));

        if(filter.getComment() != null) {
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getComment(), comingItem.comment::containsIgnoreCase));
        }

        if(filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(item.ean.eq(filter.getEan()));

        if(filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(item.id.eq(filter.getItem().getId()));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(item.section.id.eq(filter.getSection().getId()));

        if (filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(doc.supplier.id.eq(filter.getSupplier().getId()));

        if (filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(doc.id.eq(filter.getDocument().getId()));

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
