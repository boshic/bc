package barcode.dao.services;

import barcode.dao.entities.Bank;
import barcode.dao.repositories.BankRepository;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankHandler extends EntityHandlerImpl{

    private BankRepository bankRepository;

    public BankHandler(BankRepository bankRepository) {

        this.bankRepository = bankRepository;
    }

    private ResponseItem<Bank> update(Bank newBank, Bank bank) {

        ResponseItem<Bank> responseItem = new ResponseItem<Bank>();
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

        if (bankRepository.findByNameIgnoreCase(newBank.getName()) == null)
//            && (bankRepository.findByCodeIgnoreCase(newBank.getCode()) == null))
            return update(newBank, new Bank());

        else
            return setToNameIncorrectEntityFields(newBank, stringsToList(ENTITY_NAME, ENTITY_BANK_CODE));
    }

    public ResponseItem<Bank> updateItem(Bank newBank) {

        if((bankRepository.findByNameIgnoreCase(newBank.getName()) != null) &&
                !bankRepository.findByNameIgnoreCase(newBank.getName()).getId().equals(newBank.getId()))
                        return setToNameIncorrectEntityFields(newBank, stringsToList(ENTITY_NAME));

//        if((bankRepository.findByCodeIgnoreCase(newBank.getCode()) != null) &&
//                !bankRepository.findByCodeIgnoreCase(newBank.getCode()).getId().equals(newBank.getId()))
//                    return  setToNameIncorrectEntityFields(newBank, stringsToList(ENTITY_BANK_CODE));

        return update(newBank, this.getItemById(newBank.getId()));
    }

    Bank getCheckedItem (Bank bank) {

        return (bank == null
                || bank.getId() == null
                || bankRepository.findOne(bank.getId()) == null) ?
                null : bank;

    }

}
