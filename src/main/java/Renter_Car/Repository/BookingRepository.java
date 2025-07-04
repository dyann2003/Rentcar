package Renter_Car.Repository;

import Renter_Car.Models.Booking;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByCarIdAndEndDateAfter(int carId, Timestamp currentDate);

    List<Booking> findByUserId(int userId);

    Page<Booking> findByUserId(int userId, Pageable pageable);

    int countByCarId(int carId);

    @Query("SELECT b.car.id, COUNT(b) FROM Booking b WHERE b.car.id IN :carIds GROUP BY b.car.id")
    List<Object[]> countBookingsByCarIds(List<Integer> carIds);

//    @Query("SELECT b FROM Booking b WHERE b.startDate < :yesterday AND (b.status = 1 OR b.status = 2)")
//    List<Booking> findExpiredBookings(LocalDateTime yesterday);
}
