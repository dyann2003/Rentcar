//package Renter_Car.Component;
//
//import Renter_Car.Models.Booking;
//import Renter_Car.Service.BookingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//public class BookingScheduler {
//    private final BookingService bookingService;
//
//    @Autowired
//    public BookingScheduler(BookingService bookingService) {
//        this.bookingService = bookingService;
//    }
//
//    // Chạy mỗi giờ để kiểm tra đơn chưa nhận (giờ đây là ví dụ)
//    @Scheduled(cron = "0 0 * * * ?") // Cron chạy mỗi đầu giờ
//    public void cancelExpiredBookings() {
//        List<Booking> expiredBookings = bookingService.findExpiredBookings(LocalDateTime.now());
//        for (Booking booking : expiredBookings) {
//            booking.setStatus("7");
//            bookingService.save(booking);
//        }
//    }
//}
