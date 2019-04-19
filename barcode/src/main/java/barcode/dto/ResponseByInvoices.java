package barcode.dto;

import java.util.List;

/**
 * Created by xlinux on 20.11.18.
 */
public class ResponseByInvoices<T> extends ResponseItemExt<T> {

    public ResponseByInvoices() {}
    public ResponseByInvoices(String text, List<T> items, Boolean success, Integer number) {
        super(text, items, success, number);
    }

    @Override
    public void calcTotals(List<T> invoices) {

        Long quantity = 0L;
        Float sum = 0F, sumByComing = 0F;

        super.getTotals().add(new ResultRowByItemsCollection<Long, Float>
                ("отпущено", quantity, "на сумму" , sum));
        super.getTotals().add(new ResultRowByItemsCollection<Long, Float>
                ("отпущено по учетной", quantity, "на сумму" , sumByComing));
        super.getTotals().add(new ResultRowByItemsCollection<Long, Float>
                ("выбыло", quantity, "доход" , sum - sumByComing));
    }

}
