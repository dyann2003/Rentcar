package Renter_Car.Specification;

import Renter_Car.Models.Car;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class FilterSpecification implements Specification<Car> {


    private final FilterCriteria criteria;

    public FilterSpecification(FilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            if(criteria.getOperation().equals(">")){
                return criteriaBuilder.
                        greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue());
            }else if(criteria.getOperation().equals("<")){
                return criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue());
            }else if(criteria.getOperation().equals(":")){
                return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            }else {
                return null;
            }
    }
}
