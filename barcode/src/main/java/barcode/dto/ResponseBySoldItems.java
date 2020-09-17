package barcode.dto;


import barcode.dao.entities.*;
import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import barcode.dao.predicates.SoldItemPredicatesBuilder;
import barcode.dao.services.AbstractEntityManager;
import barcode.utils.CommonUtils;
import barcode.utils.SoldItemFilter;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ResponseBySoldItems extends ResponseItemExt<SoldItem>
    implements ResponseWithTotals<SoldItemFilter>{

        public ResponseBySoldItems(String text, List<SoldItem> items, Boolean success, Integer number) {
        super(text, items, success, number);
    }

    @Override
    public void calcTotals(AbstractEntityManager abstractEntityManager,
                           SoldItemFilter filter) {

        final String INCOME = "доход", RECEIPTS = "чеков", AVERAGE = "средний";

        EntityManager em = abstractEntityManager.getEntityManager();
        QSoldItem soldItem = QSoldItem.soldItem;
        JPAQuery<BigDecimal> queryBdcml = new JPAQuery<BigDecimal>(em);
        SoldItemPredicatesBuilder sipb = new SoldItemPredicatesBuilder();
        BooleanBuilder predicate = sipb.buildByFilter(filter);
        queryBdcml = queryBdcml.from(soldItem).where(predicate);

        BigDecimal
            receiptsNumber = new BigDecimal(queryBdcml.select(soldItem.receipt).fetchCount()),
            receiptsSum = CommonUtils.validateBigDecimal(queryBdcml.select(soldItem.receipt.sum.sum()).fetchOne()),
            quantity = CommonUtils.validateBigDecimal(queryBdcml.select(soldItem.quantity.sum()).fetchOne()),
            sum = CommonUtils.validateBigDecimal(queryBdcml.select(soldItem.sum.sum()).fetchOne()),
            sumByComing = CommonUtils.validateBigDecimal(queryBdcml
                .select(soldItem.coming.priceIn.multiply(soldItem.quantity).sum()).fetchOne()),
            sumExcludedFromIncome = CommonUtils.validateBigDecimal(queryBdcml
                .where(soldItem.buyer.excludeExpensesFromIncome.isTrue())
                .select(soldItem.coming.priceIn.multiply(soldItem.quantity).sum()).fetchOne());

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD, quantity, SUMM , sum));
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD_BY_PRICE_IN, quantity, SUMM , sumByComing));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD, quantity, INCOME , sum.subtract(sumByComing.add(sumExcludedFromIncome))));

        super.setBuyers(
            new JPAQuery<Buyer>(em)
            .from(soldItem).where(predicate)
            .select(soldItem.buyer)
            .distinct().orderBy(soldItem.buyer.name.asc()).fetch());

        super.setSuppliers(
            new JPAQuery<Supplier>(em)
                .from(soldItem).where(predicate)
                .select(soldItem.coming.doc.supplier)
                .distinct().orderBy(soldItem.coming.doc.supplier.name.asc()).fetch());

        super.setSections(
            new JPAQuery<Supplier>(em)
                .from(soldItem).where(predicate)
                .select(soldItem.coming.item.section)
                .distinct().orderBy(soldItem.coming.item.section.name.asc()).fetch());

        if(receiptsSum != null && receiptsNumber.compareTo(BigDecimal.ZERO) > 0)
            super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                (RECEIPTS, receiptsNumber, AVERAGE , receiptsSum.divide(receiptsNumber, 2)));
        else
            super.getTotals().add(new ResultRowByItemsCollection<Integer, BigDecimal>
                (RECEIPTS, 0, AVERAGE , BigDecimal.ZERO));

    }

}
