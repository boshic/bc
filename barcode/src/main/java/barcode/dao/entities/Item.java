package barcode.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity // This tells Hibernate to make a table out of this class
public class Item {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
//    private BigDecimal price;
    private String ean;

    @Column(name = "ean_synonym", columnDefinition="varchar(13) COLLATE utf8_general_ci default ''")
    private String eanSynonym;

    @Column(name = "predefined_quantity", columnDefinition="Decimal(9,2) default '0.00'")
    private BigDecimal predefinedQuantity;

    @Column(name = "unit", nullable = false)
    private String unit;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private ItemSection section;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ComingItem> comings;

    public Item () {
    }

    public Item(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

//    public BigDecimal getPrice() { return price; }
//    public void setPrice(BigDecimal surname) { this.price = surname;	}

    public String getEan() { return ean; }
    public void setEan(String ean) { this.ean = ean; }

    public Set<ComingItem> getComings() {
        return comings;
    }
    public void setComings(Set<ComingItem> comings) {
        this.comings = comings;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ItemSection getSection() {
        return section;
    }

    public void setSection(ItemSection section) {
        this.section = section;
    }

    public String getEanSynonym() {
        return eanSynonym;
    }

    public void setEanSynonym(String eanSynonym) {
        this.eanSynonym = eanSynonym;
    }

    public BigDecimal getPredefinedQuantity() {
        return predefinedQuantity;
    }

    public void setPredefinedQuantity(BigDecimal predefinedQuantity) {
        this.predefinedQuantity = predefinedQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;

        Item that = (Item) o;
        return (getId() != null ? getId().equals(that.getId()) : that.getId() == null);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEan() != null ? getEan().hashCode() : 0);
        result = 31 * result + (getUnit() != null ? getUnit().hashCode() : 0);
        result = 31 * result + (getSection() != null ? getSection().hashCode() : 0);
//        result = 31 * result + (getComings() != null ? getComings().hashCode() : 0);
        return result;
    }
}
