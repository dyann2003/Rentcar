package Renter_Car.Specification;

import Renter_Car.Models.Booking;
import Renter_Car.Models.Car;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CarSpecification {
    public static Specification<Car> findByCondition(List<String> carModel,
                                                     List<String> type,
                                                     String brand,
                                                     String electricCar,
                                                     String traditionalCar,
                                                     String rateFiveStar,
                                                     String delivery,
                                                     String address,
                                                     String time,
                                                     int userId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (time != null && !time.isEmpty()) {
                String[] times = time.split(" - ");
                if (times.length == 2) {
                    // Tách startTime và endTime từ chuỗi
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");
                    LocalDateTime localStartTime = LocalDateTime.parse(times[0].trim(), formatter).minusHours(1); // Trừ 1 giờ
                    LocalDateTime localEndTime = LocalDateTime.parse(times[1].trim(), formatter).plusHours(1);   // Cộng 1 giờ

                    // Chuyển sang Timestamp
                    Timestamp startTime = Timestamp.valueOf(localStartTime);
                    Timestamp endTime = Timestamp.valueOf(localEndTime);

                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Booking> bookingSubRoot = subquery.from(Booking.class);
                    Join<Booking, Car> subCarJoin = bookingSubRoot.join("car");

                    subquery.select(subCarJoin.get("id"))
                            .where(
                                    criteriaBuilder.and(
                                            criteriaBuilder.lessThan(bookingSubRoot.get("startDate"), endTime),  // startDate nhỏ hơn endTime => vi phạm
                                            criteriaBuilder.greaterThan(bookingSubRoot.get("endDate"), startTime) // endDate lớn hơn startTime => vi phạm
                                    )
                            )
                            .groupBy(subCarJoin.get("id"));
                    predicates.add(criteriaBuilder.not(root.get("id").in(subquery)));
                }
            }



            if (electricCar != null && traditionalCar != null && electricCar.equals("true") && traditionalCar.equals("true")) {
                predicates.add(criteriaBuilder.like(root.get("fuelType"), "%%"));
            }

            if (traditionalCar == null && electricCar != null && electricCar.equals("true")) {
                predicates.add(criteriaBuilder.equal(root.get("fuelType"), "Điện"));
            }

            if (electricCar == null && traditionalCar != null && traditionalCar.equals("true")) {
                predicates.add(criteriaBuilder.notEqual(root.get("fuelType"), "Điện"));
            }

            if (rateFiveStar != null && rateFiveStar.equals("true")) {
                predicates.add(criteriaBuilder.equal(root.get("rating"), "5"));
            }

            if (delivery != null && delivery.equals("true")) {
                predicates.add(criteriaBuilder.equal(root.get("isDelivery"), true));
            }

            if (address != null && !address.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("address"), "%" + address + "%"));
            }

            if (carModel != null && !carModel.isEmpty()) {
                predicates.add(root.get("model").get("id").in(carModel));
            }

            if (type != null && !type.isEmpty()) {
                predicates.add(root.get("carType").in(type));
            }

            if (brand != null && !brand.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brand));
            }

            if (userId == 0) {
                // Only for customers searching for cars
                predicates.add(criteriaBuilder.equal(root.get("status"), Car.STATUS_ACTIVE));
            } else {
                // For car owners, include their cars regardless of status
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
