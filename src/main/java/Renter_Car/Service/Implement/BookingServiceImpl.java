package Renter_Car.Service.Implement;

import Renter_Car.Models.Booking;
import Renter_Car.Models.Car;
import Renter_Car.Models.CommonValue;
import Renter_Car.Repository.BookingRepository;
import Renter_Car.Service.BookingService;
import Renter_Car.Specification.BookingSpecification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void saveBooking(Booking booking) {
        booking.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        booking.setBookingDate(new Timestamp(System.currentTimeMillis()));
        bookingRepository.save(booking);
    }

    @Override
    public void updateBooking(Booking booking) {
        booking.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public Optional<Booking> getBookingById(int bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public int countBookingByCarId(int carId) {
        return bookingRepository.countByCarId(carId);
    }

    @Override
    public int countBookingByUserIdAndStatusNotIn(int userId, Collection<String> status, boolean isCustomer) {
        // Tạo Specification
        Specification<Booking> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != 0 && !isCustomer) {
                predicates.add(criteriaBuilder.equal(root.get("car").get("user").get("id"), userId));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.not(root.get("status").in(status)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return (int) bookingRepository.count(specification);
    }

    @Override
    public Map<Integer, Integer> countBookingsByCarIds(List<Integer> carIds) {
        Map<Integer, Integer> bookingCountMap = new HashMap<>();
        List<Object[]> bookingCounts = bookingRepository.countBookingsByCarIds(carIds);

        for (Object[] result : bookingCounts) {
            Integer carId = (Integer) result[0];
            Integer count = ((Long) result[1]).intValue();
            bookingCountMap.put(carId, count);
        }

        return bookingCountMap;
    }

    @Override
    public Page<Booking> getListRentedByUser(int userId,
                                             int size,
                                             int page,
                                             boolean isCustomer,
                                             String sortBy,
                                             String sortOrder,
                                             String status) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);

        Specification<Booking> specification = BookingSpecification.findByCondition(status, userId, isCustomer);
        return bookingRepository.findAll(specification, pageable);
    }

//    @Override
//    public List<Booking> findExpiredBookings(LocalDateTime now) {
//        LocalDateTime yesterday = now.minusDays(1);
//        return bookingRepository.findExpiredBookings(yesterday);
//    }

    @Override
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> findByCarIdAndEndDateAfter(int carId, Timestamp endDate) {
        return bookingRepository.findByCarIdAndEndDateAfter(carId, endDate);
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }
}
