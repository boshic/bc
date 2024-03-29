package barcode.dao.predicates;

import barcode.dao.entities.*;
import barcode.dao.entities.embeddable.QComment;
import barcode.dao.entities.embeddable.QInventoryRow;
import barcode.dao.services.AbstractEntityManager;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import barcode.utils.ComingItemFilter;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

import java.math.BigDecimal;
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

        JPAQuery<ComingItem> commentJPAQuery = new JPAQuery<ComingItem>(abstractEntityManager.getEntityManager());
        commentJPAQuery = commentJPAQuery
            .select(comingItem).from(comingItem)
            .leftJoin(comingItem.comments, qComment);

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
                comingItem.in(commentJPAQuery
                    .where(predicateBuilder.buildCommentPredicate(filter, qComment.searchString::containsIgnoreCase))));

        // -- Search by all comments in coming! Instead search by one comment.
//            predicate = predicate.and(
//                    predicateBuilder.buildCommentPredicate(filter,
//                        comingItem.comments.any().searchString::containsIgnoreCase));

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

   public <T> JPQLQuery<T>
   getSubQueryFromInventoryRowsByComingAndStockId(QComingItem comingItem,
                                                QInventoryRow inventoryRow,
                                                Long stockId,
                                                Expression<T> path) {

        QItem qItem  = QItem.item;
        QStock qStock = QStock.stock;

        return JPAExpressions
                .select(path)
                .from(qItem)
                .where(qItem.id.eq(comingItem.item.id))
                .join(qItem.inventoryRows, inventoryRow)
                .join(inventoryRow.stock, qStock)
                .where(qStock.id.eq(stockId));
    }

    public <T> JPQLQuery<T>
    getSubQueryFromComingItemsForInventoryTotals(QComingItem qComingItem,
                                                   QItem qItem,
                                                   Long stockId,
                                                   Expression<T> path) {
        return JPAExpressions
            .select(path)
            .from(qComingItem)
            .where(qComingItem.item.id.eq(qItem.id)
                .and(qComingItem.stock.id.eq(stockId)));
    }

    public Expression<BigDecimal> getMaxPriceInForInventoryCase (QComingItem qComingItem) {

        return new CaseBuilder()
            .when(qComingItem.sum.sum().gt(0).and(qComingItem.currentQuantity.sum().gt(0)))
            .then(qComingItem.sum.sum().divide(qComingItem.currentQuantity.sum()))
            .when(qComingItem.sum.sum().gt(0).and(qComingItem.currentQuantity.sum().loe(0)))
            .then(BigDecimal.ZERO)
            .otherwise(qComingItem.priceIn.max());
    }

    public Expression<BigDecimal> getPriceOutCase (QComingItem qComingItem) {

        return new CaseBuilder()
            .when(qComingItem.item.price.gt(0))
            .then(qComingItem.item.price)
            .otherwise(qComingItem.priceOut);
    }

    public JPAQuery<BigDecimal>
    getQueryForMaxItemPriceOutByIdAndStockId(AbstractEntityManager abstractEntityManager,
                                          Long itemId, Long stockId) {
        QComingItem qComingItem = QComingItem.comingItem;

        return new JPAQuery<BigDecimal>(abstractEntityManager.getEntityManager())
            .select(new CaseBuilder()
                .when(qComingItem.item.price.gt(0))
                .then(qComingItem.item.price)
                .otherwise(qComingItem.priceOut.max()))
            .from(qComingItem)
            .where(qComingItem.item.id.eq(itemId).and(qComingItem.stock.id.eq(stockId)));

    }





}
