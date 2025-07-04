package Renter_Car.Service;

import Renter_Car.Models.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingService {
//    Page<Booking> findByUserId(int userId, Pageable pageable);
    List<Booking> findByUserId(int userId);
    void saveBooking(Booking booking);
    void updateBooking(Booking booking);
    List<Booking> findByCarIdAndEndDateAfter(int carId, Timestamp endDate);
    List<Booking> getBookingsByUserId(int userId);
    Optional<Booking> getBookingById(int bookingId);
    int countBookingByCarId(int carId);
    int countBookingByUserIdAndStatusNotIn(int userId, Collection<String> status, boolean isCustomer);
    Map<Integer, Integer> countBookingsByCarIds(List<Integer> carIds);
    Page<Booking> getListRentedByUser(int userId, int size, int page, boolean isCustomer, String sortBy, String sortOrder, String status);

//    List<Booking> findExpiredBookings(LocalDateTime now);

    void save(Booking booking);
}
