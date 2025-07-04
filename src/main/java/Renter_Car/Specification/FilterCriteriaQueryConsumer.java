package Renter_Car.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterCriteriaQueryConsumer implements Consumer<FilterCriteria> {

    private Root root;
    private CriteriaBuilder criteriaBuilder;
    private Predicate predicate;


    @Override
    public void accept(FilterCriteria filterCriteria) {
        if(filterCriteria.getOperation().equals(">")) {
        predicate=    criteriaBuilder.and(predicate,
                    criteriaBuilder.greaterThanOrEqualTo(root.get(filterCriteria.getKey()),
                                                           filterCriteria.getValue()));
        }else if(filterCriteria.getOperation().equals("<")) {
         predicate=   criteriaBuilder.and(predicate,
                    criteriaBuilder.lessThanOrEqualTo(root.get(filterCriteria.getKey()),
                            filterCriteria.getValue()));
        }else if(filterCriteria.getOperation().equals(":")) {
        predicate=    criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get(filterCriteria.getKey()),
                            filterCriteria.getValue()));
        }else if(filterCriteria.getOperation().equals(";")) {
            predicate= criteriaBuilder.or(predicate,
                    criteriaBuilder.equal(root.get(filterCriteria.getKey()),
                            filterCriteria.getValue()));
        }
    }
}
