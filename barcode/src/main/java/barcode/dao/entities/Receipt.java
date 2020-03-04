package barcode.dao.entities;

import barcode.dao.entities.basic.BasicOperationEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * Created by xlinux on 27.11.19.
 */
@Entity
public class Receipt extends BasicOperationEntity{

    @Column(name = "sum", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal sum;

    @Column(name = "number_of_positions")
    private Integer numberOfPositions;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SoldItem> sellings;

    public Receipt() {}

    public Receipt(Date date, BigDecimal sum, Integer numberOfPositions, User user, Buyer buyer) {

        super(date);
        this.sum = sum;
        this.numberOfPositions = numberOfPositions;
        this.user = user;
        this.buyer = buyer;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Integer getNumberOfPositions() {
        return numberOfPositions;
    }

    public void setNumberOfPositions(Integer numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Set<SoldItem> getSellings() {
        return sellings;
    }

    public void setSellings(Set<SoldItem> sellings) {
        this.sellings = sellings;
    }
}
