package barcode.dao.predicates;

import barcode.dao.entities.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

/**
 * Created by xlinux on 14.08.19.
 */
public class ItemPredicateBuilder {

    private QItem item = QItem.item;

    private PredicateBuilder predicateBuilder = new PredicateBuilderImpl();

    public Predicate buildByFilter(String filter) {

//        BooleanExpression predicate = null;

        if(filter != null)
            return predicateBuilder
                    .buildByPhraseAndMethod(filter, item.name::containsIgnoreCase);

        return null;
    }

}
