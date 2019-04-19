package barcode.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryInit;
import barcode.dao.entities.embeddable.Comment;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//@Config(entityAccessors=true)
@Entity
public class SoldItem {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coming_id")
    @QueryInit("*.*")
    private ComingItem coming;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @Column(name = "comment", nullable = false, columnDefinition="varchar(2500) COLLATE utf8_general_ci")
    private String comment;

    @ElementCollection
    @JsonIgnore
    @JsonProperty("comments")
    private List<Comment> comments;

    private Date date;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal quantity;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal availQuantityByEan;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal quantityBeforeSelling;

    @Column(columnDefinition="Decimal(19,2)")
    private BigDecimal price;

    @Column(columnDefinition="Decimal(19,2) default '0.00'")
    private BigDecimal vat;

    public SoldItem() {}
    public SoldItem(String comment) {
        this.comment = comment;
    }
    public SoldItem(ComingItem coming, BigDecimal price, BigDecimal quantity, BigDecimal availQuantityByEan) {
        this.coming = coming;
        this.price = price;
        this.quantity = quantity;
        this.availQuantityByEan = availQuantityByEan;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal surname) { this.price = surname;	}

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public BigDecimal getQuantity() {return quantity;}
    public void setQuantity(BigDecimal quantity) {this.quantity = quantity;}

    public BigDecimal getQuantityBeforeSelling() {return quantityBeforeSelling;}
    public void setQuantityBeforeSelling(BigDecimal quantityBeforeSelling) {this.quantityBeforeSelling = quantityBeforeSelling;}

    public ComingItem getComing() {return this.coming;}
    public void setComing(ComingItem coming) {this.coming = coming;}

    public User getUser() {return this.user;}
    public void setUser(User user) {this.user = user;}

    public Buyer getBuyer() {return this.buyer;}
    public void setBuyer(Buyer buyer) {this.buyer = buyer;}

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getAvailQuantityByEan() {
        return availQuantityByEan;
    }

    public void setAvailQuantityByEan(BigDecimal availQuantityByEan) {
        this.availQuantityByEan = availQuantityByEan;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }
}
