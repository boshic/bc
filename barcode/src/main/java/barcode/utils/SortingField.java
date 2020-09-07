package barcode.utils;

import java.util.List;

/**
 * Created by xlinux on 18.03.20.
 */
public interface SortingField {

    void checkSortField(String field) throws IllegalArgumentException;
    String getValue();
}
