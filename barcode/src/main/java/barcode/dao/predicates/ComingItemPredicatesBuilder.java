package barcode.dao.predicates;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.embeddable.Comment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.entities.Stock;
import barcode.dao.services.ComingItemHandler;
import barcode.dao.utils.ComingItemFilter;
import com.querydsl.jpa.JPAExpressions;

import java.util.ArrayList;

public class ComingItemPredicatesBuilder {

    private BooleanExpression predicate;

    private CommentPredicateBuilder commentPredicateBuilder = (comment -> {
        BooleanBuilder builder = new BooleanBuilder();

        for (String word : comment.split(" "))
            builder = builder.and(ComingItemHandler.qComingItem.comment.containsIgnoreCase(word));

        return builder;
    });

    private CommentPredicateBuilder searchStringPredicateBuilder = (comment -> {
        BooleanBuilder builder = new BooleanBuilder();

        for (String word : comment.split(" "))
            builder = builder.and(ComingItemHandler.qComingItem.item.name.containsIgnoreCase(word));

        return builder;
    });

//    private BooleanExpression testCmnt() {
//        BooleanExpression namesFilter = JPAExpressions.select()
//                .from(ComingItemHandler.qComingItem.comments)
//                .where(languageToName.offer.eq(QOffer.offer)
//                        .and(languageToName.name.eq("Esperanto")))
//               .exists();
//        return namesFilter;
//    }

    public BooleanExpression buildByFilter(ComingItemFilter filter) {

        predicate = ComingItemHandler.qComingItem.doc.date.between(filter.getFromDate(), filter.getToDate());

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(ComingItemHandler.qComingItem.stock.id.eq(filter.getStock().getId()));

        if(filter.getSearchString() != null)
            predicate = predicate.and(searchStringPredicateBuilder.build(filter.getSearchString()));
//            predicate = predicate.and(ComingItemHandler.qComingItem.item.name.containsIgnoreCase(filter.getSearchString()));

        if(filter.getSectionPart() != null)
            predicate =
                    predicate.and(ComingItemHandler.qComingItem.item.section.name.containsIgnoreCase(filter.getSectionPart()));

        if(filter.getComment() != null) {
            predicate = predicate.and(commentPredicateBuilder.build(filter.getComment()));
//            predicate = predicate.and(
//                    ComingItemHandler.qComingItem.comments.any().action.containsIgnoreCase("Перемещение")
//
//            );

        }
//            predicate = predicate.and(getCommentPredicate(filter.getComment()));
//            predicate = predicate.and(ComingItemHandler.qComingItem.comment.containsIgnoreCase(filter.getComment()));

        if(filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(ComingItemHandler.qComingItem.item.ean.eq(filter.getEan()));

        if(filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(ComingItemHandler.qComingItem.item.id.eq(filter.getItem().getId()));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(ComingItemHandler.qComingItem.item.section.id.eq(filter.getSection().getId()));

        if (filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(ComingItemHandler.qComingItem.doc.supplier.id.eq(filter.getSupplier().getId()));

        if (filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(ComingItemHandler.qComingItem.doc.id.eq(filter.getDocument().getId()));

        if(filter.getHideNullQuantity())
            predicate = predicate.and(ComingItemHandler.qComingItem.currentQuantity.gt(0));

        return predicate;
    }

    public BooleanExpression getAvailableItemsByStock(Long itemId, Stock stock) {

        predicate = ComingItemHandler.qComingItem.stock.id.eq(stock.getId())
                .and(ComingItemHandler.qComingItem.item.id.eq(itemId));

        if(stock.isPriceByAvailable())
            predicate = predicate.and(ComingItemHandler.qComingItem.currentQuantity.gt(0));

        return predicate;
    }
}
