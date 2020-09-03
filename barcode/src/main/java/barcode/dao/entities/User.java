package barcode.dao.entities;

import barcode.dao.entities.basic.BasicNamedEntity;
import barcode.dao.entities.embeddable.InventoryRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import barcode.dao.entities.embeddable.AllowedStock;

import javax.persistence.*;
import java.util.Set;

@Entity // This tells Hibernate to make a table out of this class
public class User extends BasicNamedEntity{

    @JsonIgnore
    private Byte active;

    @JsonIgnore
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "shade_when_quyering", nullable = false)
    private Boolean shadeWhenQuyering;

    private String role;

    private String position;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ComingItem> comings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SoldItem> sellings;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private Set<InventoryRow> inventoryRows;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Invoice> invoices;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @Transient private String vendor = "made by PAB p.vilmen@mail.ru";

    @ElementCollection
    private Set<AllowedStock> stocksAllowed;

    @ElementCollection
    @JsonProperty("actsAllowed")
    private Set<String> actionsAllowed;

    @Lob
    @Column(name = "stamp_image", columnDefinition="mediumblob")
    @JsonIgnore
    private byte[] stamp;

    @Transient
    private byte[] stampForInvoices;

    public byte[] getStamp() {
        return stamp;
    }

    public void setStamp(byte[] stamp) {
        this.stamp = stamp;
    }

    public User() {}

    public Byte getActive() {return this.active;}
    public void setActive(Byte active) {this.active = active;}

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}

    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}

    public Stock getStock() { return stock; }
    public void setStock( Stock stock ) { this.stock = stock; }

    public Set<ComingItem> getComings() {
        return comings;
    }
    public void setComings(Set<ComingItem> comings) {
        this.comings = comings;
    }

    public Set<SoldItem> getSellings() {
        return sellings;
    }
    public void setSellings(Set<SoldItem> sellings) {
        this.sellings = sellings;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<AllowedStock> getStocksAllowed() { return stocksAllowed; }

    public void setStocksAllowed(Set<AllowedStock> stocksAllowed) {
        this.stocksAllowed = stocksAllowed;
    }

    public Set<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(Set<Invoice> invoices) {
        this.invoices = invoices;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getShadeWhenQuyering() {
        return shadeWhenQuyering;
    }

    public void setShadeWhenQuyering(Boolean shadeWhenQuyering) {
        this.shadeWhenQuyering = shadeWhenQuyering;
    }

    public Set<String> getUserActionsAllowed() {
        return actionsAllowed;
    }

    public void setUserActionsAllowed(Set<String> userActionsAllowed) {
        this.actionsAllowed = userActionsAllowed;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Set<String> getActionsAllowed() {
        return actionsAllowed;
    }

    public void setActionsAllowed(Set<String> actionsAllowed) {
        this.actionsAllowed = actionsAllowed;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public byte[] getStampForInvoices() {
        return stampForInvoices;
    }

    public void setStampForInvoices(byte[] stampForInvoices) {
        this.stampForInvoices = stampForInvoices;
    }
}
