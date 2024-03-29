package barcode.dao.entities;

import barcode.dao.entities.basic.BasicCounterPartyEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Supplier extends BasicCounterPartyEntity {

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Document> documents;

    public Supplier() {}

    public Supplier(String name) {
        super(name);
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }

    @Column(name = "uses_vat", columnDefinition="tinyint(1) default 1")
    private Boolean usesVat;

    public Boolean getUsesVat() {
        return usesVat;
    }

    public void setUsesVat(Boolean usesVat) {
        this.usesVat = usesVat;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 31;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
//        result = 31 * result + (getDocuments() != null ? getDocuments().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Supplier)) return false;

        Supplier that = (Supplier) o;
        return (getId() != null ? getId().equals(that.getId()) : that.getId() == null);
    }
}
