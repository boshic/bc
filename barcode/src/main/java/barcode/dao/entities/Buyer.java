package barcode.dao.entities;

import barcode.dao.entities.basic.BasicCounterPartyEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "buyer")
public class Buyer extends BasicCounterPartyEntity {

    @Column(name = "sell_by_coming_prices", columnDefinition="tinyint(1) default 0")
    private Boolean sellByComingPrices;

    @Column(name = "exclude_expenses_from_income", columnDefinition="tinyint(1) default 0")
    private Boolean excludeExpensesFromIncome;

    @Column(name = "do_not_use_for_deductions", columnDefinition="tinyint(1) default 0")
    private Boolean doNotUseForDeductions;

    @Column(name = "debt", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal debt;

    @Column(name = "discount")
    private Integer discount;

    private Date lastPayDate;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SoldItem> sellings;

    @Column(columnDefinition="varchar(28) COLLATE utf8_general_ci")
    private String account;

    @Column(columnDefinition="varchar(100) COLLATE utf8_general_ci default 'Продажа'")
    private String commentAction;

    @Column(name = "invoice_days_valid", columnDefinition = "integer default 3")
    private Integer invoiceDaysValid;

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

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
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

    public Boolean getExcludeExpensesFromIncome() {
        return excludeExpensesFromIncome;
    }

    public void setExcludeExpensesFromIncome(Boolean excludeExpensesFromIncome) {
        this.excludeExpensesFromIncome = excludeExpensesFromIncome;
    }

    public Integer getInvoiceDaysValid() {
        return invoiceDaysValid;
    }

    public void setInvoiceDaysValid(Integer invoiceDaysValid) {
        this.invoiceDaysValid = invoiceDaysValid;
    }

    public Boolean getDoNotUseForDeductions() {
        return doNotUseForDeductions;
    }

    public void setDoNotUseForDeductions(Boolean doNotUseForDeductions) {
        this.doNotUseForDeductions = doNotUseForDeductions;
    }

    public String getCommentAction() {
        return commentAction;
    }

    public void setCommentAction(String commentAction) {
        this.commentAction = commentAction;
    }
}

