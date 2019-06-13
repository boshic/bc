package barcode.dao.predicates;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import java.util.function.Function;

/**
 * Created by xlinux on 13.06.19.
 */
public class PredicateBuilderImpl implements PredicateBuilder {

    @Override
    public Predicate buildByPhraseAndMethod(String phrase, Function<String, Predicate> comparingMethod)  {

        BooleanBuilder builder = new BooleanBuilder();

        for (String word : phrase.split(" "))
            builder = builder.and(comparingMethod.apply(word));

        return builder;
    }

}
