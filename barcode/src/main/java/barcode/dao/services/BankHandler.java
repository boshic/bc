package barcode.dao.services;

import barcode.dao.entities.Bank;
import barcode.dao.entities.Buyer;
import barcode.dao.repositories.BankRepository;
import barcode.dto.ResponseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankHandler {

    private BankRepository bankRepository;

    public BankHandler(BankRepository bankRepository) {

        this.bankRepository = bankRepository;
    }

    private ResponseItem<Bank> update(Bank newBank, Bank bank) {

        ResponseItem<Bank> responseItem = new ResponseItem<Bank>("Добавление нового банка");

        bank.setName(newBank.getName());

        bank.setAddress(newBank.getAddress());

        bank.setCode(newBank.getCode());

        bankRepository.save(bank);

        return new ResponseItem<Bank>("Добавлен банк '" + bank.getName() + "'", true, bank);
    }

    public Iterable<Bank> getItems(String filter) {

        List<Bank> banks = bankRepository.findByCodeContainingIgnoreCaseOrderByCodeDesc(filter);

        if (banks.size() == 0) {
            return bankRepository.findByNameContainingIgnoreCaseOrderByNameDesc(filter);
        }

        return banks;
    }

    public Bank getItemById(Long id) {

        return bankRepository.findOne(id);
    }

    public ResponseItem<Bank> addItem(Bank newBank) {

        if ((bankRepository.findByNameIgnoreCase(newBank.getName()) == null) &&
                (bankRepository.findByCodeIgnoreCase(newBank.getCode()) == null))
            return update(newBank, new Bank());

        else
            return new ResponseItem<Bank>("Для банка '" + newBank.getName() +
                    "' имеется совпадение, измените код или наименование!");
    }

    public ResponseItem<Bank> updateItem(Bank newBank) {

        if((bankRepository.findByNameIgnoreCase(newBank.getName()) != null) &&
                !bankRepository.findByNameIgnoreCase(newBank.getName()).getId().equals(newBank.getId()))
                        return new ResponseItem<Bank>("Для банка '" + newBank.getName() +
                    "' имеется совпадение, измените наименование!");

        if((bankRepository.findByCodeIgnoreCase(newBank.getCode()) != null) &&
                !bankRepository.findByCodeIgnoreCase(newBank.getCode()).getId().equals(newBank.getId()))
            return new ResponseItem<Bank>("Для банка '" + newBank.getName() +
                    "' имеется совпадение, измените БИК!");

        return update(newBank, this.getItemById(newBank.getId()));
    }

    Bank getCheckedItem (Bank bank) {

        return (bank == null
                || bank.getId() == null
                || bankRepository.findOne(bank.getId()) == null) ?
                null : bank;

    }

}
