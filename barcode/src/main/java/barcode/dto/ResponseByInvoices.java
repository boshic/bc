package barcode.dto;

import barcode.dao.services.AbstractEntityManager;
import com.querydsl.core.BooleanBuilder;

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
    public void calcTotals(AbstractEntityManager abstractEntityManager, BooleanBuilder predicate) {}

}
