package barcode.dao.entities.embeddable;

import barcode.dao.entities.Item;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by xlinux on 30.07.19.
 */
@Embeddable
public class ItemComponent {

    @ManyToOne
    @JoinColumn(name = "component_id")
    private Item item;

    @Column(columnDefinition="Decimal(19,3)")
    private BigDecimal quantity;

    @Column(columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal price;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
