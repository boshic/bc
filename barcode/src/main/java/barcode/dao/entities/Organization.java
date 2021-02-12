package barcode.dao.entities;


import barcode.dao.entities.basic.BasicNamedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
public class Organization extends BasicNamedEntity{

    @Column(name = "short_name", columnDefinition="varchar(250) COLLATE utf8_general_ci default ''")
    private String shortName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "reg_num", nullable = false)
    private String regNum;

    @Column(name = "bank_account", nullable = false)
    private String bankAccount;

    @Column(name="boss_position", columnDefinition="varchar(250) COLLATE utf8_general_ci")
    private String bossPosition;

    @Column(name="boss_name", columnDefinition="varchar(250) COLLATE utf8_general_ci")
    private String bossName;

    @Column(name="position_fc", columnDefinition="varchar(250) COLLATE utf8_general_ci")
    private String positionForContracts;

    @Column(name="boss_fc", columnDefinition="varchar(250) COLLATE utf8_general_ci")
    private String bossNameForContracts;


    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @Column(name = "vat_value", nullable = false)
    private BigDecimal vatValue;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Stock> stocks;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getVatValue() {
        return vatValue;
    }

    public void setVatValue(BigDecimal vatValue) {
        this.vatValue = vatValue;
    }

    public Set<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(Set<Stock> stocks) {
        this.stocks = stocks;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getPositionForContracts() {
        return positionForContracts;
    }

    public void setPositionForContracts(String positionForContracts) {
        this.positionForContracts = positionForContracts;
    }

    public String getBossNameForContracts() {
        return bossNameForContracts;
    }

    public void setBossNameForContracts(String bossNameForContracts) {
        this.bossNameForContracts = bossNameForContracts;
    }

    public String getBossPosition() {
        return bossPosition;
    }

    public void setBossPosition(String bossPosition) {
        this.bossPosition = bossPosition;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}