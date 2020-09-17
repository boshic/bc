package barcode.dto;

import barcode.dao.services.AbstractEntityManager;
import barcode.utils.ComingItemFilter;
import com.querydsl.core.BooleanBuilder;

public interface ResponseWithTotals<T extends ComingItemFilter> {

     void calcTotals(
        AbstractEntityManager abstractEntityManager, T filter);
}
