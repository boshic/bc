package barcode.dto;

import barcode.dao.entities.Invoice;
import barcode.dao.services.AbstractEntityManager;
import barcode.utils.ComingItemFilter;
import barcode.utils.SoldItemFilter;
import com.querydsl.core.BooleanBuilder;

import java.util.List;

/**
 * Created by xlinux on 20.11.18.
 */
public class ResponseByInvoices
    extends ResponseItemExt<Invoice>
    implements ResponseWithTotals<SoldItemFilter> {

    public ResponseByInvoices() {}
    public ResponseByInvoices(String text, List<Invoice> items, Boolean success, Integer number) {
        super(text, items, success, number);
    }

    @Override
    public void calcTotals(AbstractEntityManager abstractEntityManager, SoldItemFilter filter) {}

}
