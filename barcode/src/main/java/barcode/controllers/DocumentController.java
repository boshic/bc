package barcode.controllers;


import barcode.dao.entities.Document;
import barcode.dao.services.DocumentHandler;
import barcode.dao.utils.BasicFilter;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/")
public class DocumentController {

    private DocumentHandler documentHandler;

    public DocumentController(DocumentHandler documentHandler) {
        this.documentHandler = documentHandler;
    }

    @RequestMapping(value = "/addDocument", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addDocument (@RequestBody Document item) {
        if (item.getId() == null) {
            return this.documentHandler.addItem(item);
        } else {
            return this.documentHandler.updateItem(item);
        }
    }

//    @GetMapping(path="/getDocs")
//    public @ResponseBody Iterable<Document> getDocs(@RequestParam String filter,
//                                                    @RequestParam  @DateTimeFormat(pattern="dd.MM.yyyy") Date dateFrom,
//                                                    @RequestParam  @DateTimeFormat(pattern="dd.MM.yyyy") Date dateTo) {
//        return this.documentHandler.getItems(filter, dateFrom, dateTo);
//    }

    @RequestMapping(value = "/getDocs", method = RequestMethod.POST)
    public @ResponseBody Iterable<Document> getDocs(@RequestBody BasicFilter filter) {
        return this.documentHandler.getItems(filter);
    }

}
