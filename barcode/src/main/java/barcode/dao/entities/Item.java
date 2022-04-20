package barcode.dao.entities;

import barcode.dao.entities.basic.BasicEntity;
import barcode.dao.entities.basic.BasicNamedEntity;
import barcode.dao.entities.embeddable.InventoryRow;
import barcode.dao.entities.embeddable.ItemComponent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity // This tells Hibernate to make a table out of this class
public class Item extends BasicNamedEntity {

    private String ean;

    @Column(name = "alter_name", columnDefinition="varchar(250) COLLATE utf8_general_ci default ''")
    private String alterName;

    @Column(name = "ean_synonym", columnDefinition="varchar(13) COLLATE utf8_general_ci default ''")
    private String eanSynonym;

    @Column(name = "predefined_quantity", columnDefinition="Decimal(9,2) default '0.00'")
    private BigDecimal predefinedQuantity;

    @Column(name = "per_unit_quantity", columnDefinition="Decimal(9,3) default '0.000'")
    private BigDecimal perUnitQuantity;

    @Column(name = "content_unit", columnDefinition="varchar(100) COLLATE utf8_general_ci default ''")
    private String contentUnit;

    @Column(name = "price", columnDefinition="Decimal(9,2) default '0.00'")
    private BigDecimal price;

    @Column(name = "unit", nullable = false)
    private String unit;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private ItemSection section;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ComingItem> comings;

    @ElementCollection
    @JsonProperty("components")
    private List<ItemComponent> components;

    @ElementCollection
    @JsonIgnore
    private List<InventoryRow> inventoryRows;

    public Item() {}
    public Item(ItemSection section) {
        super(section.getId(), section.getName());
    }


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

    public List<ItemComponent> getComponents() {
        return components;
    }

    public void setComponents(List<ItemComponent> components) {
        this.components = components;
    }

    public String getAlterName() {
        return alterName;
    }

    public void setAlterName(String alterName) {
        this.alterName = alterName;
    }

    public List<InventoryRow> getInventoryRows() {
        return inventoryRows;
    }

    public void setInventoryRows(List<InventoryRow> inventoryRows) {
        this.inventoryRows = inventoryRows;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPerUnitQuantity() {
        return perUnitQuantity;
    }

    public void setPerUnitQuantity(BigDecimal perUnitQuantity) {
        this.perUnitQuantity = perUnitQuantity;
    }

    public String getContentUnit() {
        return contentUnit;
    }

    public void setContentUnit(String contentUnit) {
        this.contentUnit = contentUnit;
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
        int result = getId() != null ? getId().hashCode() : 31;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEan() != null ? getEan().hashCode() : 0);
        result = 31 * result + (getUnit() != null ? getUnit().hashCode() : 0);
        result = 31 * result + (getSection() != null ? getSection().hashCode() : 0);
//        result = 31 * result + (getComings() != null ? getComings().hashCode() : 0);
        return result;
    }
}
