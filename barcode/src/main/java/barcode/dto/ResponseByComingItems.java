package barcode.dto;

import barcode.dao.entities.*;
import barcode.dao.entities.embeddable.QInventoryRow;
import barcode.dao.services.AbstractEntityManager;
import barcode.dao.services.ComingItemHandler;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class ResponseByComingItems extends ResponseItemExt<ComingItem> {

    final private String
        SUMM_BY_SELECTION = SUMM + BY_SELECTION,
        QUANTITY_BY_SELECTION_PRICE_IN = QUANTITY + BY_SELECTION + PRICE_IN,
        QUANTITY_BY_FACT_PRICE_IN = QUANTITY + BY_FACT + PRICE_IN,
        QUANTITY_BY_SELECTION_PRICE_OUT = QUANTITY + BY_SELECTION + PRICE_OUT,
        QUANTITY_BY_FACT_PRICE_OUT = QUANTITY + BY_FACT + PRICE_OUT,
        SUMM_BY_FACT = SUMM + BY_FACT;

    QComingItem comingItem = QComingItem.comingItem;

    public ResponseByComingItems(
            String text, List<ComingItem> items, Boolean success, Integer numberOfPages) {

        super(text, items, success, numberOfPages);

    }

    @Override
    public void calcTotals (AbstractEntityManager abstractEntityManager, BooleanBuilder predicate) {

        JPAQuery<BigDecimal> query = new JPAQuery<BigDecimal>(abstractEntityManager.getEntityManager());
        query = query.from(comingItem).where(predicate);

        Expression<BigDecimal> pricesSelection = new CaseBuilder()
            .when(comingItem.item.price.gt(0))
            .then(comingItem.item.price)
            .otherwise(comingItem.priceOut);

        BigDecimal
            quantity = query.select(comingItem.quantity.sum()).fetchOne(),
            currentQuantity = query.select(comingItem.currentQuantity.sum()).fetchOne(),
            sum = query.select(comingItem.quantity.multiply(comingItem.priceIn).sum()).fetchOne(),
            currentSum =  query.select(comingItem.sum.sum()).fetchOne(),
            sumOut = query.select(comingItem.quantity.multiply(pricesSelection).sum()).fetchOne(),
            currentSumOut = query.select(comingItem.currentQuantity.multiply(pricesSelection).sum()).fetchOne();

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_FACT_PRICE_OUT, currentQuantity, SUMM_BY_FACT , currentSumOut));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_SELECTION_PRICE_IN, quantity, SUMM_BY_SELECTION , sum));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_FACT_PRICE_IN, currentQuantity, SUMM_BY_FACT , currentSum));

        assert sum != null;
        assert quantity != null;
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD_BY_PRICE_IN, quantity.subtract(currentQuantity == null ? BigDecimal.ZERO : currentQuantity),
                SUMM , sum.subtract(currentSum == null ? BigDecimal.ZERO : currentSum)));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_SELECTION_PRICE_OUT, quantity, SUMM_BY_SELECTION , sumOut));

        assert sumOut != null;
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD_BY_PRICE_OUT, quantity.subtract(currentQuantity == null ? BigDecimal.ZERO : currentQuantity),
                SUMM , sumOut.subtract(currentSumOut == null ? BigDecimal.ZERO : currentSumOut)));

        super.setSections(
            new JPAQuery<ItemSection>(abstractEntityManager.getEntityManager())
                .from(comingItem).where(predicate)
                .select(comingItem.item.section)
                .distinct().orderBy(comingItem.item.section.name.asc()).fetch());

        super.setSuppliers(
            new JPAQuery<Supplier>(abstractEntityManager.getEntityManager())
                .from(comingItem).where(predicate)
                .select(comingItem.doc.supplier)
                .distinct().orderBy(comingItem.doc.supplier.name.asc()).fetch());

    }

    //_deprecated. must be deleted1
//    @Override
//    public void calcTotals(List<ComingItem> comings) {
//
////        final String
////                SUMM_BY_SELECTION = SUMM + BY_SELECTION,
////                QUANTITY_BY_SELECTION_PRICE_IN = QUANTITY + BY_SELECTION + PRICE_IN,
////                QUANTITY_BY_FACT_PRICE_IN = QUANTITY + BY_FACT + PRICE_IN,
////                QUANTITY_BY_SELECTION_PRICE_OUT = QUANTITY + BY_SELECTION + PRICE_OUT,
////                QUANTITY_BY_FACT_PRICE_OUT = QUANTITY + BY_FACT + PRICE_OUT,
////                SUMM_BY_FACT = SUMM + BY_FACT;
//
//
//        BigDecimal quantity = BigDecimal.ZERO,
//                    currentQuantity = BigDecimal.ZERO,
//                    sum = BigDecimal.ZERO, currentSum = BigDecimal.ZERO,
//                    sumOut = BigDecimal.ZERO, currentSumOut = BigDecimal.ZERO;
//
//        for(ComingItem comingItem: comings)  {
//
//            BigDecimal priceOut = comingItem.getItem().getPrice().compareTo(BigDecimal.ZERO) > 0 ?
//                    comingItem.getItem().getPrice() : comingItem.getPriceOut();
//
//            currentQuantity = currentQuantity.add(comingItem.getCurrentQuantity());
//
//            currentSum = currentSum.add(comingItem.getSum());
//
//            currentSumOut = currentSumOut
//                    .add(priceOut.multiply(comingItem.getCurrentQuantity()))
//                    .setScale(2, BigDecimal.ROUND_HALF_UP);
//
//            quantity = quantity.add(comingItem.getQuantity());
//
//            sum = sum.add(comingItem.getPriceIn().multiply(comingItem.getQuantity()))
//                    .setScale(2, BigDecimal.ROUND_HALF_UP);
//
//            sumOut = sumOut
//                    .add(priceOut.multiply( comingItem.getQuantity()))
//                    .setScale(2, BigDecimal.ROUND_HALF_UP);
//        }
//
//        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
//                (QUANTITY_BY_FACT_PRICE_OUT, currentQuantity, SUMM_BY_FACT , currentSumOut));
//
//        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
//                (QUANTITY_BY_SELECTION_PRICE_IN, quantity, SUMM_BY_SELECTION , sum));
//
//        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
//                (QUANTITY_BY_FACT_PRICE_IN, currentQuantity, SUMM_BY_FACT , currentSum));
//
//        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
//                (SOLD_BY_PRICE_IN, quantity.subtract(currentQuantity),
//                        SUMM , sum.subtract(currentSum)));
//
//        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
//                (QUANTITY_BY_SELECTION_PRICE_OUT, quantity, SUMM_BY_SELECTION , sumOut));
//
//        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
//                (SOLD_BY_PRICE_OUT, quantity.subtract(currentQuantity),
//                        SUMM , sumOut.subtract(currentSumOut)));
//
//        super.setSuppliersByComings(comings);
//
//        super.setSectionsByComings(comings);
//
//    }

    public void calcInventoryTotalsNew(AbstractEntityManager abstractEntityManager,
                                       BooleanBuilder predicate, Long stockId) {

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

        qFromComingItem = qFromComingItem.from(comingItem).where(predicate);
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
                    .select(comingItem.item.id)
                    .from(comingItem).where(predicate)
                    .distinct()
            ))
            .leftJoin(qItem.inventoryRows, inventoryRow)
            .leftJoin(inventoryRow.stock, qStock)
            .where(qStock.id.eq(stockId));

        BigDecimal
            quantity = qFromComingItem.select(comingItem.currentQuantity.sum()).fetchOne(),
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
                SUMM_IN , currentSum.subtract(sum == null ? BigDecimal.ZERO : sum)));

        currentSumOut = currentSumOut == null ? BigDecimal.ZERO : currentSumOut;
        quantity = quantity == null ? BigDecimal.ZERO : quantity;
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (DEVIATION, currentQuantity.subtract(quantity),
                SUMM_OUT , currentSumOut.subtract(sumOut == null ? BigDecimal.ZERO : sumOut)));


    }

    public void calcInventoryTotals(List<ComingItem> comings) {

        final String
                DEVIATION = "Расхождение, кол.",
                SUMM_OUT = SUMM + PRICE_OUT,
                SUMM_IN = SUMM + PRICE_IN;
        ;

        BigDecimal
            quantity = comings.stream().map(ComingItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add),
                currentQuantity = comings.stream().map(ComingItem::getCurrentQuantity).reduce(BigDecimal.ZERO, BigDecimal::add),
                sum = comings.stream().map(ComingItem::getSum).reduce(BigDecimal.ZERO, BigDecimal::add),
                currentSum = comings.stream()
                        .map(c -> c.getCurrentQuantity().multiply(c.getPriceIn()).setScale(2, BigDecimal.ROUND_HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),

                sumOut = comings.stream()
                        .map(c -> c.getQuantity()
                                .multiply(ComingItemHandler.getComingPrice(c)).setScale(2, BigDecimal.ROUND_HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),


                currentSumOut = comings.stream()
                        .map(c -> c.getCurrentQuantity().multiply(ComingItemHandler.getComingPrice(c)).setScale(2, BigDecimal.ROUND_HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);


        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                (DEVIATION, currentQuantity.subtract(quantity),
                        SUMM_IN , currentSum.subtract(sum)));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                (DEVIATION, currentQuantity.subtract(quantity),
                        SUMM_OUT , currentSumOut.subtract(sumOut)));

        super.setSectionsByComings(comings);

    }

}