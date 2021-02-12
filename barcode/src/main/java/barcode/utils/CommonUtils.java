package barcode.utils;

import barcode.dto.DtoBuyer;
import com.querydsl.core.Tuple;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CommonUtils {

    public static String toEnumStyle(String value) {
        return value.replace(".","_").toUpperCase();
    }

    public static String enumToString(String value) {
        return value.replace("_",".").toLowerCase();
    }

    public static BigDecimal validateBigDecimal(BigDecimal bigDecimal) {

          return (bigDecimal == null) ? BigDecimal.ZERO : bigDecimal;

    }

    public static String validateString(String value) {
        return (value == null) ? "" : value;

    }

    public static Boolean validateBoolean(Boolean value) {
        return (value == null) ? false : value;

    }

    public static Date validateDate(Date date) {
        return (date == null) ? new Date() : date;

    }
}
