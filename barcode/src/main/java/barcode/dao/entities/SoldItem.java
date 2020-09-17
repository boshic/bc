package barcode.dao.entities;


import barcode.dao.entities.basic.BasicOperationWithCommentEntity;
import com.querydsl.core.annotations.QueryInit;
import javax.persistence.*;
import java.math.BigDecimal;


@Entity
public class SoldItem extends BasicOperationWithCommentEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @ManyToOne
    @JoinColumn(name = "sold_composite_item_id")
    @QueryInit("*.*")
    private SoldCompositeItem soldCompositeItem;

    @ManyToOne
    @JoinColumn(name = "coming_id")
    @QueryInit("*.*")
    private ComingItem coming;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @Column(name = "sum", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal sum;

    @Transient
    private BigDecimal incomeSum;

    @Transient
    private BigDecimal incomeSumPercent;

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
        super(comment);
    }
    public SoldItem(ComingItem coming,
                    BigDecimal price,
                    BigDecimal quantity,
                    BigDecimal availQuantityByEan,
                    BigDecimal incomeSum,
                    BigDecimal incomeSumPercent) {

        this.coming = coming;
        this.price = price;
        this.quantity = quantity;
        this.availQuantityByEan = availQuantityByEan;
        this.incomeSum = incomeSum;
        this.incomeSumPercent = incomeSumPercent;
    }

    public SoldItem( ComingItem coming,
                     BigDecimal price,
                     BigDecimal vat,
                     BigDecimal quantity,
                     String comment,
                     Buyer buyer,
                     User user) {
        super(comment);
        this.coming = coming;
        this.price = price;
        this.vat = vat;
        this.quantity = quantity;
        this.buyer = buyer;
        this.user = user;


    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal surname) { this.price = surname;	}

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

    public BigDecimal getAvailQuantityByEan() {
        return availQuantityByEan;
    }

    public void setAvailQuantityByEan(BigDecimal availQuantityByEan) {
        this.availQuantityByEan = availQuantityByEan;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public SoldCompositeItem getSoldCompositeItem() {
        return soldCompositeItem;
    }

    public void setSoldCompositeItem(SoldCompositeItem soldCompositeItem) {
        this.soldCompositeItem = soldCompositeItem;
    }

    public BigDecimal getIncomeSum() {
        return incomeSum;
    }

    public void setIncomeSum(BigDecimal incomeSum) {
        this.incomeSum = incomeSum;
    }

    public BigDecimal getIncomeSumPercent() {
        return incomeSumPercent;
    }

    public void setIncomeSumPercent(BigDecimal incomeSumPercent) {
        this.incomeSumPercent = incomeSumPercent;
    }
}
