package barcode.dao.entities;

import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import barcode.dao.entities.embeddable.Comment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class ComingItem extends BasicOperationWithCommentEntity{

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

    private Date lastChangeDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Document doc;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToMany(mappedBy = "coming", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SoldItem> sellings;

    public ComingItem() {}

    public ComingItem(Item item, BigDecimal currentQuantity, BigDecimal priceOut) {

        this.item = item;
        this.currentQuantity = currentQuantity;
        this.priceOut = priceOut;

    }

    public ComingItem(ComingItem comingItem) {
        this.currentQuantity = comingItem.getCurrentQuantity();
        this.sum = comingItem.getSum();
        this.quantity = comingItem.getQuantity();
    }

    public ComingItem(Item item,
                      Stock stock) {
        this.stock = stock;
        this.item = item;
    }

    public ComingItem(Item item,
                      Stock stock,
                      BigDecimal priceIn,
                      BigDecimal currentQuantity,
                      BigDecimal quantity
                      ) {

        this.item = item;
        this.stock = stock;
        this.currentQuantity = currentQuantity;
        this.quantity = quantity;
        this.priceIn = priceIn;

    }

    public ComingItem(Item item,
                      Stock stock,
                      BigDecimal sum,
                      BigDecimal quantity,
                      BigDecimal priceIn,
                      BigDecimal priceOut,
                      BigDecimal currentQuantity,
                      Date lastChangeDate) {

        this.stock = stock;
        this.item = item;
        this.sum = sum;
        this.quantity = quantity;
        this.priceIn = priceIn;
        this.priceOut = priceOut;
        this.currentQuantity = currentQuantity;
        this.lastChangeDate = lastChangeDate;
    }

    public ComingItem(BigDecimal priceIn,
                      BigDecimal priceOut,
                      BigDecimal quantity,
                      List<Comment> comments,
                      Document document,
                      Item item,
                      Stock stock,
                      User user) {
        super(comments);
        this.priceIn = priceIn;
        this.priceOut = priceOut;
        this.quantity = quantity;
        this.doc = document;
        this.item = item;
        this.stock = stock;
        this.user = user;
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

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
