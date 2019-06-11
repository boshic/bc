//package barcode.dao.repositories;
//
//import barcode.dao.entities.Invoice;
//import barcode.dao.repositories.InvoiceRepository;
//import barcode.dao.repositories.InvoiceRepositoryCustom;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.transaction.Transactional;
//
////
////import barcode.dao.entities.Invoice;
////import org.springframework.transaction.annotation.Transactional;
////
////import javax.persistence.EntityManager;
////import javax.persistence.PersistenceContext;
////
/////**
//// * Created by xlinux on 10.06.19.
//// */
//public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {
//
//
//    @PersistenceContext
//    private EntityManager em;
//
//    @Override
//    @Transactional
//    public void refresh(Invoice invoice) {
//        em.refresh(invoice);
//    }
//}
