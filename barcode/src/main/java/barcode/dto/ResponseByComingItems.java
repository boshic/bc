package barcode.dto;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.ItemSection;
import barcode.dao.entities.Supplier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseByComingItems extends ResponseItemExt<ComingItem> {

    public ResponseByComingItems(
            String text, List<ComingItem> items, Boolean success, Integer numberOfPages) {

        super(text, items, success, numberOfPages);

    }

    @Override
    public void calcTotals(List<ComingItem> comings) {

        BigDecimal quantity = BigDecimal.ZERO,
                    currentQuantity = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO, currentSum = BigDecimal.ZERO,
                    sumOut = BigDecimal.ZERO, currentSumOut = BigDecimal.ZERO;

        for(ComingItem comingItem: comings)  {

//            suppliers.add(comingItem.getDoc().getSupplier());
//            sections.add(comingItem.getItem().getSection());

            currentQuantity = currentQuantity.add(comingItem.getCurrentQuantity());

            currentSum = currentSum.add(comingItem.getSum());

            currentSumOut = currentSumOut
                    .add(comingItem.getPriceOut().multiply(comingItem.getCurrentQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            quantity = quantity.add(comingItem.getQuantity());

            sum = sum.add(comingItem.getPriceIn().multiply(comingItem.getQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            sumOut = sumOut
                    .add(comingItem.getPriceOut().multiply( comingItem.getQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("кол-во по факту (отп.цена)", currentQuantity, "сумма по факту" , currentSumOut));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("кол-во по выборке (уч.цена)", quantity, "сумма по выборке" , sum));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("кол-во по факту (уч.цена)", currentQuantity, "сумма по факту" , currentSum));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("выбыло по учетной", quantity.subtract(currentQuantity),
                        "сумма" , sum.subtract(currentSum)));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("кол-во по выборке (отп.цена)", quantity, "сумма по выборке" , sumOut));

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("выбыло по отп.цене", quantity.subtract(currentQuantity),
                        "сумма" , sumOut.subtract(currentSumOut)));

        super.setSuppliersByComings(comings);

        super.setSectionsByComings(comings);

    }

    public void calcInventoryTotals(List<ComingItem> comings) {

        BigDecimal quantity = comings.stream().map(ComingItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add),

                currentQuantity = comings.stream().map(ComingItem::getCurrentQuantity).reduce(BigDecimal.ZERO, BigDecimal::add),

                sum = comings.stream().map(ComingItem::getSum).reduce(BigDecimal.ZERO, BigDecimal::add),

                currentSum = comings.stream()
                        .map(
                                c -> c.getCurrentQuantity()
                                    .multiply(
                                            c.getSum().divide(c.getQuantity(), 5, RoundingMode.CEILING)
                                    ).setScale(2, BigDecimal.ROUND_HALF_UP)
                        )
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("Расхождение, кол.", currentQuantity.subtract(quantity),
                        "Сумма" , currentSum.subtract(sum)));

        super.setSectionsByComings(comings);

    }

}