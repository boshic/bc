package barcode.dao.utils;

import barcode.dao.entities.Supplier;

public class DocumentFilter extends BasicFilter {

    private Supplier supplier;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public DocumentFilter() {}
    public DocumentFilter(BasicFilter filter, Supplier supplier) {
        super(filter);
        this.supplier = supplier;
    }
}
