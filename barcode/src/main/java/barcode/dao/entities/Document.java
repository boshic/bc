package barcode.dao.entities;

import barcode.dao.entities.basic.BasicOperationEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Document extends BasicOperationEntity{

    private String name;

    @OneToMany(mappedBy = "doc", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ComingItem> comings;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public Set<ComingItem> getComings() {return this.comings;}
    public void setComings(Set<ComingItem> comings) {this.comings = comings;}

    public Supplier getSupplier() {return this.supplier;}
    public void setSupplier(Supplier supplier) {this.supplier = supplier;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document that = (Document) o;
        return (getId() != null ? getId().equals(that.getId()) : that.getId() == null);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
//        result = 31 * result + (getComings() != null ? getComings().hashCode() : 0);
        result = 31 * result + (getSupplier() != null ? getSupplier().hashCode() : 0);
        return result;
    }
}
