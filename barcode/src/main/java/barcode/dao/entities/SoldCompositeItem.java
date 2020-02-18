package barcode.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by xlinux on 13.02.20.
 */
@Entity
public class SoldCompositeItem {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "sum", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal sum;

    @Column(name = "price", columnDefinition="Decimal(19,2)")
    private BigDecimal price;

    @Column(name = "quantity", columnDefinition="Decimal(19,3)")
    private BigDecimal quantity;

    @OneToMany(mappedBy = "soldCompositeItem", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SoldItem> sellings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Set<SoldItem> getSellings() {
        return sellings;
    }

    public void setSellings(Set<SoldItem> sellings) {
        this.sellings = sellings;
    }
}
