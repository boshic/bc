package barcode.dao.predicates;

import barcode.dao.utils.SoldItemFilter;
import com.querydsl.core.types.Predicate;

import java.util.function.Function;

/**
 * Created by xlinux on 13.06.19.
 */
public interface PredicateBuilder {

    Predicate buildByPhraseAndMethod(String phrase, Function<String, Predicate> function);
//    Predicate build(SoldItemFilter filter);
}
