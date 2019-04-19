package barcode.dao.entities;

import barcode.dao.entities.embeddable.ComingReportRow;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by xlinux on 30.10.18.
 */
@Entity
public class ComingReport {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ElementCollection
    @JsonProperty("rows")
    @Column(name = "rows")
    private List<ComingReportRow> comingReportRows;

    public ComingReport() {}

    public ComingReport(Date date) {

        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public List<ComingReportRow> getComingReportRows() {
        return comingReportRows;
    }

    public void setComingReportRows(List<ComingReportRow> comingReportRows) {
        this.comingReportRows = comingReportRows;
    }
}
