package barcode.utils;

public class CommonUtils {

    public static String toEnumStyle(String value) {
        return value.replace(".","_").toUpperCase();
    }

    public static String enumToString(String value) {
        return value.replace("_",".").toLowerCase();
    }

}
