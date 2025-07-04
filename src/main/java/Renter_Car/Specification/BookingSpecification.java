package Renter_Car.Specification;

import Renter_Car.Models.Booking;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookingSpecification {
    public static Specification<Booking> findByCondition(String status, int userId, boolean isCustomer) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty() && !status.equals("0")) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (status != null && status.equals("0")) {
                List<String> validStatuses = Arrays.asList("3", "4", "5");
                predicates.add(root.get("status").in(validStatuses));
            }

            if (userId != 0 && !isCustomer) {
                predicates.add(criteriaBuilder.equal(root.get("car").get("user").get("id"), userId));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
