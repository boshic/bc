package barcode.dao.predicates;

import barcode.api.EntityHandler;
import barcode.dao.entities.*;
import barcode.dao.entities.embeddable.QComment;
import barcode.dao.services.AbstractEntityManager;
import barcode.dao.services.EntityHandlerImpl;
import barcode.enums.CommentAction;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.dao.utils.ComingItemFilter;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.ArrayList;
import java.util.List;

public class ComingItemPredicatesBuilder {

    private QComingItem comingItem = QComingItem.comingItem;
    private QItem item  = comingItem.item;
    private QDocument doc = comingItem.doc;
    private QComment qComment = QComment.comment;

    private PredicateBuilder predicateBuilder = new PredicateBuilder();

    public BooleanBuilder buildByFilter(ComingItemFilter filter, AbstractEntityManager abstractEntityManager) {

        BooleanBuilder predicate = new BooleanBuilder();

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(doc.date.between(filter.getFromDate(), filter.getToDate()));

//        List<ComingItem> comings =
//                new JPAQuery<ComingItem>(
//                abstractEntityManager.getEntityManager())
//                    .select(comingItem)
//                    .from(comingItem).innerJoin(comingItem.comments, qComment)
//                        .where(qComment.action.eq(CommentAction.MOVE_COMMENT.getAction())
//                                .and(qComment.date.between(filter.getFromDate(), filter.getToDate()))
//                        )
//            .fetch();

//        predicates.add(comingItem.in(comings));

//        predicate = predicate.and(comingItem.in(comings));

        if(filter.getInventoryModeEnabled() != null && !filter.getInventoryModeEnabled())
            predicate  = predicate.andAnyOf(predicates.stream().toArray(Predicate[]::new));

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(comingItem.stock.id.eq(filter.getStock().getId()));

        if(filter.getSearchString() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), item.name::containsIgnoreCase));

        if(filter.getSectionPart() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSectionPart(), item.section.name::containsIgnoreCase));

        if(!filter.getInventoryModeEnabled()
                && filter.getComment() != null
                && filter.getStrictCommentSearch() != null
                && filter.getComment().length() > 0)
            predicate = predicate.and(
                    predicateBuilder.buildCommentPredicate(filter, comingItem.comments.any().searchString::containsIgnoreCase))
//                    .or(comingItem.comments.size().eq(0))
                    ;

        if(filter.getEan() != null && filter.getEan().length() == 13)
            predicate = predicate.and(item.ean.eq(filter.getEan()));

        if(filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(item.id.eq(filter.getItem().getId()));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(item.section.id.eq(filter.getSection().getId()));

        if (!filter.getInventoryModeEnabled() && filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(doc.supplier.id.eq(filter.getSupplier().getId()));

        if (!filter.getInventoryModeEnabled() && filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(doc.id.eq(filter.getDocument().getId()));

        if(filter.getHideNullQuantity())
            predicate = predicate.and(comingItem.currentQuantity.gt(0));

        return predicate;
    }

    public Predicate buildByFilterForRelease(ComingItemFilter filter) {

        BooleanBuilder predicate = new BooleanBuilder();

        predicate = predicate.and(comingItem.currentQuantity.gt(0));

        if (filter.getStock() != null && !filter.getStock().isAllowAll())
            predicate = predicate.and(comingItem.stock.id.eq(filter.getStock().getId()));

        if(filter.getSearchString() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSearchString(), item.name::containsIgnoreCase));

        if(filter.getSectionPart() != null)
            predicate = predicate.and(predicateBuilder
                    .buildByPhraseAndMethod(filter.getSectionPart(), item.section.name::containsIgnoreCase));

        if(filter.getItem() != null && filter.getItem().getId() != null)
            predicate = predicate.and(item.id.eq(filter.getItem().getId()));

        if (filter.getSection() != null && filter.getSection().getId() != null)
            predicate =
                    predicate.and(item.section.id.eq(filter.getSection().getId()));

        if (filter.getSupplier() != null && filter.getSupplier().getId() != null)
            predicate = predicate.and(doc.supplier.id.eq(filter.getSupplier().getId()));

        if (filter.getDocument() != null && filter.getDocument().getId() != null)
            predicate = predicate.and(doc.id.eq(filter.getDocument().getId()));

        return predicate;
    }

    public BooleanExpression getAvailableItemsByStock(Long itemId, Stock stock) {

        BooleanExpression predicate = comingItem.item.id.eq(itemId);

        if(!stock.isAllowAll())
            predicate = predicate.and(comingItem.stock.id.eq(stock.getId()));

        if(stock.isPriceByAvailable())
            predicate = predicate.and(comingItem.currentQuantity.gt(0));

        return predicate;
    }
}
