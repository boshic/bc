package barcode.dao.predicates;

import barcode.dao.entities.QBuyer;
import com.querydsl.core.types.Predicate;

/**
 * Created by xlinux on 09.12.19.
 */
public class BuyerPredicateBuilder {

    private QBuyer buyer = QBuyer.buyer;

    private PredicateBuilder predicateBuilder = new PredicateBuilderImpl();

    public Predicate buildByFilter(String filter) {

        if(filter != null)
            return predicateBuilder
                    .buildByPhraseAndMethod(filter, buyer.name::containsIgnoreCase);
//
        return null;
    }


}
