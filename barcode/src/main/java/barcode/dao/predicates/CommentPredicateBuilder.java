package barcode.dao.predicates;

import com.querydsl.core.types.Predicate;

/**
 * Created by xlinux on 04.06.19.
 */
public interface CommentPredicateBuilder {

    Predicate build(String comment);

}
