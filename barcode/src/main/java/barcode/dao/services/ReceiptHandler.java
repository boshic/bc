package barcode.dao.services;

import barcode.dao.entities.Buyer;
import barcode.dao.entities.Receipt;
import barcode.dao.entities.User;
import barcode.dao.repositories.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

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
        if(receipt != null)
            receiptRepository.save(receipt);
    }

    Receipt getReceiptByBuyer(Buyer buyer,
                                   BigDecimal sum,
                                   Integer numberOfPositions,
                                   User user) {

        Receipt receipt = null;
        if (!buyer.getSellByComingPrices()) {
            receipt = new Receipt(new Date(), sum, numberOfPositions, user, buyer);
//            receiptRepository.save(receipt);
        }

        return receipt;

    }


}
