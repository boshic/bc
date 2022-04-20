package barcode.dao.entities.basic;

import barcode.dao.entities.basic.BasicEntity;
import barcode.utils.CommonUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by xlinux on 04.03.20.
 */
@MappedSuperclass
public class BasicNamedEntity extends BasicEntity {

    @Column(name = "name", nullable = false)
    private String name;

    public BasicNamedEntity() {}

    public BasicNamedEntity(String name) {
        this.name = CommonUtils.validateString(name);
    }
    public BasicNamedEntity(Long id, String name) {
        super(id);
        this.name = CommonUtils.validateString(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
