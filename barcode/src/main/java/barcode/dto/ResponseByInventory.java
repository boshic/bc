package barcode.dto;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.QItem;
import barcode.dao.entities.QStock;
import barcode.dao.entities.embeddable.QInventoryRow;
import barcode.dao.services.AbstractEntityManager;
import barcode.utils.ComingItemFilter;
import com.querydsl.core.BooleanBuilder;
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
    public void calcTotals(AbstractEntityManager abstractEntityManager,
                                       BooleanBuilder predicate,
                                       ComingItemFilter filter) {

        QInventoryRow inventoryRow = QInventoryRow.inventoryRow;
        QItem qItem = QItem.item;
        QStock qStock = QStock.stock;

        final String
            DEVIATION = "Расхождение, кол.",
            SUMM_OUT = SUMM + PRICE_OUT,
            SUMM_IN = SUMM + PRICE_IN;

        EntityManager em = abstractEntityManager.getEntityManager();
        JPAQuery<BigDecimal> qFromComingItem = new JPAQuery<BigDecimal>(em);
        JPAQuery<BigDecimal> qFromItem = new JPAQuery<BigDecimal>(em);

        qFromComingItem = qFromComingItem.from(qComingItem).where(predicate);
//        qFromItem = qFromItem
//            .from(comingItem)
//            .where(predicate)
//            .innerJoin(comingItem.item, qItem)
//            .where(qItem.id.eq(comingItem.item.id))
//            .join(qItem.inventoryRows, inventoryRow)
//            .join(inventoryRow.stock, qStock)
//            .where(qStock.id.eq(stockId));
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
            quantity = qFromComingItem.select(qComingItem.currentQuantity.sum()).fetchOne(),
            currentQuantity = qFromItem.select(inventoryRow.quantity.sum()).fetchOne(),
//            currentQuantity = new JPAQuery<BigDecimal>(abstractEntityManager.getEntityManager())
//                .select(inventoryRow.quantity.sum())
//                .from(qItem)
//                .where(qItem.id.in(
//                    JPAExpressions
//                    .select(comingItem.item.id)
//                    .from(comingItem).where(predicate)
//                    .distinct()
//                ))
//                .leftJoin(qItem.inventoryRows, inventoryRow)
//                .leftJoin(inventoryRow.stock, qStock)
//                .where(qStock.id.eq(stockId))
//                .fetchOne(),
            sum = BigDecimal.ZERO,
//                qFromComingItem.select(comingItem.sum.sum()).fetchOne(),
            currentSum = BigDecimal.ZERO,
//                qFromItem.select(inventoryRow.quantity.multiply(comingItem.priceIn).sum()).fetchOne(),
            sumOut = BigDecimal.ZERO,
//                = query.select(comingItem.currentQuantity.multiply(comingItem.priceIn).sum()).fetchOne(),
            currentSumOut = BigDecimal.ZERO;
//                = query.select(
//                JPAExpressions
//                    .select(inventoryRow.quantity.multiply(comingItem.priceIn.max()).sum())
//                    .from(qItem)
//                    .join(qItem.inventoryRows, inventoryRow)
//                    .join(inventoryRow.stock, qStock)
//                    .where(qStock.id.eq(stockId))
//            ).fetchOne();

        currentSum = currentSum == null ? BigDecimal.ZERO : currentSum;
        currentQuantity = currentQuantity == null ? BigDecimal.ZERO : currentQuantity;
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (DEVIATION, currentQuantity.subtract(quantity == null ? BigDecimal.ZERO : quantity),
                SUMM_IN, currentSum.subtract(sum == null ? BigDecimal.ZERO : sum)));

        currentSumOut = currentSumOut == null ? BigDecimal.ZERO : currentSumOut;
        quantity = quantity == null ? BigDecimal.ZERO : quantity;
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (DEVIATION, currentQuantity.subtract(quantity),
                SUMM_OUT, currentSumOut.subtract(sumOut == null ? BigDecimal.ZERO : sumOut)));
    }
}
