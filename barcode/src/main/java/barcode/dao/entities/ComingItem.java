package barcode.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.embeddable.Comment;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class ComingItem {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition="Decimal(19,4)")
    @JsonProperty("price")
    private BigDecimal priceIn;

    @Column(columnDefinition="Decimal(19,2)")
    private BigDecimal priceOut;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal quantity;

    @Column(name = "sum", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal sum;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal currentQuantity;
    private Date factDate;
    private Date lastChangeDate;

    @Column(name = "comment", nullable = false,
            columnDefinition="varchar(2000) COLLATE utf8_general_ci")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Document doc;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ElementCollection
    @JsonIgnore
    @JsonProperty("comments")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToMany(mappedBy = "coming", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SoldItem> sellings;

    public ComingItem() {}

    public ComingItem(ComingItem comingItem) {

        this.currentQuantity = comingItem.getCurrentQuantity();

        this.sum = comingItem.getSum();

        this.quantity = comingItem.getQuantity();
    }

    public ComingItem(Item item, Stock stock) {
        this.stock = stock;
        this.item = item;
    }

    public ComingItem(Item item, Stock stock, BigDecimal sum, BigDecimal quantity,
                      BigDecimal currentQuantity, Date lastChangeDate) {
        this.stock = stock;
        this.item = item;
        this.sum = sum;
        this.quantity = quantity;
        this.currentQuantity = currentQuantity;
        this.lastChangeDate = lastChangeDate;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPriceIn() { return priceIn; }
    public void setPriceIn(BigDecimal priceIn) { this.priceIn = priceIn;	}

    public BigDecimal getPriceOut() { return priceOut; }
    public void setPriceOut(BigDecimal priceOut) { this.priceOut = priceOut;	}

    public BigDecimal getQuantity() {return quantity;}
    public void setQuantity(BigDecimal quantity) {this.quantity = quantity;}

    public BigDecimal getCurrentQuantity() {return currentQuantity;}
    public void setCurrentQuantity(BigDecimal quantity) {this.currentQuantity = quantity;}

    public Document getDoc() { return doc; }
    public void setDoc(Document doc) { this.doc = doc; }

    public Stock getStock() { return stock; }
    public void setStock( Stock stock ) { this.stock = stock; }

    public Date getFactDate() { return factDate; }
    public void setFactDate(Date date) { this.factDate = date; }

    public Date getLastChangeDate() { return lastChangeDate; }
    public void setLastChangeDate(Date date) { this.lastChangeDate = date; }

    //    @JsonIgnore
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

    public Set<SoldItem> getSellings() {return this.sellings;}
    public void setSellings(Set<SoldItem> sellings) {this.sellings = sellings;}

    public String getComment() {

        return comment;
    }

    public void setComment(String comment) {

        this.comment = comment;
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
