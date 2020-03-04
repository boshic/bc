package barcode.dao.entities;

import barcode.dao.entities.basic.BasicNamedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Stock extends BasicNamedEntity{

    private boolean allowAll;

    @Column(name = "address", nullable = false)
    private String address;

    @Transient private boolean selected;

    private boolean retail;
    private boolean priceByAvailable;
    private boolean incomePrices;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ComingItem> comings;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users;

    public boolean isAllowAll() {
        return allowAll;
    }

    public void setAllowAll(boolean allowAll) {
        this.allowAll = allowAll;
    }

    public Set<ComingItem> getComings() {return this.comings;}
    public void setComings(Set<ComingItem> comings) {this.comings = comings;}

    public Set<User> getUsers() {return this.users;}
    public void setUsers(Set<User> users) {this.users = users;}

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isRetail() {
        return retail;
    }

    public void setRetail(boolean retail) {
        this.retail = retail;
    }

    public boolean isPriceByAvailable() {
        return priceByAvailable;
    }

    public void setPriceByAvailable(boolean priceByAvailable) {
        this.priceByAvailable = priceByAvailable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isIncomePrices() {
        return incomePrices;
    }

    public void setIncomePrices(boolean incomePrices) {
        this.incomePrices = incomePrices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;

        Stock that = (Stock) o;
        return (getId() != null ? getId().equals(that.getId()) : that.getId() == null);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (isAllowAll() ? 1 : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (isSelected() ? 1 : 0);
        result = 31 * result + (isRetail() ? 1 : 0);
        result = 31 * result + (isPriceByAvailable() ? 1 : 0);
        result = 31 * result + (isIncomePrices() ? 1 : 0);
        result = 31 * result + (getOrganization() != null ? getOrganization().hashCode() : 0);
//        result = 31 * result + (getComings() != null ? getComings().hashCode() : 0);
//        result = 31 * result + (getUsers() != null ? getUsers().hashCode() : 0);
        return result;
    }
}
