package barcode.dao.services;

import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;
import barcode.dao.entities.embeddable.QInvoiceRow;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by xlinux on 11.06.19.
 */
@Transactional
@Repository
public class AbstractEntityManagerImpl implements AbstractEntityManager {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void test() {

        QInvoiceRow qInvoiceRow = QInvoiceRow.invoiceRow;
        QInvoice invoice = InvoiceHandler.qInvoice;

        List<Invoice> rows = new JPAQuery<Invoice>(entityManager)
                .select(invoice)
                .from(invoice)
                .where(invoice.buyer.name.containsIgnoreCase("огаз"))
                .fetch();

        System.out.println(rows.toString());

    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
