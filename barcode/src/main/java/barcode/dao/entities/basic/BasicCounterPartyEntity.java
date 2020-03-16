package barcode.dao.entities.basic;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by xlinux on 16.03.20.
 */
@MappedSuperclass
public class BasicCounterPartyEntity extends BasicNamedEntity{

    @Column(name = "address", columnDefinition = "varchar(254) COLLATE utf8_general_ci default ''")
    private String address;

    @Column(name = "use_for_inventory", columnDefinition="tinyint(1) default 0")
    private Boolean useForInventory;

    @Column(columnDefinition="varchar(9) COLLATE utf8_general_ci default ''" )
    private String unp;

    public BasicCounterPartyEntity() {}
    public BasicCounterPartyEntity(String name) {
        super(name);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getUseForInventory() {
        return useForInventory;
    }

    public void setUseForInventory(Boolean useForInventory) {
        this.useForInventory = useForInventory;
    }

    public String getUnp() {
        return unp;
    }

    public void setUnp(String unp) {
        this.unp = unp;
    }
}
