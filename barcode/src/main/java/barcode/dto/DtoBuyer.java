package barcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xlinux on 17.05.19.
 */
public class DtoBuyer {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    public DtoBuyer(Long id, String name) {

        this.id = id;

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
}
