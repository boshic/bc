package barcode.dao.entities.basic;

import barcode.dao.entities.basic.BasicEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Created by xlinux on 04.03.20.
 */
@MappedSuperclass
public class BasicOperationEntity extends BasicEntity {

    private Date date;


    public BasicOperationEntity() {}

    public BasicOperationEntity(Date date) {

        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
