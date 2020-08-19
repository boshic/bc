package barcode.dao.services;

import barcode.dao.entities.basic.BasicOperationEntity;
import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import barcode.dao.entities.embeddable.Comment;
import barcode.dao.entities.embeddable.InvoiceRow;
import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;
import barcode.dao.entities.SoldItem;
import barcode.dao.predicates.InvoicesPredicatesBuilder;
import barcode.dao.repositories.InvoiceRepository;
import barcode.dao.utils.BasicFilter;
import barcode.dao.utils.SoldItemFilter;
import barcode.dto.*;
import barcode.enums.CommentAction;
import barcode.enums.SystemMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InvoiceHandler extends EntityHandlerImpl {

    public static QInvoice qInvoice = QInvoice.invoice;


    private static InvoicesPredicatesBuilder ipb = new InvoicesPredicatesBuilder();

    private InvoiceRepository invoiceRepository;

    private SoldItemHandler soldItemHandler;

    private UserHandler userHandler;

    private AbstractEntityManager abstractEntityManager;


    public InvoiceHandler(InvoiceRepository invoiceRepository, UserHandler userHandler, SoldItemHandler soldItemHandler
            , AbstractEntityManager abstractEntityManager) {

        this.soldItemHandler = soldItemHandler;

        this.userHandler = userHandler;

        this.invoiceRepository = invoiceRepository;

        this.abstractEntityManager = abstractEntityManager;

    }

    private ResponseItem<Invoice> update(Invoice newInvoice, Invoice invoice) {

        newInvoice.setUser(userHandler.getCurrentUser());

        newInvoice.setBuyer(invoice.getBuyer());

        newInvoice.setStock(invoice.getStock());

        newInvoice.setInvoiceRows(invoice.getInvoiceRows());

        newInvoice.setSum(invoice.getSumOfRows());

        if(newInvoice.getDeleted() == null)
            newInvoice.setDeleted(false);

        if(invoice.getId() == null) {

            newInvoice.setComments(invoice.getComments() != null ? invoice.getComments() : new ArrayList<>());
            newInvoice.setComment(
                    this.buildComment(newInvoice.getComments(), invoice.getComment(),
                            userHandler.checkUser(invoice.getUser(), null).getFullName(),
                        CommentAction.NEW_REPORT_ADDED.getAction(), BigDecimal.ZERO)
            );
        }
        else
            newInvoice.setComment(
                    this.buildComment(newInvoice.getComments(), invoice.getComment(),
                            userHandler.checkUser(invoice.getUser(), null ).getFullName(),
                        CommentAction.SMTH_CHANGED.getAction(), BigDecimal.ZERO)
            );


        invoiceRepository.save(newInvoice);

        return new ResponseItem<Invoice>(SystemMessage.NEW_REPORT_ADDED.getMessage(), true, newInvoice);
    }

    public ResponseItem<Invoice> addInvoice(Invoice invoice) {

//        Invoice newInvoice = new Invoice(new Date());

        return update(new Invoice(new Date()), invoice);
    }

    public Invoice getWriteOffActById(Long id) {

        Invoice invoice = invoiceRepository.findOne(id);

        invoice.setComment(this.getCommentByAction(invoice.getComments(),
            CommentAction.WRITE_OFF_ACT_ADDED.getAction()));

        return invoice;
    }

    public Invoice getItemById(Long id) {

        return invoiceRepository.findOne(id);
    }

//    public Invoice get

    public ResponseItem<Invoice> updateInvoice(Invoice invoice) {

        Invoice newInvoice = invoiceRepository.findOne(invoice.getId());

        return update(newInvoice, invoice);
    }


    public List<DtoInvoice> getInvoicesFor1c(BasicFilter filter) {

        List<Invoice> invoices = invoiceRepository
                .findByDateBetweenAndIsDeletedOrderByDateDesc(
                        filter.getFromDate(), filter.getToDate(), false);

        if(invoices.size() > 0) {

            List<DtoInvoice> dtoInvoices = new ArrayList<DtoInvoice>();

            for (Invoice invoice: invoices) {

                DtoInvoice dtoInvoice = new DtoInvoice(invoice);

                for(InvoiceRow invoiceRow: invoice.getInvoiceRows())
                    dtoInvoice.getRows().add(new DtoInvoiceRow(invoiceRow));

                dtoInvoices.add(dtoInvoice);

            }

            return dtoInvoices;
        }

        return null;
    }

    public synchronized void deleteItem(long id) {

        Invoice invoice = invoiceRepository.findOne(id);
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
    }

    public ResponseByInvoices<Invoice> getInvoicesByFilter(SoldItemFilter filter) {

//        Sort sort = new Sort(Sort.Direction.DESC, "date");

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<Invoice> page =  invoiceRepository.findAll(ipb.buildByFilter(filter, abstractEntityManager), pageRequest);

        List<Invoice> result = page.getContent();

        if (result.size() > 0) {

            ResponseByInvoices<Invoice> ribyi =
                    new ResponseByInvoices<Invoice>(
                            ELEMENTS_FOUND, result, true, page.getTotalPages());

//            if(filter.getCalcTotal())
//                ribyi.calcTotals(invoiceRepository.findAll(ipb.buildByFilter(filter, abstractEntityManager)));

            return ribyi;
        }

        return new ResponseByInvoices<Invoice>(NOTHING_FOUND, new ArrayList<Invoice>(),
                false, 0);

    }

    public ResponseItem<InvoiceHeader> getInvoiceHeaders(SoldItemFilter filter) {

//        abstractEntityManager.test();

        ResponseByInvoices<Invoice> responseByInvoices = getInvoicesByFilter(filter);

        if(responseByInvoices.getSuccess()) {

            ResponseByInvoices<InvoiceHeader> headers
                    = new ResponseByInvoices<InvoiceHeader>(
                            "", new ArrayList<InvoiceHeader>(),true,
                                                responseByInvoices.getNumberOfPages());

            for(Invoice invoice: responseByInvoices.getEntityItems())
                headers.getEntityItems().add(new InvoiceHeader(invoice));

            return headers;
        }

        return new ResponseItem<>(NOTHING_FOUND, new ArrayList<>(), false);
    }

    public Long addInvoiceByFilter(SoldItemFilter filter) {

        List<SoldItem> sellings = soldItemHandler.getSoldItemsByFilter(filter);

        if(sellings.size() > 0) {

            Invoice invoice = new Invoice(new Date(),
                    filter.getStock(), filter.getBuyer(), new ArrayList<InvoiceRow>());

            for(SoldItem soldItem: sellings)
                if(soldItem.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                    invoice.getInvoiceRows().add(new InvoiceRow(soldItem,"", soldItem.getPrice()));

            return addInvoice(invoice).getEntityItem().getId();
        }

        return null;
    }


    public Long addWriteOffAct(SoldItemFilter filter) {

        List<SoldItem> sellings = soldItemHandler.getSoldItemsByFilter(filter);

        if(sellings.size() > 0) {

            Invoice invoice = new Invoice(new Date(),
                    filter.getStock(), filter.getBuyer(), new ArrayList<InvoiceRow>());

            String comment = "с " + DATE_FORMAT_WO_TIME.format(filter.getFromDate()) + " по " +
                    DATE_FORMAT_WO_TIME.format(filter.getToDate());

            this.buildComment(invoice.getComments(), comment,
                    userHandler.checkUser(invoice.getUser(), null ).getFullName(),
                CommentAction.WRITE_OFF_ACT_ADDED.getAction(), BigDecimal.ZERO);

            for(SoldItem soldItem: sellings)
                if(soldItem.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                    invoice.getInvoiceRows().add(new InvoiceRow(
                        soldItem,
                        DATE_FORMAT_WITH_TIME.format(soldItem.getDate()) + SPACE +
                        this.getCommentByAction(soldItem.getComments(), WRITE_OFF_CAUSE),
                        soldItem.getComing().getPriceIn()));


            return addInvoice(invoice).getEntityItem().getId();
        }

        return null;
    }

    public ResponseItem<InvoiceHeader> changeDate(InvoiceHeader invoiceHeader) {

        this.changeDate(invoiceHeader.getId(), invoiceHeader.getDocDate(),
                invoiceRepository, userHandler.getCurrentUser().getFullName());

        return null;
    }

}
