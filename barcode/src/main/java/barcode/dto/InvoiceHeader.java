package barcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.Invoice;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xlinux on 21.11.18.
 */
public class InvoiceHeader {


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

    @JsonProperty("rows")
    private Integer numberOfRows;

    @JsonProperty("sum")
    private BigDecimal sum;

    @JsonProperty("deleted")
    private Boolean isDeleted;

    public InvoiceHeader() {};

    public InvoiceHeader(Invoice invoice) {

        this.setId(invoice.getId());

        this.setBuyerName(invoice.getBuyer().getName());

        this.setDocDate(invoice.getDate());

        this.setUserName(invoice.getUser().getFullName());

        this.setStockName(invoice.getStock().getName());

        this.setNumberOfRows(invoice.getInvoiceRows().size());

        this.setSum(invoice.getSum());

        this.setDeleted(invoice.getDeleted());



    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
