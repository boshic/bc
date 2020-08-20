package barcode.tools.filter;

import barcode.dao.entities.ComingItem;
import barcode.dao.entities.Supplier;
import barcode.utils.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class ComingItemSpecification implements Specification<ComingItem> {

    private SearchCriteria criteria;

    public ComingItemSpecification(SearchCriteria sc) {
        this.criteria = sc;
    }


//    public Specification<User> getSpecification(SpecificationField field, Object searchCriteria){
//        return new Specification<User>() {
//            @Override
//            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//                if(searchCriteria instanceof String){
//                    searchCriteria.toString().trim().toLowerCase();
//                }
//
//        switch(field){
//            case USER_GROUP:
//                Join<User, Group> groupJoin = userRoot.join("group");
//                return cb.equal(cb.lower(groupJoin.<String> get("name")), searchCriteria);
//            case USER_ROLE:
//                Join<User, Role> roleJoin = userRoot.join("role");
//                return cb.equal(cb.lower(roleJoin.<String> get("name")), searchCriteria);

    @Override
    public Predicate toPredicate
            (Root<ComingItem> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Join<ComingItem,Supplier> supplier = root.join("supplier");

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
