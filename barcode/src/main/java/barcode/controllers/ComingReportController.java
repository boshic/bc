package barcode.controllers;

import barcode.dao.entities.ComingReport;
import barcode.dao.services.ComingReportHandler;
import barcode.dao.utils.ComingItemFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xlinux on 30.10.18.
 */
@Controller
public class ComingReportController {

    private ComingReportHandler comingReportHandler;

    public ComingReportController(ComingReportHandler comingReportHandler) {

        this.comingReportHandler = comingReportHandler;
    }

    @RequestMapping(value = "/addComingReportByFilter", method = RequestMethod.POST)
    public @ResponseBody
    Long addComingReportByFilter(@RequestBody ComingItemFilter filter) {

        if(filter != null)
            return this.comingReportHandler.addItemByFilter(filter).getEntityItem().getId();

        return 0L;
    }

    @RequestMapping(value = "/addComingReport", method = RequestMethod.POST)
    public @ResponseBody
    Long addComingReport(@RequestBody ComingReport comingReport) {

        if(comingReport != null)
            return this.comingReportHandler.addItem(comingReport).getEntityItem().getId();

        return 0L;
    }


    @GetMapping(path="/getReportById")
    public @ResponseBody
    ComingReport getReportById(@RequestParam Long id) {
        return this.comingReportHandler.getItemById(id);
    }


}
