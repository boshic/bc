package barcode.dto;

import barcode.dao.entities.basic.BasicNamedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by xlinux on 17.05.19.
 */
public class DtoBuyer extends BasicNamedEntity {

    @JsonIgnore
    private Integer numberOfSellings;
    private BigDecimal debt;

    public DtoBuyer() {}
    public DtoBuyer(Long id, String name, BigDecimal debt) {
        super(id,name);
        this.debt = debt;
    }

    public Integer getNumberOfSellings() {
        return numberOfSellings;
    }
    public void setNumberOfSellings(Integer numberOfSellings) {
        this.numberOfSellings = numberOfSellings;
    }

    public BigDecimal getDebt() {
        return debt;
    }

    public void setDebt(BigDecimal debt) {
        this.debt = debt;
    }
}
