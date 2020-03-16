package barcode.dao.entities.embeddable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by xlinux on 07.03.19.
 */
@Embeddable
public class Comment {

    @Column(name = "text", columnDefinition="varchar(2000) COLLATE utf8_general_ci default ''")
    private String text;

    @Column(name = "user", columnDefinition="varchar(500) COLLATE utf8_general_ci default ''")
    private String userName;

    @Column(name = "action", columnDefinition="varchar(200) COLLATE utf8_general_ci default ''")
    private String action;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @Column(name = "quantity", columnDefinition="Decimal(12,2) default '0.00'")
    private BigDecimal quantity;

    public Comment() {}
    public Comment(String text, String user, String action, Date date, BigDecimal quantity) {
        this.userName = user;
        this.action = action;
        this.date = date;
        this.text = text;
        this.quantity = quantity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
