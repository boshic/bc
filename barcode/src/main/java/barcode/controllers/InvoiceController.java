package barcode.controllers;

import barcode.dao.entities.Invoice;
import barcode.dao.services.InvoiceHandler;
import barcode.dao.utils.BasicFilter;
import barcode.dao.utils.SoldItemFilter;
import barcode.dto.DtoInvoice;
import barcode.dto.InvoiceHeader;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping(path="/")
public class InvoiceController {

    private InvoiceHandler invoiceHandler;

    public InvoiceController(InvoiceHandler invoiceHandler) {
        this.invoiceHandler = invoiceHandler;
    }

    @RequestMapping(value = "/addInvoice", method = RequestMethod.POST)
    public @ResponseBody Long addInvoice(@RequestBody Invoice invoice) {

        if (invoice.getId() == null)
            return this.invoiceHandler.addInvoice(invoice).getEntityItem().getId();

         else
            return this.invoiceHandler.updateInvoice(invoice).getEntityItem().getId();
    }

    @RequestMapping(value = "/addWriteOffAct", method = RequestMethod.POST)
    public @ResponseBody Long addWriteOffAct(@RequestBody SoldItemFilter filter) {

        return this.invoiceHandler.addWriteOffAct(filter);
    }

//    addInvoiceByFilter
    @RequestMapping(value = "/addInvoiceByFilter", method = RequestMethod.POST)
    public @ResponseBody Long addInvoiceByFilter(@RequestBody SoldItemFilter filter) {

        return this.invoiceHandler.addInvoiceByFilter(filter);
    }

    @RequestMapping(value = "/getInvoicesByFilter", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem<InvoiceHeader> getInvoicesByFilter(@RequestBody SoldItemFilter filter) {

        return this.invoiceHandler.getInvoiceHeaders(filter);
    }

    @RequestMapping(value = "/getInvoicesFor1c", method = RequestMethod.POST)
    public @ResponseBody
    List<DtoInvoice> getInvoicesFor1c(@RequestBody BasicFilter filter) {

        return this.invoiceHandler.getInvoicesFor1c(filter);
    }

//    getWriteOffActById
    @GetMapping(path="/getWriteOffActById")
    public @ResponseBody
    Invoice getWriteOffActById(@RequestParam Long id) {
        return this.invoiceHandler.getWriteOffActById(id);
    }

    @GetMapping(path="/getInvoiceById")
    public @ResponseBody
    Invoice getInvoiceById(@RequestParam Long id) {
        return this.invoiceHandler.getItemById(id);
    }

    @GetMapping(value = "/deleteInvoice")
    public @ResponseBody void deleteInvoice (@RequestParam Long id) {
        invoiceHandler.deleteItem(id);
    }

}
