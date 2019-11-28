package barcode.dao.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xlinux on 27.11.19.
 */
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

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

    public Recipe() {}

    public Recipe(Date date, BigDecimal sum, Integer numberOfPositions, User user, Buyer buyer) {

        this.date = date;
        this.sum = sum;
        this.numberOfPositions = numberOfPositions;
        this.user = user;
        this.buyer = buyer;
    }

    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
