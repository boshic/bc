package barcode.controllers;


import barcode.dao.entities.embeddable.Comment;
import barcode.dao.entities.SoldItem;
import barcode.dao.services.SoldItemHandler;
import barcode.dao.utils.SoldItemFilter;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path="/")
public class SoldItemController {

    private SoldItemHandler soldItemHandler;

    public SoldItemController(SoldItemHandler soldItemHandler) {
        this.soldItemHandler = soldItemHandler;
    }

    @RequestMapping(value="/addSellingsSet", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addSelling(@RequestBody List<SoldItem> sellings) {
        return this.soldItemHandler.addSellings(sellings);
    }

    @RequestMapping(value="/addOneSelling", method = RequestMethod.POST)
    public @ResponseBody
    ResponseItem addOneSelling(@RequestBody SoldItem soldItem) {

        return this.soldItemHandler.addOneSelling(soldItem);
    }

    @RequestMapping(value = "/findSoldItemByFilter", method = RequestMethod.POST)
    public @ResponseBody ResponseItem<SoldItem> findSoldItemByFilter(@RequestBody SoldItemFilter soldItemFilter) {
        return this.soldItemHandler.findByFilter(soldItemFilter);
    }

    @RequestMapping(value = "/addAutoSellings", method = RequestMethod.POST)
    public @ResponseBody ResponseItem addAutoSellings(@RequestBody List<SoldItem> soldItems) {
        return this.soldItemHandler.makeAutoSelling(soldItems);
    }

    @RequestMapping(value = "/returnSoldItem", method = RequestMethod.POST)
    public @ResponseBody ResponseItem returnSoldItem(@RequestBody SoldItem soldItem) {
        return this.soldItemHandler.returnSoldItem(soldItem);
    }

    @RequestMapping(value = "/changeSoldItem", method = RequestMethod.POST)
    public @ResponseBody ResponseItem changeSoldItem(@RequestBody SoldItem soldItem) {
        return this.soldItemHandler.changeSoldItem(soldItem);
    }

    @RequestMapping(value = "/changeSoldItemDate", method = RequestMethod.POST)
    public @ResponseBody ResponseItem changeSoldItemDate(@RequestBody SoldItem soldItem) {
        return this.soldItemHandler.changeDate(soldItem);
    }

    @RequestMapping(value = "/addSoldItemComment", method = RequestMethod.POST)
    public @ResponseBody String addSoldItemComment(@RequestBody Comment comment, @RequestParam Long id) {
        this.soldItemHandler.addComment(comment, id);
        return null;
    }

    @GetMapping(path="/getSoldItemById")
    public @ResponseBody SoldItem getComingById(@RequestParam Long id) {
        return soldItemHandler.getItemById(id);
    }


//    changeSoldItemDate

//    @RequestMapping(value = "/tstsi", method = RequestMethod.POST)
//    public @ResponseBody Iterable<SoldItem> getItemById(@RequestBody SearchCriteria searchCriteria) {
//        SoldItemPredicatesBuilder soldItemPredicatesBuilder = new SoldItemPredicatesBuilder()
//                .with(searchCriteria.getKey(), searchCriteria.getOperation(),searchCriteria.getValue());
//        return soldItemHandler.getByBuilder(soldItemPredicatesBuilder);
//    }

//    MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder()
//            .with("lastName", ":", "Doe").with("age", ">", "25");
//
//    Iterable<MyUser> results = repo.findAll(builder.build());
//
//    assertThat(results, contains(userTom));
//    assertThat(results, not(contains(userJohn)));

    //end of Selling API
}
