package barcode.dao.services;

import barcode.enums.SystemMessage;
import com.querydsl.core.types.Predicate;
import barcode.dao.entities.ComingItem;
import barcode.dao.entities.ComingReport;
import barcode.dao.entities.embeddable.ComingReportRow;
import barcode.dao.repositories.ComingItemRepository;
import barcode.dao.repositories.ComingReportRepository;
import barcode.utils.ComingItemFilter;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xlinux on 30.10.18.
 */
@Service
public class ComingReportHandler extends  EntityHandlerImpl {

    private ComingReportRepository comingReportRepository;

    private ComingItemRepository comingItemRepository;

    private UserHandler userHandler;

    private ItemHandler itemHandler;

    private AbstractEntityManager abstractEntityManager;

    public ComingReportHandler(ComingReportRepository comingReportRepository,
                               ComingItemRepository comingItemRepository,
                               UserHandler userHandler, ItemHandler itemHandler,
                               AbstractEntityManager abstractEntityManager
    ) {

        this.comingReportRepository = comingReportRepository;

        this.comingItemRepository = comingItemRepository;

        this.userHandler = userHandler;

        this.itemHandler = itemHandler;

        this.abstractEntityManager = abstractEntityManager;
    }


    private ResponseItem<ComingReport> update(ComingReport newComingReport, ComingReport srcComingReport) {

        newComingReport.setUser(userHandler.getCurrentUser());

        newComingReport.setDate(new Date());

        newComingReport.setStock(srcComingReport.getStock());

        newComingReport.setComingReportRows(srcComingReport.getComingReportRows());

        comingReportRepository.save(newComingReport);

        return new ResponseItem<ComingReport>(SystemMessage.NEW_REPORT_ADDED.getMessage(), true, newComingReport);
    }

    public ResponseItem<ComingReport> addItem(ComingReport comingReport) {

        return update(new ComingReport(), comingReport);
    }

    public ResponseItem<ComingReport> addItemByFilter(ComingItemFilter filter) {

        itemHandler.checkEanInFilter(filter);

        Predicate predicate = ComingItemHandler.cipb.buildByFilter(filter, abstractEntityManager);

        List<ComingItem> comings = comingItemRepository.findAll(predicate);

        if(comings.size() > 0) {

            ComingReport comingReport = new ComingReport(filter.getStock(), new ArrayList<ComingReportRow>());
            for(ComingItem comingItem : comings)
                comingReport.getComingReportRows()
                        .add(new ComingReportRow(
                                comingItem.getItem(),
                                comingItem.getDoc(),
                                comingItem.getCurrentQuantity(),
                                comingItem.getItem().getPrice().compareTo(BigDecimal.ZERO) > 0 ?
                                    comingItem.getItem().getPrice() : comingItem.getPriceOut()));

            return addItem(comingReport);

        }

        return new ResponseItem<ComingReport>(NEW_REPORT_ADDING_FAILED, false, new ComingReport());
    }

    public ComingReport getItemById(Long id) {
        return comingReportRepository.findOne(id);
    }

    public ResponseItem<ComingReport> updateItem(ComingReport comingReport) {

        ComingReport newComingReport = comingReportRepository.findOne(comingReport.getId());

        return update(newComingReport, comingReport);
    }

}
