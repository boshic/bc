package barcode.dao.services;

import barcode.dao.entities.Receipt;
import barcode.dao.repositories.ReceiptRepository;
import org.springframework.stereotype.Service;

/**
 * Created by xlinux on 27.11.19.
 */
@Service
public class ReceiptHandler {

    private ReceiptRepository receiptRepository;

    public ReceiptHandler(ReceiptRepository receiptRepository) {

        this.receiptRepository = receiptRepository;
    }

    void save (Receipt receipt) {

        receiptRepository.save(receipt);
    }


}
