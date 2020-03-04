package barcode.dao.entities;

import barcode.dao.entities.basic.BasicEntity;
import barcode.dao.entities.basic.BasicNamedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "buyer")
public class Buyer extends BasicNamedEntity {

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "sell_by_coming_prices", columnDefinition="tinyint(1) default 0")
    private Boolean sellByComingPrices;

    @Column(name = "use_for_inventory", columnDefinition="tinyint(1) default 0")
    private Boolean useForInventory;

    private BigDecimal debt;

    @Column(name = "discount")
    private Integer discount;

    private Date lastPayDate;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SoldItem> sellings;

    @Column(columnDefinition="varchar(9) COLLATE utf8_general_ci")
    private String unp;

    @Column(columnDefinition="varchar(28) COLLATE utf8_general_ci")
    private String account;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    public Buyer() {}

    public BigDecimal getDebt() {return this.debt;}
    public void setDebt(BigDecimal debt) {this.debt = debt;}

    public Date getLastPayDate() {return this.lastPayDate;}
    public void setLastPayDate(Date lastPayDate) {this.lastPayDate = lastPayDate;}

    public Set<SoldItem> getSellings() {
        return sellings;
    }
    public void setSellings(Set<SoldItem> sellings) {
        this.sellings = sellings;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getUnp() {
        return unp;
    }

    public void setUnp(String unp) {
        this.unp = unp;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Boolean getSellByComingPrices() {
        return sellByComingPrices;
    }

    public void setSellByComingPrices(Boolean sellByComingPrices) {
        this.sellByComingPrices = sellByComingPrices;
    }

    public Boolean getUseForInventory() {
        return useForInventory;
    }

    public void setUseForInventory(Boolean useForInventory) {
        this.useForInventory = useForInventory;
    }
}

