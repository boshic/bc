package barcode.controllers;

import barcode.dao.entities.Supplier;
import barcode.dao.services.SupplierHandler;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/")
public class SupplierController {

    private SupplierHandler supplierHandler;

    public SupplierController(SupplierHandler supplierHandler) {
        this.supplierHandler = supplierHandler;
    }

    @RequestMapping(value = "/addSupplier", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addSupplier (@RequestBody Supplier item) {
        if (item.getId() == null) {
            return this.supplierHandler.addSupplier(item);
        } else {
            return this.supplierHandler.updateSupplier(item);
        }
    }

    @GetMapping(path="/getSuppliers")
    public @ResponseBody Iterable<Supplier> getSuppliers(@RequestParam String filter) {
        return this.supplierHandler.getSuppliers(filter);
    }

    @GetMapping(path="/getSupplierById")
    public @ResponseBody Supplier getSupplierById(@RequestParam Long id) {
        return this.supplierHandler.getSupplierById(id);
    }

}
