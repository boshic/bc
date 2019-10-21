package barcode.dao.predicates;

import barcode.dao.entities.QItemSection;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by xlinux on 18.10.19.
 */
public class ItemSectionPredicateBuilder {

    private QItemSection qItemSection = QItemSection.itemSection;

    private PredicateBuilder predicateBuilder = new PredicateBuilderImpl();

    public Predicate buildByFilter(String filter) {

        if(filter != null)
            return predicateBuilder
                    .buildByPhraseAndMethod(filter, qItemSection.name::containsIgnoreCase);

        return null;
    }

    private Field getField(String fieldName, Object entity) {
        Field field = null;
        try {
            field = entity.getClass().getField(fieldName);
//            if (field.getType().equals(String.class) {
//                street = (String) field.get(entity);
//            }
        } catch (NoSuchFieldException ex) {
            System.out.print(ex.getMessage());
        }

        return field;
    }

    private Method getMethod(String methodName, Object entity) {
        Method method = null;
        try {
            method = entity.getClass().getMethod(methodName);
//            if (field.getType().equals(String.class) {
//                street = (String) field.get(entity);
//            }
        } catch (NoSuchMethodException ex) {
            System.out.print(ex.getMessage());
        }

        return method;
    }

    public <T extends EntityPathBase> void test(T value) {

        Class clazz = value.getClass();



        System.out.print(clazz.toString());

    };

}
