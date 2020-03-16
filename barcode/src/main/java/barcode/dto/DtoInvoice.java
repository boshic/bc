package barcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.Invoice;
import barcode.dao.services.EntityHandlerImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xlinux on 14.09.18.
 */
public class DtoInvoice {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("date")
    private Date docDate;

    @JsonProperty("user")
    private String userName;

    @JsonProperty("stock")
    private String stockName;

    @JsonProperty("buyer")
    private String buyerName;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("rows")
    private List<DtoInvoiceRow> rows;


    public DtoInvoice() {};

    public DtoInvoice(Invoice invoice) {

        this.setId(invoice.getId());

        this.setBuyerName(invoice.getBuyer().getName());

        this.setDocDate(invoice.getDate());

        this.setUserName(invoice.getUser().getFullName());

        this.setStockName(invoice.getStock().getName());

        this.setComment(new EntityHandlerImpl().buildComment(invoice.getComments(), "","","", BigDecimal.ZERO));

        this.setRows(new ArrayList<DtoInvoiceRow>());
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public List<DtoInvoiceRow> getRows() {
        return rows;
    }

    public void setRows(List<DtoInvoiceRow> rows) {
        this.rows = rows;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
