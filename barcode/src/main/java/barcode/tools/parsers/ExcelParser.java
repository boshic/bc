package barcode.tools.parsers;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

//    public static List<String> parse(String name) {

//        List<String> result;
//
//        try {
//            InputStream in = new FileInputStream(name);
//            HSSFWorkbook wb = new HSSFWorkbook(in);
//            try {
//                Sheet sheet = wb.getSheetAt(0);
//                Iterator<Row> it = sheet.iterator();
//                result = new ArrayList<String>();
//                while (it.hasNext()) {
//                    Row row = it.next();
//                    Iterator<Cell> cells = row.iterator();
//                    while (cells.hasNext()) {
//                        Cell cell = cells.next();
//                        int cellType = cell.getCellType();
//                        switch (cellType) {
//                            case Cell.CELL_TYPE_STRING:
//                                result.add(cell.getStringCellValue());
//                                break;
//                            case Cell.CELL_TYPE_NUMERIC:
//                                result.add(""+ cell.getNumericCellValue());
////                                res_row += "[" + cell.getNumericCellValue() + "]";
//                                break;
////                    case Cell.CELL_TYPE_FORMULA:
////                        result += "[" + cell.getNumericCellValue() + "]";
////                        break;
//                            default:
////                                result.add("|");
//                                break;
//                        }
//                    }
////            result += "</p><p>";
//                }
//                return result;
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return new ArrayList<String>() {{
//            add("there are");
//            add("some");
//            add("bugs");
//        }};
//
//    }
}
