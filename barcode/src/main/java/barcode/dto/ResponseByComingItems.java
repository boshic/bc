package barcode.dto;

import barcode.dao.entities.*;
import barcode.dao.predicates.ComingItemPredicatesBuilder;
import barcode.dao.services.AbstractEntityManager;
import barcode.utils.ComingItemFilter;
import barcode.utils.CommonUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;

import java.math.BigDecimal;
import java.util.List;

public class ResponseByComingItems
    extends ResponseItemExt<ComingItem>
    implements ResponseWithTotals<ComingItemFilter>{

    public ResponseByComingItems(
            String text, List<ComingItem> items, Boolean success, Integer numberOfPages) {

        super(text, items, success, numberOfPages);
    }

    @Override
    public void calcTotals (AbstractEntityManager abstractEntityManager,
                            BooleanBuilder predicate, ComingItemFilter filter) {

        final String
            SUMM_BY_SELECTION = SUMM + BY_SELECTION,
            QUANTITY_BY_SELECTION_PRICE_IN = QUANTITY + BY_SELECTION + PRICE_IN,
            QUANTITY_BY_FACT_PRICE_IN = QUANTITY + BY_FACT + PRICE_IN,
            QUANTITY_BY_SELECTION_PRICE_OUT = QUANTITY + BY_SELECTION + PRICE_OUT,
            QUANTITY_BY_FACT_PRICE_OUT = QUANTITY + BY_FACT + PRICE_OUT,
            SUMM_BY_FACT = SUMM + BY_FACT;

        ComingItemPredicatesBuilder cipb = new ComingItemPredicatesBuilder();
        Expression<BigDecimal> casePriceOut = cipb.getPriceOutCase(qComingItem);

        JPAQuery<BigDecimal> query = new JPAQuery<BigDecimal>(abstractEntityManager.getEntityManager());
        query = query.from(qComingItem).where(predicate);

        BigDecimal
            quantity = CommonUtils.validateBigDecimal(query.select(qComingItem.quantity.sum()).fetchOne()),
            currentQuantity = CommonUtils.validateBigDecimal(query.select(qComingItem.currentQuantity.sum()).fetchOne()),
            sum = CommonUtils.validateBigDecimal(
                query.select(qComingItem.quantity.multiply(qComingItem.priceIn).sum()).fetchOne()),
            currentSum =  CommonUtils.validateBigDecimal(query.select(qComingItem.sum.sum()).fetchOne()),
            sumOut = CommonUtils.validateBigDecimal(
                query.select(qComingItem.quantity.multiply(casePriceOut).sum()).fetchOne()),
            currentSumOut = CommonUtils.validateBigDecimal(
                query.select(qComingItem.currentQuantity.multiply(casePriceOut).sum()).fetchOne());

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_FACT_PRICE_OUT, currentQuantity, SUMM_BY_FACT , currentSumOut));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_SELECTION_PRICE_IN, quantity, SUMM_BY_SELECTION , sum));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_FACT_PRICE_IN, currentQuantity, SUMM_BY_FACT , currentSum));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD_BY_PRICE_IN, quantity.subtract(currentQuantity),
                SUMM , sum.subtract(currentSum)));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY_BY_SELECTION_PRICE_OUT, quantity, SUMM_BY_SELECTION , sumOut));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD_BY_PRICE_OUT, quantity.subtract(currentQuantity),
                SUMM , sumOut.subtract(currentSumOut)));

        super.setSections(
            new JPAQuery<ItemSection>(abstractEntityManager.getEntityManager())
                .from(qComingItem).where(predicate)
                .select(qComingItem.item.section)
                .distinct().orderBy(qComingItem.item.section.name.asc()).fetch());

        super.setSuppliers(
            new JPAQuery<Supplier>(abstractEntityManager.getEntityManager())
                .from(qComingItem).where(predicate)
                .select(qComingItem.doc.supplier)
                .distinct().orderBy(qComingItem.doc.supplier.name.asc()).fetch());

    }

}