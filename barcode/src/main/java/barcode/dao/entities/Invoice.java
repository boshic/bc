package barcode.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.embeddable.Comment;
import barcode.dao.entities.embeddable.InvoiceRow;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Date date;

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

    @ElementCollection
    @JsonIgnore
    @JsonProperty("comments")
    private List<Comment> comments;

    @Column(name = "sum", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal sum;


//    @Column(name = "comment", nullable = false)
//    @Column(name = "comment", columnDefinition="varchar(2000) COLLATE utf8_general_ci default ''")
    @Transient
    private String comment = "";

//    @Column(name = "has_stamp", columnDefinition="bit(1) default 0")
//    private Boolean hasStamp;

    public BigDecimal getSumOfRows() {

        BigDecimal sum = BigDecimal.ZERO;

        for (InvoiceRow invoiceRow: getInvoiceRows())
            sum = sum.add(invoiceRow.getSum());
//            sum = sum.add(invoiceRow.getQuantity().multiply(invoiceRow.getPrice())).setScale(2, BigDecimal.ROUND_HALF_UP);

        return sum;
    }


    public Invoice() {}
    public Invoice(Date date) {
        this.comments = new ArrayList<>();
        this.date = date;
    }

    public Invoice(Date date, Stock stock, Buyer buyer, List<InvoiceRow> invoiceRows) {
        this(date);
        this.stock = stock;
        this.buyer = buyer;
        this.invoiceRows = invoiceRows;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
