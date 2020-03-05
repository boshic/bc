package barcode.dao.entities;

import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.embeddable.InvoiceRow;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Entity
public class Invoice extends BasicOperationWithCommentEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ElementCollection
    @JsonProperty("rows")
    private List<InvoiceRow> invoiceRows;

    @Column(name = "sum", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal sum;

    @Column(name = "is_deleted", columnDefinition="tinyint(1) default 0")
    private Boolean isDeleted;

    public BigDecimal getSumOfRows() {

        BigDecimal sum = BigDecimal.ZERO;

        for (InvoiceRow invoiceRow: getInvoiceRows())
            sum = sum.add(invoiceRow.getSum());

        return sum;
    }

    public Invoice() {}

    public Invoice(Date date) {
        super(date);
    }

    public Invoice(Date date, Stock stock, Buyer buyer, List<InvoiceRow> invoiceRows) {
        super(date);
        this.stock = stock;
        this.buyer = buyer;
        this.invoiceRows = invoiceRows;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<InvoiceRow> getInvoiceRows() {
        return invoiceRows;
    }

    public void setInvoiceRows(List<InvoiceRow> invoiceRows) {
        this.invoiceRows = invoiceRows;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
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
