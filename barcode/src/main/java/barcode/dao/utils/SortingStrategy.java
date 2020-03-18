package barcode.dao.utils;

import java.util.List;

/**
 * Created by xlinux on 18.03.20.
 */
public interface SortingStrategy<T> {

    void sort(List<T> items);
}
