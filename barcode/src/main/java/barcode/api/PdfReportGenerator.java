package barcode.api;

import java.io.ByteArrayInputStream;

/**
 * Created by xlinux on 21.06.18.
 */
public interface PdfReportGenerator {

    ByteArrayInputStream create(Object reportData, String reportType);

}
