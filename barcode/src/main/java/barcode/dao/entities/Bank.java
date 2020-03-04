package barcode.dao.entities;

import barcode.dao.entities.basic.BasicNamedEntity;

import javax.persistence.*;

@Entity
public class Bank extends BasicNamedEntity {

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "code", nullable = false)
    private String code;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
