package barcode.dto;


import barcode.dao.entities.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResponseBySoldItems extends ResponseItemExt<SoldItem> {

//    public ResponseBySoldItems() {}
    public ResponseBySoldItems(String text, List<SoldItem> items, Boolean success, Integer number) {
        super(text, items, success, number);
    }

    @Override
    public void calcTotals(List<SoldItem> soldItemList) {

        BigDecimal quantity = BigDecimal.ZERO, sum = BigDecimal.ZERO, sumByComing = BigDecimal.ZERO;
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
        }

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("отпущено", quantity, "на сумму" , sum));
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("отпущено по учетной", quantity, "на сумму" , sumByComing));
        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
                ("выбыло", quantity, "доход" , sum.subtract(sumByComing)));
        if(receipts.size() > 0)
            super.getTotals().add(new ResultRowByItemsCollection<Integer, BigDecimal>
                ("чеков", receipts.size(), "средний" ,
                        receipts.stream().map(Receipt::getSum).reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(new BigDecimal(receipts.size()), 2)));

        super.setBuyers(buyers.stream().sorted(Comparator.comparing(Buyer::getName)).collect(Collectors.toList()));
        super.setSuppliers(suppliers
                .stream()
                .sorted(Comparator.comparing(Supplier::getName))
                .collect(Collectors.toList()));

        super.setSections(sections
                .stream()
                .sorted(Comparator.comparing(ItemSection::getName))
                .collect(Collectors.toList()));

        super.setGoodsBySellings(soldItemList);

    }
}
