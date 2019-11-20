package barcode.dao.entities.embeddable;

import barcode.dao.entities.Stock;
import barcode.dao.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xlinux on 11.11.19.
 */
@Embeddable
public class InventoryRow {

    @JsonProperty("date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    public InventoryRow() {}

    public InventoryRow(User user, Stock stock, BigDecimal quantity) {

        this.date = new Date();
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
