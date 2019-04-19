package barcode.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Document> documents;

    public Supplier() {}

    public Supplier(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }
    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
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
