package barcode.dao.predicates;

import barcode.utils.ComingItemFilter;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import java.util.function.Function;

/**
 * Created by xlinux on 13.06.19.
 */
//
class PredicateBuilder {

//    @Override
    Predicate buildByPhraseAndMethod(String phrase, Function<String, Predicate> comparingMethod)  {

        BooleanBuilder builder = new BooleanBuilder();

        for (String word : phrase.split(" "))
            builder = builder.and(comparingMethod.apply(word));

        return builder;
    }

//    @Override
    <T extends ComingItemFilter>
    Predicate buildCommentPredicate(T filter, Function<String, Predicate> comparingMethod) {

        if(filter.getStrictCommentSearch())
            return comparingMethod.apply(filter.getComment());
        else
            return buildByPhraseAndMethod(filter.getComment(), comparingMethod);

    }

}
