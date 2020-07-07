package barcode.dto;

import barcode.dao.entities.ComingItem;
import barcode.dao.services.ComingItemHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ResponseByComingItems extends ResponseItemExt<ComingItem> {


    public ResponseByComingItems(
            String text, List<ComingItem> items, Boolean success, Integer numberOfPages) {

        super(text, items, success, numberOfPages);

    }

    @Override
    public void calcTotals(List<ComingItem> comings) {

        final String
                SUMM_BY_SELECTION = SUMM + BY_SELECTION,
                QUANTITY_BY_SELECTION_PRICE_IN = QUANTITY + BY_SELECTION + PRICE_IN,
                QUANTITY_BY_FACT_PRICE_IN = QUANTITY + BY_FACT + PRICE_IN,
                QUANTITY_BY_SELECTION_PRICE_OUT = QUANTITY + BY_SELECTION + PRICE_OUT,
                QUANTITY_BY_FACT_PRICE_OUT = QUANTITY + BY_FACT + PRICE_OUT,
                SUMM_BY_FACT = SUMM + BY_FACT;


        BigDecimal quantity = BigDecimal.ZERO,
                    currentQuantity = BigDecimal.ZERO,
                    sum = BigDecimal.ZERO, currentSum = BigDecimal.ZERO,
                    sumOut = BigDecimal.ZERO, currentSumOut = BigDecimal.ZERO;

        for(ComingItem comingItem: comings)  {

            BigDecimal priceOut = comingItem.getItem().getPrice().compareTo(BigDecimal.ZERO) > 0 ?
                    comingItem.getItem().getPrice() : comingItem.getPriceOut();

            currentQuantity = currentQuantity.add(comingItem.getCurrentQuantity());

            currentSum = currentSum.add(comingItem.getSum());

            currentSumOut = currentSumOut
                    .add(priceOut.multiply(comingItem.getCurrentQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            quantity = quantity.add(comingItem.getQuantity());

            sum = sum.add(comingItem.getPriceIn().multiply(comingItem.getQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            sumOut = sumOut
                    .add(priceOut.multiply( comingItem.getQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }

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

        super.setSuppliersByComings(comings);

        super.setSectionsByComings(comings);

    }

    public void calcInventoryTotals(List<ComingItem> comings) {

        final String
                DEVIATION = "Расхождение, кол.",
                SUMM_OUT = SUMM + PRICE_OUT,
                SUMM_IN = SUMM + PRICE_IN;
        ;

        BigDecimal quantity = comings.stream().map(ComingItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add),

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