package barcode.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ItemSection {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Item> items;

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

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public ItemSection() {}

    public ItemSection(String name) {

        this.name = name;
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
