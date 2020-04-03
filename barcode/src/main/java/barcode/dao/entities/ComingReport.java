package barcode.dao.entities;

import barcode.dao.entities.basic.BasicOperationEntity;
import barcode.dao.entities.embeddable.ComingReportRow;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class ComingReport extends BasicOperationEntity{

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
        super(date);
    }

    public ComingReport(Stock stock, List<ComingReportRow> rows) {

        this.stock = stock;
        this.comingReportRows = rows;
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
