package barcode.controllers;

import barcode.dao.entities.Bank;
import barcode.dao.services.BankHandler;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/")
public class BankController {

    private BankHandler bankHandler;

    public BankController(BankHandler bankHandler) {

        this.bankHandler = bankHandler;
    }

    @GetMapping(path="/getBanks")
    public @ResponseBody
    Iterable<Bank> getItems(@RequestParam String filter) {
        return this.bankHandler.getItems(filter);
    }

    @GetMapping(path="/getBankById")
    public @ResponseBody Bank getItemById(@RequestParam Long id) {

        return this.bankHandler.getItemById(id);
    }

    @RequestMapping(value = "/addBank", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addBank (@RequestBody Bank bank) {
        if (bank.getId() == null) {
            return  this.bankHandler.addItem(bank);
        } else {
            return this.bankHandler.updateItem(bank);
        }
    }


}
