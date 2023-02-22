package barcode.dao.services;

import barcode.dao.entities.embeddable.InvoiceRow;
import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;
import barcode.dao.entities.SoldItem;
import barcode.dao.predicates.InvoicesPredicatesBuilder;
import barcode.dao.repositories.InvoiceRepository;
import barcode.utils.BasicFilter;
import barcode.utils.CommonUtils;
import barcode.utils.SoldItemFilter;
import barcode.dto.*;
import barcode.enums.CommentAction;
import barcode.enums.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private UserHandler userHandler;
    private ApplicationContext context;
    private AbstractEntityManager abstractEntityManager;


    public InvoiceHandler(InvoiceRepository invoiceRepository,
                          UserHandler userHandler,
                          ApplicationContext context,
                          AbstractEntityManager abstractEntityManager) {

        super(context);
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

        newInvoice.setDeleted(CommonUtils.validateBoolean(invoice.getDeleted()));
        newInvoice.setNotForUpload(CommonUtils.validateBoolean(invoice.getNotForUpload()));

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
        Invoice item = invoiceRepository.findOne(id);
        item.getUser().setStampForInvoices(userHandler.getCurrentUser().getStamp());
        return item;
    }

//    public Invoice get

    public ResponseItem<Invoice> updateInvoice(Invoice invoice) {

        Invoice newInvoice = invoiceRepository.findOne(invoice.getId());
        return update(newInvoice, invoice);
    }


    public List<DtoInvoice> getInvoicesFor1c(BasicFilter filter) {

        List<Invoice> invoices = invoiceRepository
                .findByDateBetweenAndNotForUploadOrderByDateDesc(
                        filter.getFromDate(), filter.getToDate(), false);

        if(invoices.size() > 0) {

            List<DtoInvoice> dtoInvoices = new ArrayList<DtoInvoice>();

            for (Invoice invoice: invoices) {

                disallowUploadFor1c(invoice.getId());
                DtoInvoice dtoInvoice = new DtoInvoice(invoice);

                for(InvoiceRow invoiceRow: invoice.getInvoiceRows())
                    dtoInvoice.getRows().add(new DtoInvoiceRow(invoiceRow));

                dtoInvoices.add(dtoInvoice);

            }

            return dtoInvoices;
        }

        return null;
    }

    public synchronized void disallowUploadFor1c(long id) {

        Invoice invoice = invoiceRepository.findOne(id);
        invoice.setNotForUpload(true);
        invoiceRepository.save(invoice);
    }

    public synchronized void deleteInvoice(long id) {

        Invoice invoice = invoiceRepository.findOne(id);
        invoice.setNotForUpload(true);
        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
    }

    public synchronized void allowUploadReport(long id) {

        Invoice invoice = invoiceRepository.findOne(id);
        invoice.setNotForUpload(false);
        if(!invoice.getDeleted())
            invoiceRepository.save(invoice);
    }

    private ResponseByInvoices
    getResults(Page<Invoice> page, SoldItemFilter filter) {

        ResponseByInvoices response =
            new ResponseByInvoices(ELEMENTS_FOUND, page.getContent(), true, page.getTotalPages());

        if(checkResponse(response.getEntityItems().size(), response))
            calcTotals(filter, abstractEntityManager, response);

        return response;
    }

    public ResponseByInvoices getInvoicesByFilter(SoldItemFilter filter) {

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());
        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);
        Page<Invoice> page =  invoiceRepository.findAll(ipb.buildByFilter(filter, abstractEntityManager.getEntityManager()), pageRequest);

        return getResults(page, filter);
    }

    public ResponseItemExt<InvoiceHeader>
    getInvoiceHeaders(SoldItemFilter filter) {

        ResponseItemExt<Invoice> responseByInvoices = getInvoicesByFilter(filter);

        if(responseByInvoices.getSuccess()) {

            ResponseItemExt<InvoiceHeader> headers
                    = new ResponseItemExt<InvoiceHeader>(
                            "", new ArrayList<InvoiceHeader>(),true,
                                                responseByInvoices.getNumberOfPages());

            for(Invoice invoice: responseByInvoices.getEntityItems())
                headers.getEntityItems().add(new InvoiceHeader(invoice));

            headers.setTotals(responseByInvoices.getTotals());
            headers.setBuyers(responseByInvoices.getBuyers());

            return headers;
        }

        return new ResponseItemExt<InvoiceHeader>(NOTHING_FOUND, new ArrayList<>(), false, 0);
    }

    public Long addInvoiceByFilter(SoldItemFilter filter) {

        List<SoldItem> sellings = getSoldItemHandler().getSoldItemsByFilter(filter);

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

        List<SoldItem> sellings = getSoldItemHandler().getSoldItemsByFilter(filter);
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
