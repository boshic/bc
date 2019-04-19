package barcode.dao.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.services.SoldItemHandler;
import barcode.dao.utils.SoldItemFilter;

public class SoldItemPredicatesBuilder {

//    QSoldItem qSoldItem = QSoldItem.soldItem;

//    QComingItem qComingItem = QComingItem.comingItem;

    public BooleanExpression buildByFilter(SoldItemFilter filter) {

        BooleanExpression predicate = SoldItemHandler.qSoldItem.date.between(filter.getFromDate(), filter.getToDate());

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.stock.id.eq(filter.getStock().getId()));

        if(filter.getComment() != null)
            predicate = predicate.and(SoldItemHandler.qSoldItem.comment.containsIgnoreCase(filter.getComment()));

//        if(filter.getComment() != null) {
//            predicate = predicate.and(SoldItemHandler.qSoldItem.comments.any().text.containsIgnoreCase(filter.getComment()));
//            predicate = predicate.or(SoldItemHandler.qSoldItem.comments.any().action.containsIgnoreCase(filter.getComment()));
//        }

        if(filter.getSearchString() != null)
            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.item.name.containsIgnoreCase(filter.getSearchString()));

        if (filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.item.ean.eq(filter.getEan()));

        if (filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.item.id.eq(filter.getItem().getId()));

        if (filter.getBuyer().getId() != null)
            predicate = predicate.and(SoldItemHandler.qSoldItem.buyer.id.eq(filter.getBuyer().getId()));

        if(filter.getSectionPart() != null)
            predicate =
                    predicate.and(SoldItemHandler.qSoldItem.coming.item.section.name.containsIgnoreCase(filter.getSectionPart()));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(SoldItemHandler.qSoldItem.coming.item.section.id.eq(filter.getSection().getId()));

        if (filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.doc.supplier.id.eq(filter.getSupplier().getId()));

        if (filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(SoldItemHandler.qSoldItem.coming.doc.id.eq(filter.getDocument().getId()));


        return predicate;
    }

}
