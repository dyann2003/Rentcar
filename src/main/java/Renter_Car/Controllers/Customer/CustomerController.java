package Renter_Car.Controllers.Customer;

import Renter_Car.Constrant.IConstants;
import Renter_Car.Models.*;
import Renter_Car.Service.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final PaginationService paginationService;
    private final BookingService bookingService;
    private final CommonValueService commonValueService;
    private final UserService userService;

    public CustomerController(PaginationService paginationService, BookingService bookingService, CommonValueService commonValueService, UserService userService) {
        this.paginationService = paginationService;
        this.bookingService = bookingService;
        this.commonValueService = commonValueService;
        this.userService = userService;
    }

    @GetMapping("/list-booking-car")
    public String showListRentedCar(Model model,
                                    @AuthenticationPrincipal AuthUser authUser,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "3") int size,
                                    @RequestParam(name = "sortBy", defaultValue = "updateDate") String sortBy,
                                    @RequestParam(name = "sortOrder", defaultValue = "DESC") String sortOrder,
                                    @RequestParam(name = "statusBooking", defaultValue = "1") String statusOrder) {

        boolean isCustomer = userService.findById(authUser.getId()).getRoles().stream()
                .anyMatch(role -> "ROLE_CUSTOMER".equals(role.getRoleName()));

        Page<Booking> listRentedBooking = bookingService.getListRentedByUser(authUser.getId(),
                size,
                page,
                isCustomer,
                sortBy,
                sortOrder,
                statusOrder
        );

        // If the user enters a page number in the url is greater than the calculated page number
        if (!listRentedBooking.hasContent() && listRentedBooking.getTotalElements() != 0) {
            page = 0;
            listRentedBooking = bookingService.getListRentedByUser(authUser.getId(),
                    size,
                    page,
                    isCustomer,
                    sortBy,
                    sortOrder,
                    statusOrder
            );
        }


        int totalPages = listRentedBooking.getTotalPages();
        List<Integer> dataDisplayPages = (totalPages > IConstants.LIMIT_PAGE_DISPLAY)
                ? paginationService.getComplexPage(totalPages, page)
                : paginationService.getSimplePage(totalPages);


        int totalBookingOnGoing = bookingService.countBookingByUserIdAndStatusNotIn(
                authUser.getId(),
                List.of("6", "7"),
                isCustomer
        );

        Map<Integer, CommonValue> listStatusBooking = new HashMap<>();

        for (Booking booking : listRentedBooking) {
            listStatusBooking.put(booking.getId(), commonValueService.getCommonValueByNameAndKey(IConstants.BOOKING_STATUS, booking.getStatus()));
        }

        pagingData(sortOrder, sortBy, statusOrder, model);

        model.addAttribute("listStatusBooking", listStatusBooking);
        model.addAttribute("totalBookingOnGoing", totalBookingOnGoing);
        model.addAttribute("displayPages", dataDisplayPages);
        model.addAttribute("resultPage", listRentedBooking);

        return "page/customer/bookingList";
    }


    private void pagingData(String sortOrder, String sortBy, String status, Model model) {
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("status", status);
    }

}
