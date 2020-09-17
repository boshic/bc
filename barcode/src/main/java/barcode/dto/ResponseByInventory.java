package barcode.dto;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.QComingItem;
import barcode.dao.entities.QItem;
import barcode.dao.entities.QStock;
import barcode.dao.entities.embeddable.QInventoryRow;
import barcode.dao.predicates.ComingItemPredicatesBuilder;
import barcode.dao.services.AbstractEntityManager;
import barcode.utils.ComingItemFilter;
import barcode.utils.CommonUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class ResponseByInventory
    extends ResponseItemExt<ComingItem>
    implements ResponseWithTotals<ComingItemFilter> {

    public ResponseByInventory() {}
    public ResponseByInventory(String text,
                               List<ComingItem> items,
                               Boolean success,
                               Integer numberOfPages) {
        super(text, items, success, numberOfPages);
    }

    @Override
    public void calcTotals(AbstractEntityManager abstractEntityManager, ComingItemFilter filter) {

        QInventoryRow inventoryRow = QInventoryRow.inventoryRow;
        QItem qItem = QItem.item;
        QStock qStock = QStock.stock;
        QComingItem qComingItem = QComingItem.comingItem;

        ComingItemPredicatesBuilder cipb = new ComingItemPredicatesBuilder();
        BooleanBuilder predicate = cipb.buildByFilter(filter, abstractEntityManager);

        final String
            DEVIATION = "Расхождение, кол.",
            SUMM_OUT = SUMM + PRICE_OUT,
            SUMM_IN = SUMM + PRICE_IN;

        EntityManager em = abstractEntityManager.getEntityManager();
        JPAQuery<BigDecimal> qFromComingItem = new JPAQuery<BigDecimal>(em);
        JPAQuery<BigDecimal> qFromItem = new JPAQuery<BigDecimal>(em);

        Expression<BigDecimal> casePriceOut = cipb.getPriceOutCase(qComingItem);

        qFromComingItem = qFromComingItem.from(qComingItem).where(predicate);

        qFromItem = qFromItem
            .from(qItem)
            .where(qItem.id.in(
                JPAExpressions
                    .select(qComingItem.item.id)
                    .from(qComingItem).where(predicate)
                    .distinct()
            ))
            .leftJoin(qItem.inventoryRows, inventoryRow)
            .leftJoin(inventoryRow.stock, qStock)
            .where(qStock.id.eq(filter.getStock().getId()));

        BigDecimal
            quantity = CommonUtils.validateBigDecimal(qFromComingItem.select(qComingItem.currentQuantity.sum()).fetchOne()),
            inventoryQuantity = CommonUtils.validateBigDecimal(qFromItem.select(inventoryRow.quantity.sum()).fetchOne()),
            sum = CommonUtils.validateBigDecimal(qFromComingItem.select(qComingItem.sum.sum()).fetchOne()),
            sumOut = CommonUtils.validateBigDecimal(
                qFromComingItem.select(qComingItem.currentQuantity.multiply(casePriceOut).sum()).fetchOne()),

            inventorySum = CommonUtils.validateBigDecimal(
                qFromItem.select(inventoryRow.quantity.multiply(
                    cipb.getSubQueryFromComingItemsForInventoryTotals(
                        qComingItem, qItem, filter.getStock().getId(), cipb.getMaxPriceInForInventoryCase(qComingItem))
                ).sum()).fetchOne()),

            ivnentorySumOut = CommonUtils.validateBigDecimal(
                qFromItem.select(inventoryRow.quantity.multiply(
                    cipb.getSubQueryFromComingItemsForInventoryTotals(
                        qComingItem, qItem, filter.getStock().getId(),
                        new CaseBuilder()
                            .when(qItem.price.gt(0))
                            .then(qItem.price).otherwise(qComingItem.priceOut.max()))
                ).sum()).fetchOne());

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (DEVIATION, inventoryQuantity.subtract(quantity), SUMM_IN, inventorySum.subtract(sum)));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (DEVIATION, inventoryQuantity.subtract(quantity), SUMM_OUT, ivnentorySumOut.subtract(sumOut)));
    }
}
