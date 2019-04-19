package barcode.dao.entities.embeddable;

import barcode.dao.entities.Stock;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AllowedStock {

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllowedStock)) return false;

        AllowedStock that = (AllowedStock) o;

        return getStock().equals(that.getStock());
    }

    @Override
    public int hashCode() {
        return getStock().hashCode();
    }
}
