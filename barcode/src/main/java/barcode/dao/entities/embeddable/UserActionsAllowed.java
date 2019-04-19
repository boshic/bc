package barcode.dao.entities.embeddable;

import javax.persistence.Embeddable;

/**
 * Created by xlinux on 04.10.18.
 */
@Embeddable
public class UserActionsAllowed {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
