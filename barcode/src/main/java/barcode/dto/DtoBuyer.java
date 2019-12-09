package barcode.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xlinux on 17.05.19.
 */
public class DtoBuyer {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonIgnore
    private Integer numberOfSellings;

    public DtoBuyer(Long id, String name, Integer numberOfSellings) {

        this.id = id;

        this.name = name;

        this.numberOfSellings = numberOfSellings;
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

    public Integer getNumberOfSellings() {
        return numberOfSellings;
    }

    public void setNumberOfSellings(Integer numberOfSellings) {
        this.numberOfSellings = numberOfSellings;
    }
}
