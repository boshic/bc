package barcode.dao.services;

import com.querydsl.core.types.Predicate;
import barcode.dao.entities.ComingItem;
import barcode.dao.entities.ComingReport;
import barcode.dao.entities.embeddable.ComingReportRow;
import barcode.dao.repositories.ComingItemRepository;
import barcode.dao.repositories.ComingReportRepository;
import barcode.dao.utils.ComingItemFilter;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xlinux on 30.10.18.
 */
@Service
public class ComingReportHandler {

    private ComingReportRepository comingReportRepository;

    private ComingItemRepository comingItemRepository;

    private UserHandler userHandler;

    private ItemHandler itemHandler;

    public ComingReportHandler(ComingReportRepository comingReportRepository,
                               ComingItemRepository comingItemRepository,
                               UserHandler userHandler, ItemHandler itemHandler) {

        this.comingReportRepository = comingReportRepository;

        this.comingItemRepository = comingItemRepository;

        this.userHandler = userHandler;

        this.itemHandler = itemHandler;
    }


    private ResponseItem<ComingReport> update(ComingReport newComingReport, ComingReport comingReport) {

        newComingReport.setUser(userHandler.getCurrentUser());

        newComingReport.setStock(comingReport.getStock());

        newComingReport.setComingReportRows(comingReport.getComingReportRows());

        comingReportRepository.save(newComingReport);

        return new ResponseItem<ComingReport>("Добавлен отчет по приходу", true, newComingReport);
    }

    public ResponseItem<ComingReport> addItem(ComingReport comingReport) {

        return update(new ComingReport(new Date()), comingReport);
    }

    public ResponseItem<ComingReport> addItemByFilter(ComingItemFilter filter) {

        itemHandler.checkEanInFilter(filter);

        Predicate predicate = ComingItemHandler.cipb.buildByFilter(filter);

        List<ComingItem> comings = comingItemRepository.findAll(predicate);

        if(comings.size() > 0) {

            ComingReport comingReport = new ComingReport(new Date());

            comingReport.setStock(filter.getStock());

            comingReport.setComingReportRows(new ArrayList<ComingReportRow>());

            for(ComingItem comingItem : comings)
                comingReport.getComingReportRows().add(new ComingReportRow(comingItem));

            return addItem(comingReport);

        }

        return new ResponseItem<ComingReport>("Отчет не добавлен", false, new ComingReport());
    }

    public ComingReport getItemById(Long id) {
        return comingReportRepository.findOne(id);
    }

    public ResponseItem<ComingReport> updateItem(ComingReport comingReport) {

        ComingReport newComingReport = comingReportRepository.findOne(comingReport.getId());

        return update(newComingReport, comingReport);
    }

}
