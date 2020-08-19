package barcode.dto;


import barcode.dao.entities.*;
import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import barcode.dao.services.AbstractEntityManager;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResponseBySoldItems <T> extends ResponseItemExt<T> {

    final private String INCOME = "доход", RECEIPTS = "чеков", AVERAGE = "средний";

    public ResponseBySoldItems(String text, List<T> items, Boolean success, Integer number) {
        super(text, items, success, number);
    }

    @Override
    public void calcTotals (AbstractEntityManager abstractEntityManager, BooleanBuilder predicate) {

//        final String INCOME = "доход", RECEIPTS = "чеков", AVERAGE = "средний";

        QSoldItem soldItem = QSoldItem.soldItem;
        JPAQuery<BigDecimal> queryBdcml = new JPAQuery<BigDecimal>(abstractEntityManager.getEntityManager());
        queryBdcml = queryBdcml.from(soldItem).where(predicate);

        BigDecimal
            receiptsNumber = new BigDecimal(queryBdcml.select(soldItem.receipt).fetchCount()),
            receiptsSum = queryBdcml.select(soldItem.receipt.sum.sum()).fetchOne(),
            quantity = queryBdcml.select(soldItem.quantity.sum()).fetchOne(),
            sum = queryBdcml.select(soldItem.sum.sum()).fetchOne(),
            sumByComing = queryBdcml.select(soldItem.coming.priceIn.multiply(soldItem.quantity).sum()).fetchOne(),
            sumExcludedFromIncome = queryBdcml
                .where(soldItem.buyer.excludeExpensesFromIncome.isTrue())
                .select(soldItem.coming.priceIn.sum()).fetchOne();

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD, quantity, SUMM , sum));
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD_BY_PRICE_IN, quantity, SUMM , sumByComing));

        assert sum != null;
        assert sumByComing != null;
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (SOLD, quantity, INCOME , sum.subtract(sumByComing
                .add(sumExcludedFromIncome == null ? BigDecimal.ZERO : sumExcludedFromIncome))));

        super.setBuyers(
            new JPAQuery<Buyer>(abstractEntityManager.getEntityManager())
            .from(soldItem).where(predicate)
            .select(soldItem.buyer)
            .distinct().orderBy(soldItem.buyer.name.asc()).fetch());

        super.setSuppliers(
            new JPAQuery<Supplier>(abstractEntityManager.getEntityManager())
                .from(soldItem).where(predicate)
                .select(soldItem.coming.doc.supplier)
                .distinct().orderBy(soldItem.coming.doc.supplier.name.asc()).fetch());

        super.setSections(
            new JPAQuery<Supplier>(abstractEntityManager.getEntityManager())
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

    public void calcTotalsDepr(List<SoldItem> soldItemList) {

        BigDecimal quantity = BigDecimal.ZERO, sum = BigDecimal.ZERO, sumByComing = BigDecimal.ZERO,
            sumExcludedFromIncome = BigDecimal.ZERO;
        Set<Receipt> receipts = new HashSet<>();
        Set<Buyer> buyers = new HashSet<>();
        Set<Supplier> suppliers = new HashSet<>();
        Set<ItemSection> sections = new HashSet<>();

        for(SoldItem soldItem: soldItemList)  {

            if(soldItem.getReceipt() != null && soldItem.getReceipt().getSum().compareTo(BigDecimal.ZERO) > 0)
                receipts.add(soldItem.getReceipt());

            buyers.add(soldItem.getBuyer());

            sections.add(soldItem.getComing().getItem().getSection());

            suppliers.add(soldItem.getComing().getDoc().getSupplier());

            quantity = quantity.add(soldItem.getQuantity());

            sum = sum.add(soldItem.getSum());

            sumByComing = sumByComing.add(soldItem.getComing().getPriceIn().multiply(soldItem.getQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            if(soldItem.getBuyer().getExcludeExpensesFromIncome())
                sumExcludedFromIncome = sumExcludedFromIncome.add(soldItem.getSum());
        }

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                (SOLD, quantity, SUMM , sum));
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                (SOLD_BY_PRICE_IN, quantity, SUMM , sumByComing));
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                (SOLD, quantity,
                        INCOME , sum.subtract(sumByComing.add(sumExcludedFromIncome))));
        if(receipts.size() > 0)
            super.getTotals().add(new ResultRowByItemsCollection<Integer, BigDecimal>
                (RECEIPTS, receipts.size(), AVERAGE ,
                        receipts.stream().map(Receipt::getSum).reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(new BigDecimal(receipts.size()), 2)));
        else
            super.getTotals().add(new ResultRowByItemsCollection<Integer, BigDecimal>
                    (RECEIPTS, 0, AVERAGE , BigDecimal.ZERO));


        super.setBuyers(buyers.stream().sorted(Comparator.comparing(Buyer::getName)).collect(Collectors.toList()));
        super.setSuppliers(suppliers
                .stream()
                .sorted(Comparator.comparing(Supplier::getName))
                .collect(Collectors.toList()));

        super.setSections(sections
                .stream()
                .sorted(Comparator.comparing(ItemSection::getName))
                .collect(Collectors.toList()));

//        super.setGoodsBySellings(soldItemList);
    }
}
