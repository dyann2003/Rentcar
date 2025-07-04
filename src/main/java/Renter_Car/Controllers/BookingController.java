package Renter_Car.Controllers;

import Renter_Car.Constrant.IConstants;
import Renter_Car.Models.*;
import Renter_Car.Service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import Renter_Car.Constrant.IConstants;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Controller
@RequestMapping("booking")
public class BookingController {

    private final UserService userService;
    private final CarService carService;
    private final BookingService bookingService;
    private final CommonValueService commonValueService;
    private final FileUpload fileUpload;
    private final ReportService reportService;
    private final EmailService emailService;
    private final TransactionService transactionService;


    @Autowired
    public BookingController(UserService userService, CarService carService,
                             BookingService bookingService, CommonValueService commonValueService,
                             FileUpload fileUpload, EmailService emailService,
                             TransactionService transactionService,  ReportService reportService) {
        this.userService = userService;
        this.carService = carService;
        this.bookingService = bookingService;
        this.commonValueService = commonValueService;
        this.fileUpload = fileUpload;
        this.reportService = reportService;
        this.emailService = emailService;
        this.transactionService = transactionService;
    }

    @GetMapping("")
    public String booking(@AuthenticationPrincipal AuthUser authUser,
                          Model model,
                          @RequestParam int id,
                          @RequestParam String time) {
        Booking booking = new Booking();

        User user = userService.findById(authUser.getId());
        Car car = carService.getCar(id).get();

        booking.setUser(user);
        booking.setDriverName(user.getFullName());
        booking.setDriverDob(user.getDob());
        booking.setDriverAddress(user.getAddress());
        booking.setDriverEmail(user.getEmail());
        booking.setDriverPhone(user.getPhone());
        List<String> licenses = user.getDrivingLicense();
        booking.setDriverLiense(licenses != null ? String.join(",", licenses) : "");
        booking.setNationID(user.getNationalId());
        booking.setCar(car);

        // Tách chuỗi time thành thời gian bắt đầu và kết thúc
        String[] myTime = time.split(" - ");

        // Định dạng thời gian theo kiểu HH:mm, dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");

        // Chuyển đổi chuỗi thời gian thành LocalDateTime
        LocalDateTime pickupTime = LocalDateTime.parse(myTime[0], formatter);
        LocalDateTime returnTime = LocalDateTime.parse(myTime[1], formatter);

        // Tính số giờ chênh lệch giữa thời gian nhận và trả xe
        long hoursRented = ChronoUnit.HOURS.between(pickupTime, returnTime);

        // Chuyển đổi số giờ thành ngày và làm tròn lên nếu có dư giờ
        int daysRented = (int) (hoursRented / 24);

        int remainingHours = (int) (hoursRented % 24);

        double pricePerDay = car.getPricePerDay();

        double totalAmount = daysRented * pricePerDay + car.getDeposit();

        if (remainingHours > 0) {
            totalAmount += remainingHours < 12 ? (pricePerDay / 2) : pricePerDay;
        }

        List<Booking> bookings = bookingService.findByCarIdAndEndDateAfter(id, Timestamp.valueOf(LocalDateTime.now()));

        List<Map<String, String>> rentedRanges = bookings.stream().map(b -> Map.of("startDate", b.getStartDate().toLocalDateTime().format(formatter), "endDate", b.getEndDate().toLocalDateTime().format(formatter))).collect(Collectors.toList());

        CommonValue carStatus = commonValueService.getCommonValueByNameAndKey(IConstants.CAR_STATUS, String.valueOf(car.getStatus()));

        List<CommonValue> paymentMethod = commonValueService.getListCommonValueByName(IConstants.PAYMENT_METHOD);

        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("carStatus", carStatus);
        model.addAttribute("rentedRanges", rentedRanges);
        model.addAttribute("time", time);
        model.addAttribute("pickup", myTime[0]);
        model.addAttribute("return", myTime[1]);
        model.addAttribute("myCar", car);
        model.addAttribute("user", user);
        model.addAttribute("booking", booking);
        model.addAttribute("totalAmount", totalAmount);  // Thêm tổng tiền vào model

        return "page/customer/booking";
    }

    @GetMapping("/update_status_booking")
    public String updateStatusBooking(@AuthenticationPrincipal AuthUser authUser,
                                      @RequestParam(name = "statusBooking") String statusBooking,
                                      @RequestParam(name = "bookingId") int bookingId,
                                      @RequestParam(name = "reason" ,required = false) String reason,
                                      @RequestParam(name = "cancel", defaultValue = "false") boolean cancelBooking,
                                      Model model,
                                      @RequestParam(name="comment" , required = false) String comment,
                                      @RequestParam(name="mark",required = false)Integer mark,
                                      @RequestParam(name="carId",required = false) Integer carId) {
        if(mark!=null && carId!=null && comment!=null) {
            Car car = carService.findById(carId);
            Booking booking = bookingService.getBookingById(bookingId).get();
            User user = userService.findById(authUser.getId());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedNow = now.format(formatter);
            Report report = new Report(formattedNow,mark,comment,car,user,booking);
            reportService.insertReport(report);
        }

        Booking booking = bookingService.getBookingById(bookingId).get();
        User user = userService.findById(authUser.getId());
        Car car = booking.getCar();
        boolean isCustomer = userService.findById(authUser.getId()).getRoles().stream()
                .anyMatch(role -> "ROLE_CUSTOMER".equals(role.getRoleName()));

        if (booking.getStatus().equals("7")) {
            return redirectWithError("Có lỗi xảy ra, đơn hàng đã bị hủy", isCustomer);
        } else if ((!booking.getStatus().equals("1") && !booking.getStatus().equals("2")) && cancelBooking) {
            return redirectWithError("Có lỗi xảy ra, đơn hàng đã bị hủy", isCustomer);
        }

        if (cancelBooking) {
            Timestamp startDate = booking.getStartDate();
            Instant startInstant = startDate.toInstant();
            Instant nowInstant = Instant.now(); // Thời gian hiện tại

            long diffInHours = Duration.between(startInstant, nowInstant).toHours();

            if (diffInHours < 6) {
                redirectWithError("Trong vòng 6 tiếng trở lại từ thời điểm lấy xe, bạn không thể hủy thuê xe", isCustomer);
            }

            booking.setStatus("7");
            String carName = car.getBrand().getBrandName() + " " + car.getModel().getName();
            if (isCustomer) {
                // Gửi email cho khách hàng khi hủy
                emailService.sendCancellationConfirmation(user.getEmail(),
                        user.getFullName(), carName, reason);

                // Gửi email cho chủ xe khi khách hàng hủy
                emailService.sendCancellationConfirmationToOwner(car.getUser().getEmail(),
                        carName, user.getFullName(), reason);
            } else {
                // Gửi email cho cả chủ xe và khách hàng khi chủ xe hủy
                emailService.sendCancellationConfirmationByOwner(booking.getDriverEmail(), user.getEmail(),
                        carName, booking.getUser().getFullName(), user.getFullName(), reason);
            }
        } else if (booking.getPaymentMethod().equals("1") && booking.getStatus().equals("3")) {
            booking.setStatus("6");
        } else {
            int status = Integer.parseInt(statusBooking) + 1;
            booking.setStatus("" + status);
        }

        bookingService.updateBooking(booking);


        if (statusBooking.equals("3") || statusBooking.equals("4") || statusBooking.equals("5")) {
            statusBooking = "0";
        }

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("statusBooking", statusBooking);
        if (isCustomer) {
            return "redirect:/customer/list-booking-car?statusBooking=" + statusBooking;
        } else {
            return "redirect:/car-owner/list-rented-car?statusBooking=" + statusBooking;
        }
    }

    private String redirectWithError(String message, boolean isCustomer) {
        try {
            String encodedMess = URLEncoder.encode(message, "UTF-8");
            if (isCustomer) {
                return "redirect:/customer/list-booking-car?erro=" + encodedMess;
            } else {
                return "redirect:/car-owner/list-rented-car?erro=" + encodedMess;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "redirect:/error"; // Hoặc xử lý theo cách khác
        }
    }


    @GetMapping("/details")
    public String viewBookingDetails(@RequestParam(name = "id") int id, Model model,
                                     @RequestParam(required = false) String address,
                                     @RequestParam(required = false) String time) {
        Optional<Booking> bookingOptional = bookingService.getBookingById(id);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            Car car = booking.getCar();
            User carOwner = car.getUser(); // Lấy thông tin chủ xe

            // Lấy danh sách Additional Functions
            String[] keyAddFunc = car.getAdditional_functions().split(", ");
            List<CommonValue> additionalFunction = new ArrayList<>();
            for (String key : keyAddFunc) {
                additionalFunction.add(commonValueService.getCommonValueByNameAndKey(IConstants.ADDITIONAL_FUNCTION, key));
            }

            // Đếm số lần xe được thuê
            int count = bookingService.countBookingByCarId(car.getId());
            CommonValue commonValue = commonValueService.getCommonValueByNameAndKey(IConstants.BOOKING_STATUS,booking.getStatus());
            model.addAttribute("bookingStatus",commonValue);
            // Xử lý thời gian nhận xe và trả xe
            if (time != null && !time.isEmpty()) {
                String[] myTime = time.split(" - ");
                model.addAttribute("time", time);
                model.addAttribute("pickup", myTime[0]);
                model.addAttribute("return", myTime[1]);
            }

            // Xử lý địa chỉ nhận xe
            if (address != null && !address.isEmpty()) {
                model.addAttribute("address", address);
            }
            Map<Integer, CommonValue> listStatusBooking = new HashMap<>();
            listStatusBooking.put(booking.getId(), commonValueService.getCommonValueByNameAndKey(IConstants.BOOKING_STATUS, booking.getStatus()));
            // Thêm thông tin vào Model
            model.addAttribute("listStatusBooking", listStatusBooking);
            model.addAttribute("booking", booking);               // Thông tin đặt xe
            model.addAttribute("car", car);                       // Thông tin xe
            model.addAttribute("user", booking.getUser());         // Người thuê xe
            model.addAttribute("carOwner", carOwner);             // Chủ xe
            model.addAttribute("additionalFunctions", additionalFunction); // Các chức năng bổ sung
            model.addAttribute("count", count);                   // Số lần xe được thuê

            return "page/customer/bookingDetails";
        } else {
            return "error/404";
        }
    }


    @PostMapping("/confirm-pickup")
    public String confirmPickup(@RequestParam("bookingId") int bookingId, Model model) {
        Optional<Booking> bookingOptional = bookingService.getBookingById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            // Update the booking status to "In Process"
            booking.setStatus("3");
            bookingService.save(booking); // Save the updated booking

            // Add the updated booking to the model
            model.addAttribute("booking", booking);
            model.addAttribute("car", booking.getCar());
            model.addAttribute("user", booking.getUser()); // Assuming user is the person who made the booking
            model.addAttribute("carOwner", booking.getCar().getUser()); // Assuming car owner is in the car entity

            // Add a success message
            model.addAttribute("successMessage", "Pickup confirmed successfully.");

            // Return the booking details page with the updated booking information
            return "page/customer/bookingDetails";
        } else {
            return "error/404"; // Handle case where booking is not found
        }
    }

    @PostMapping("/cancel-booking")
    public String cancelBooking(@RequestParam("bookingId") int bookingId, RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOptional = bookingService.getBookingById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            // Update the booking status to "cancelled"
            booking.setStatus("cancelled");
            bookingService.save(booking); // Save the updated booking
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully.");
            return "redirect:/details?id=" + bookingId; // Redirect back to the booking details page
        } else {
            return "error/404";
        }
    }


    @PostMapping("")
    public String checkout(Model model, @AuthenticationPrincipal AuthUser authUser,
                           @ModelAttribute Booking booking,
                           @RequestParam int carId,
                           @RequestParam String pickupDate,
                           @RequestParam String returnDate,
                           @RequestParam String addressDelivery,
                           @RequestParam String totalAmount,
                           @RequestParam String isDelivery,
                           @RequestParam(name = "paymentMethod") String paymentMethod,
                           @RequestParam(name = "driverLicense") MultipartFile driverLicense,
                           @RequestParam(name = "takeNote") String takeNote,
                           BindingResult result) {
        User user = userService.findById(authUser.getId());
        Car car = carService.getCar(carId).get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");

        LocalDateTime startDate = LocalDateTime.parse(pickupDate, formatter);
        LocalDateTime endDate = LocalDateTime.parse(returnDate, formatter);

        // Validate thông tin người thuê
        if (booking.getDriverName() == null || booking.getDriverName().trim().isEmpty()) {
            result.addError(new FieldError("booking", "driverName", "Tên người thuê không được để trống"));
        }

        if (booking.getDriverDob() == null || booking.getDriverDob().trim().isEmpty()) {
            result.addError(new FieldError("booking", "driverDob", "Ngày sinh không được để trống"));
        }

        if (booking.getDriverPhone() == null || booking.getDriverPhone().trim().isEmpty()) {
            result.addError(new FieldError("booking", "driverPhone", "Số điện thoại không được để trống"));
        }

        if (booking.getDriverEmail() == null || booking.getDriverEmail().trim().isEmpty()) {
            result.addError(new FieldError("booking", "driverEmail", "Email không được để trống"));
        }

        if (booking.getNationID() == null || booking.getNationID().trim().isEmpty()) {
            result.addError(new FieldError("booking", "nationID", "Số định danh công dân không được để trống"));
        }

        if (booking.getDriverAddress() == null || booking.getDriverAddress().trim().isEmpty()) {
            result.addError(new FieldError("booking", "driverAddress", "Địa chỉ không được để trống"));
        }

        if (result.hasErrors()) {
            return populateBookingModel(model, car, user, pickupDate, returnDate, addressDelivery, totalAmount, booking);
        }

        // Gắn thông tin đặt xe vào đối tượng Booking
        booking.setAddressPickup(addressDelivery);
        booking.setStartDate(Timestamp.valueOf(startDate));
        booking.setEndDate(Timestamp.valueOf(endDate));
        booking.setTotalAmount(Double.parseDouble(totalAmount));
        booking.setDelivery(Boolean.parseBoolean(isDelivery));
        booking.setUser(user);
        booking.setCar(car);
        booking.setPaymentMethod(paymentMethod);

        if (!takeNote.isEmpty()) {
            booking.setNotes(takeNote);
        }

        if (driverLicense.isEmpty()) {
            if (user.getDrivingLicense() == null || user.getDrivingLicense().isEmpty()) {
                result.addError(new ObjectError("driverLicense", "Giấy phép lái xe không được để trống!"));
                return populateBookingModel(model, car, user, pickupDate, returnDate, addressDelivery, totalAmount, booking);
            }
            booking.setDriverLiense(user.getDrivingLicense().get(0));
        } else {
            try {
                booking.setDriverLiense(fileUpload.uploadFile(driverLicense, IConstants.IMAGE_DRIVER_LICENSE));
            } catch (Exception e) {
                result.addError(new ObjectError("driverLicense", "Tải lên giấy phép lái xe thất bại: " + e.getMessage()));
                return populateBookingModel(model, car, user, pickupDate, returnDate, addressDelivery, totalAmount, booking);
            }
        }

        // Xử lý thanh toán
        if (paymentMethod.equals("1")) { // Thanh toán bằng ví
            synchronized (user) {
                if (user.getWallet() >= Double.parseDouble(totalAmount)) {
                    booking.setStatus("2");
                    user.setWallet(user.getWallet() - Double.parseDouble(totalAmount));
                    try {
                        String addressToUse = (addressDelivery != null) ? addressDelivery : user.getAddress();
                        // Gửi email cho khách hàng
                        emailService.sendBookingConfirmation(user.getEmail(), user.getFullName(),
                                car.getBrand().getBrandName() + " " + car.getModel().getName(),
                                pickupDate, returnDate, addressToUse,
                                Double.parseDouble(totalAmount), car.getDeposit());

                        // Gửi email cho chủ xe
                        emailService.sendBookingConfirmationToOwner(car.getUser().getEmail(),
                                car.getBrand().getBrandName() + " " + car.getModel().getName(),
                                user.getFullName(), pickupDate, returnDate, addressToUse,
                                Double.parseDouble(totalAmount), car.getDeposit());

                    } catch (Exception e) {
                        System.err.println("Gửi email thất bại: " + e.getMessage());
                    }

                    // Lưu giao dịch
                    int randomNum = ThreadLocalRandom.current().nextInt(10000000, 100000000);
                    String randomStr = String.valueOf(randomNum);
                    transactionService.saveTransaction(new Transaction(randomStr, user, Double.parseDouble(totalAmount), "Rent car", LocalDateTime.now()));
                } else {
                    model.addAttribute("error", "Số dư không đủ để thuê xe. Vui lòng nạp thêm tiền!");
                    return populateBookingModel(model, car, user, pickupDate, returnDate, addressDelivery, totalAmount, booking);
                }
            }
        } else { // Thanh toán bằng phương thức khác
            booking.setStatus("1");
        }

        // Lưu thông tin đặt xe
        bookingService.saveBooking(booking);

        // Điều hướng người dùng
        if (paymentMethod.equals("1")) {
            return "redirect:/customer/list-booking-car?statusBooking=2";
        } else {
            return "redirect:/customer/list-booking-car";
        }
    }

    @GetMapping("/getUnavailableDates/{carId}")
    @ResponseBody
    public List<Map<String, String>> getUnavailableDates(@PathVariable int carId) {
        List<Booking> bookings = bookingService.findByCarIdAndEndDateAfter(carId, new Timestamp(System.currentTimeMillis()));
        List<Map<String, String>> unavailableDates = new ArrayList<>();

        for (Booking booking : bookings) {
            // Chuyển đổi thời gian bắt đầu và kết thúc sang LocalDateTime
            LocalDateTime startDateTime = booking.getStartDate().toLocalDateTime();
            LocalDateTime endDateTime = booking.getEndDate().toLocalDateTime();

            // Loại bỏ các phần ngày không đầy đủ
            LocalDateTime adjustedStartDateTime = startDateTime.toLocalTime().equals(LocalTime.MIN)
                    ? startDateTime
                    : startDateTime.toLocalDate().plusDays(1).atStartOfDay();

            LocalDateTime adjustedEndDateTime = endDateTime.toLocalTime().equals(LocalTime.MAX)
                    ? endDateTime
                    : endDateTime.toLocalDate().minusDays(1).atTime(LocalTime.MAX);

            // Kiểm tra nếu khoảng thời gian sau khi điều chỉnh vẫn hợp lệ
            if (!adjustedStartDateTime.isAfter(adjustedEndDateTime)) {
                // Duyệt qua từng ngày trong khoảng thời gian điều chỉnh
                LocalDate current = adjustedStartDateTime.toLocalDate();
                LocalDate end = adjustedEndDateTime.toLocalDate();

                while (!current.isAfter(end)) {
                    Map<String, String> dateRange = new HashMap<>();
                    dateRange.put("start", current.toString() + " 00:00:00");
                    dateRange.put("end", current.toString() + " 23:59:59");
                    unavailableDates.add(dateRange);
                    current = current.plusDays(1); // Tiến tới ngày tiếp theo
                }
            }
        }
        return unavailableDates;
    }

    /**
     * Hàm tiện ích để load lại thông tin khi có lỗi
     */
    private String populateBookingModel(Model model, Car car, User user, String pickupDate, String returnDate, String addressDelivery, String totalAmount, Booking booking) {
        CommonValue carStatus = commonValueService.getCommonValueByNameAndKey(IConstants.CAR_STATUS, String.valueOf(car.getStatus()));
        List<CommonValue> payment = commonValueService.getListCommonValueByName(IConstants.PAYMENT_METHOD);

        model.addAttribute("paymentMethod", payment);
        model.addAttribute("carStatus", carStatus);
        model.addAttribute("time", pickupDate + " - " + returnDate);
        model.addAttribute("pickup", pickupDate);
        model.addAttribute("return", returnDate);
        model.addAttribute("address", addressDelivery);
        model.addAttribute("myCar", car);
        model.addAttribute("user", user);
        model.addAttribute("booking", booking);
        model.addAttribute("totalAmount", totalAmount);

        return "page/customer/booking";
    }

}
