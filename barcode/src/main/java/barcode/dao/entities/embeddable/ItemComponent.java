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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    //    public Item getComponent() {
//        return component;
//    }
//
//    public void setComponent(Item component) {
//        this.component = component;
//    }

    //    public Item getItem() {
//        return component;
//    }
//
//    public void setItem(Item item) {
//        this.component = item;
//    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
