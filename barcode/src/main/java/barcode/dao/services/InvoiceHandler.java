package barcode.dao.services;

import barcode.dao.entities.embeddable.InvoiceRow;
import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;
import barcode.dao.entities.SoldItem;
import barcode.dao.predicates.InvoicesPredicatesBuilder;
import barcode.dao.repositories.InvoiceRepository;
import barcode.dao.utils.BasicFilter;
import barcode.dao.utils.SoldItemFilter;
import barcode.dto.*;
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

    private static final String NEW_INVOICE_ADDED = "добавлен отчет";

    private static final String WRITE_OFF_CAUSE = "причина списания";

    private static final String WRITE_OFF_ACT_ADDED = "добавлен акт на списание";

    private static InvoicesPredicatesBuilder ipb = new InvoicesPredicatesBuilder();

    private InvoiceRepository invoiceRepository;

    private SoldItemHandler soldItemHandler;

    private UserHandler userHandler;

    public InvoiceHandler(InvoiceRepository invoiceRepository, UserHandler userHandler, SoldItemHandler soldItemHandler) {

        this.soldItemHandler = soldItemHandler;

        this.userHandler = userHandler;

        this.invoiceRepository = invoiceRepository;
    }

    private ResponseItem<Invoice> update(Invoice newInvoice, Invoice invoice) {

        newInvoice.setUser(userHandler.getCurrentUser());

        newInvoice.setBuyer(invoice.getBuyer());

        newInvoice.setStock(invoice.getStock());

        newInvoice.setInvoiceRows(invoice.getInvoiceRows());

        if(invoice.getComments() != null)
            newInvoice.getComments().addAll(invoice.getComments());
        else
            newInvoice.setComment(
                this.buildComment(newInvoice.getComments(), invoice.getComment() ,
                        userHandler.checkUser(invoice.getUser(), null ).getFullName(),
                        NEW_INVOICE_ADDED)
            );

        invoiceRepository.save(newInvoice);

        return new ResponseItem<Invoice>(NEW_INVOICE_ADDED, true, newInvoice);
    }

    public ResponseItem<Invoice> addInvoice(Invoice invoice) {

        Invoice newInvoice = new Invoice(new Date());

        return update(newInvoice, invoice);
    }

    public Invoice getWriteOffActById(Long id) {

        Invoice invoice = invoiceRepository.findOne(id);

        invoice.setComment(this.getCommentByAction(invoice.getComments(), WRITE_OFF_ACT_ADDED));

        return invoice;
    }

    public Invoice getItemById(Long id) {

        Invoice invoice = invoiceRepository.findOne(id);

//        invoice.setComment(
//                this.buildComment(invoice.getComments(), invoice.getComment() ,
//                        userHandler.checkUser(invoice.getUser(), null ).getFullName(),
//                        "выдан отчет")
//        );
//
//        invoiceRepository.save(invoice);

        return invoice;
    }

//    public Invoice get

    public ResponseItem<Invoice> updateInvoice(Invoice invoice) {

        Invoice newInvoice = invoiceRepository.findOne(invoice.getId());

        return update(newInvoice, invoice);
    }


    public List<DtoInvoice> getInvoicesFor1c(BasicFilter filter) {

        List<Invoice> invoices = invoiceRepository
                .findByDateBetweenOrderByDateDesc(filter.getFromDate(), filter.getToDate());

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

    public ResponseByInvoices<Invoice> getInvoicesByFilter(SoldItemFilter filter) {

//        Sort sort = new Sort(Sort.Direction.DESC, "date");

        Sort sort = new Sort(Sort.Direction.fromStringOrNull(filter.getSortDirection()), filter.getSortField());

        PageRequest pageRequest = new PageRequest(filter.getPage() - 1, filter.getRowsOnPage(), sort);

        Page<Invoice> page =  invoiceRepository.findAll(ipb.buildByFilter(filter), pageRequest);

        List<Invoice> result = page.getContent();

        if (result.size() > 0) {

            ResponseByInvoices<Invoice> ribyi =
                    new ResponseByInvoices<Invoice>(
                            ELEMENTS_FOUND, result, true, page.getTotalPages());

            if(filter.getCalcTotal())
                ribyi.calcTotals(invoiceRepository.findAll(ipb.buildByFilter(filter)));

            return ribyi;
        }

        return new ResponseByInvoices<Invoice>(NOTHING_FOUND, new ArrayList<Invoice>(),
                false, 0);

    }

    public ResponseItem<InvoiceHeader> getInvoiceHeaders(SoldItemFilter filter) {

        ResponseByInvoices<Invoice> responseByInvoices = getInvoicesByFilter(filter);

        if(responseByInvoices.getSuccess()) {

            ResponseByInvoices<InvoiceHeader> headers
                    = new ResponseByInvoices<InvoiceHeader>(
                            "", new ArrayList<InvoiceHeader>(),true,
                                                responseByInvoices.getNumberOfPages());

            for(Invoice invoice: responseByInvoices.getItems())
                headers.getItems().add(new InvoiceHeader(invoice));

            return headers;
        }

        return new ResponseItem<>(NOTHING_FOUND,false);
    }

    public Long addInvoiceByFilter(SoldItemFilter filter) {

        List<SoldItem> sellings = soldItemHandler.getSoldItemsByFilter(filter);

        if(sellings.size() > 0) {

            Invoice invoice = new Invoice(new Date(), filter.getStock(), filter.getBuyer(), new ArrayList<InvoiceRow>());

            this.buildComment(invoice.getComments(), "",
                    userHandler.checkUser(invoice.getUser(), null ).getFullName(),
                    NEW_INVOICE_ADDED);

            for(SoldItem soldItem: sellings)
                if(soldItem.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                    invoice.getInvoiceRows().add(new InvoiceRow(soldItem,"", soldItem.getPrice()));

            return addInvoice(invoice).getItem().getId();
        }

        return null;
    }


    public Long addWriteOffAct(SoldItemFilter filter) {

        List<SoldItem> sellings = soldItemHandler.getSoldItemsByFilter(filter);

        if(sellings.size() > 0) {

            Invoice invoice = new Invoice(new Date(), filter.getStock(), filter.getBuyer(), new ArrayList<InvoiceRow>());

            String comment = "с " + DATE_FORMAT_WO_TIME.format(filter.getFromDate()) + " по " +
                    DATE_FORMAT_WO_TIME.format(filter.getToDate());

            this.buildComment(invoice.getComments(), comment,
                    userHandler.checkUser(invoice.getUser(), null ).getFullName(),
                    WRITE_OFF_ACT_ADDED);

            for(SoldItem soldItem: sellings)
                if(soldItem.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                    invoice.getInvoiceRows().add(new InvoiceRow(
                        soldItem,
                        DATE_FORMAT_WITH_TIME.format(soldItem.getDate()) + SPACE +
                        this.getCommentByAction(soldItem.getComments(), WRITE_OFF_CAUSE),
                        soldItem.getComing().getPriceIn()));


            return addInvoice(invoice).getItem().getId();
        }

        return null;
    }

}