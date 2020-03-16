package barcode.dao.services;


import barcode.dao.entities.Supplier;
import barcode.dao.repositories.SupplierRepository;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

@Service
public class SupplierHandler {

    private SupplierRepository supplierRepository;

    public SupplierHandler(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    private ResponseItem<Supplier> update(Supplier newSupplier, Supplier supplier) {
        newSupplier.setName(supplier.getName());
        supplierRepository.save(newSupplier);
        return new ResponseItem<Supplier>("Поставщик создан/изменен ", true, newSupplier);

    }

    public ResponseItem<Supplier> addSupplier(Supplier supplier) {
        if(getSupplierByName(supplier.getName()) == null) {
            Supplier newSupplier = new Supplier(supplier.getName());
            return update(newSupplier, supplier);
        }
            return new ResponseItem<Supplier>("Не создадим такого поставщика, уже есть в справочнике!", false);
    }

    public ResponseItem<Supplier> updateSupplier(Supplier supplier) {
        Supplier newSupplier =  supplierRepository.findOne(supplier.getId());
        return  update(newSupplier, supplier);
    }

    public Supplier getSupplierByName(String name) {
        return supplierRepository.findByNameIgnoreCase(name);
    }

    public Iterable<Supplier> getSuppliers(String filter) {
        return supplierRepository.findByNameContainingIgnoreCaseOrderByNameAsc(filter);
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findOne(id);
    }

}
