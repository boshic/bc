package barcode.dao.predicates;

import barcode.dao.entities.QItem;
import com.querydsl.core.types.Predicate;

/**
 * Created by xlinux on 14.08.19.
 */
public class ItemPredicateBuilder {

    private QItem item = QItem.item;

    private PredicateBuilder predicateBuilder = new PredicateBuilder();

    public Predicate buildByFilter(String filter) {

        if(filter != null)
            return predicateBuilder
                    .buildByPhraseAndMethod(filter, item.name::containsIgnoreCase);

        return null;
    }

    public Predicate buildByFilterForComponentsInput(String filter) {

        if(filter != null)
            return item.eanSynonym.eq("").and(item.components.size().eq(0))
                .and(buildByFilter(filter));

        return null;
    }

    public Predicate getPredicateForCompositeItemsByFilter(String filter) {

        if(filter != null)
            return item.components.size().gt(0).and(buildByFilter(filter));

        return null;
    }

    public Predicate getPredicateForCompositeItemByEan(String filter) {

        if(filter != null)
            return item.ean.eq(filter).and(item.components.size().gt(0));

        return null;
    }

//    public Predicate getInventoryRowByStock(Long itemId, Long stockId) {
//
//        return item.id.eq(itemId).and(item.inventoryRows.any().stock.id.eq(stockId));
////        return null;
//    }

}
