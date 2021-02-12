package barcode.dto;

import barcode.dao.entities.basic.BasicNamedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xlinux on 17.05.19.
 */
public class DtoBuyer extends BasicNamedEntity {

    @JsonIgnore
    private Integer numberOfSellings;

    public DtoBuyer(Long id, String name) {
        super(id,name);
    }

    public Integer getNumberOfSellings() {
        return numberOfSellings;
    }
    public void setNumberOfSellings(Integer numberOfSellings) {
        this.numberOfSellings = numberOfSellings;
    }
}
