package barcode.dao.entities;

import barcode.dao.entities.basic.BasicNamedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
public class ItemSection extends BasicNamedEntity{

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Item> items;

    @Column(name = "perc_overhead_limit", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal percOverheadLimit;

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public ItemSection() {}

    public ItemSection(String name) {

        super(name);
    }

    public BigDecimal getPercOverheadLimit() {
        return percOverheadLimit;
    }

    public void setPercOverheadLimit(BigDecimal percOverheadLimit) {
        this.percOverheadLimit = percOverheadLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemSection)) return false;

        ItemSection that = (ItemSection) o;

        return (getId() != null ? getId().equals(that.getId()) : that.getId() == null);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
//        result = 31 * result + (getItems() != null ? getItems().hashCode() : 0);
        return result;
    }
}
