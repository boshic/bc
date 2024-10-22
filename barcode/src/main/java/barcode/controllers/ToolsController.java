package barcode.controllers;

import barcode.dao.entities.ComingItem;
import barcode.dao.services.ItemHandler;
import barcode.dto.ResponseItem;
import barcode.tools.xml.XmlCreator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static com.google.common.io.ByteStreams.toByteArray;

@Controller
@RequestMapping(path="/tools")
public class ToolsController {

    private ItemHandler itemHandler;
//    private PdfReportGenerator pdfReportGenerator;

    public ToolsController( ItemHandler itemHandler) {

        this.itemHandler = itemHandler;
    }

    @GetMapping(path="/getHashedPass")
    public @ResponseBody
    String getHashedPass(@RequestParam String pass) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return  passwordEncoder.encode(pass);
    }

//    @RequestMapping(value = "/pdfreport", method = RequestMethod.GET,
//                                                produces = MediaType.APPLICATION_PDF_VALUE)
//    public ResponseEntity<InputStreamResource> citiesReport() throws IOException {
//
////        List<City> cities = (List<City>) cityService.findAll();
////        List<Buyer> buyers = (List<Buyer>) buyerRepository.findAll();
//        ByteArrayInputStream bis = pdfReportGenerator.create(null,"");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "inline; filename=3534.pdf");
//
//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(new InputStreamResource(bis));
//    }

    @RequestMapping(value = "/testQuery", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem handleQuery (@RequestBody Set<ComingItem> comings) {
        return new ResponseItem(comings.toString());
    }


//    @GetMapping(path="/uplxls")
//    public @ResponseBody List<String> uploadXls(@RequestParam String fileName) {
////        List<String> parsedXls = ExcelParser.parse(fileName);
////        for (String rowData : parsedXls) {
////            if (this.itemHandler.getItems(rowData).spliterator().getExactSizeIfKnown() == 0) {
////                this.itemHandler.addItemFromParsedData(rowData);
////            }
////        }
//        return ExcelParser.parse(fileName);
//    }

//    @RequestMapping(value = "/getXml", method = RequestMethod.GET)
//    @ResponseBody public FileSystemResource getXmlFile(HttpServletResponse response) {
//        response.setContentType("application/xml");
//        response.setHeader("Content-disposition",
//            "attachment; filename=" +
//                "/home/pab/_temp_/example.xml" );
//        return new FileSystemResource(new File("/home/pab/_temp_/example.xml")); //Or path to your file
//    }
//    @GetMapping("/home")
//    public String redirectHome() throws Exception{
//        XmlCreator.create();
//        return "home";
//    }


//    @GetMapping(path="/uplhtml")
//    public @ResponseBody List<String> uplhtml(@RequestParam String fileName) {
////        List<String> parsedXls = ExcelParser.parse(fileName);
////        for (String rowData : parsedXls) {
////            if (this.itemHandler.getItems(rowData).spliterator().getExactSizeIfKnown() == 0) {
////                this.itemHandler.addItemFromParsedData(rowData);
////            }
////        }
//        return HTMLParser.parse(fileName);
//    }


}
