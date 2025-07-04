package Renter_Car.Controllers;


import Renter_Car.Constrant.IConstants;
import Renter_Car.Models.*;
import Renter_Car.Repository.CarRepository;
import Renter_Car.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;


import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;
    private final BookingService bookingService;
    private final PaginationService paginationService;
    private final CommonValueService commonValueService;
    private final BrandService brandService;
    private final ModelService modelService;
    private final ReportService reportService;

    @Autowired
    public CarController(CarService carService, CarRepository carRepository, BookingService bookingService, PaginationService paginationService, CommonValueService commonValueService, BrandService brandService, ModelService modelService, ReportService reportService) {
        this.carService = carService;
        this.bookingService = bookingService;
        this.paginationService = paginationService;
        this.commonValueService = commonValueService;
        this.brandService = brandService;
        this.modelService = modelService;
        this.reportService = reportService;
    }

    @GetMapping("/detail")
    public String showCarListDetail(Model model, @RequestParam int id, @RequestParam(required = false) String address, @RequestParam(required = false) String time, @AuthenticationPrincipal AuthUser authUser) {

        List<Report> reports = reportService.getReportByCarId(id);

        Car car = carService.findById(id);

        int count = bookingService.countBookingByCarId(car.getId());

        String[] keyAddFunc = car.getAdditional_functions().split(", ");

        List<CommonValue> additionalFunction = new ArrayList<>();

        for (int i = 0; i < keyAddFunc.length; i++) {
            additionalFunction.add(commonValueService.getCommonValueByNameAndKey(IConstants.ADDITIONAL_FUNCTION, keyAddFunc[i]));
        }

        if (time != null && !time.isEmpty()) {
            String[] myTime = time.split(" - ");
            model.addAttribute("time", time);
            model.addAttribute("pickup", myTime[0]);
            model.addAttribute("return", myTime[1]);
        }

        if (address != null && !address.isEmpty()) {
            model.addAttribute("address", address);
        }

        Timestamp currentDate = new Timestamp(System.currentTimeMillis());
        List<Booking> currentBookings = bookingService.findByCarIdAndEndDateAfter(car.getId(), currentDate);
        boolean isBeingRented = !currentBookings.isEmpty();

        boolean isCarOwner = authUser != null && authUser.getId() == car.getUser().getId();
        boolean canStopRenting = isCarOwner && !isBeingRented && car.getStatus() == Car.STATUS_ACTIVE;

        String carStatus;
        if (car.getStatus() == Car.STATUS_STOPPED_RENTING) {
            carStatus = "Not available for rent";
        } else if (isBeingRented) {
            carStatus = "Currently being rented";
        } else if (car.getStatus() == Car.STATUS_ACTIVE) {
            carStatus = "Available";
        } else {
            carStatus = "Unavailable";
        }

        model.addAttribute("isBeingRented", isBeingRented);
        model.addAttribute("canStopRenting", canStopRenting);
        model.addAttribute("isCarOwner", isCarOwner);
        model.addAttribute("carStatus", carStatus);
        model.addAttribute("reports", reports);

        model.addAttribute("additionalFunctions", additionalFunction);
        model.addAttribute("count", count);
        model.addAttribute("car", car);

        return "Public/car_detail";
    }

    @GetMapping("/search")
    public String searchCar(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size,
                            @RequestParam(name = "carType", required = false) List<String> type,
                            @RequestParam(name = "carModel", required = false) List<String> carModel,
                            @RequestParam(name = "carBrand", required = false) String brand,
                            @RequestParam(name = "electricCar", required = false) String electricCar,
                            @RequestParam(name = "traditionalCar", required = false) String traditionalCar,
                            @RequestParam(name = "rateFiveStar", required = false) String rateFiveStar,
                            @RequestParam(name = "delivery", required = false) String delivery,
                            @RequestParam String address,
                            @RequestParam String time) {

        if (time != null && !time.isEmpty()) {
            model.addAttribute("time", time);
        }

        String location = "";
        if (address != null && !address.isEmpty()) {
            model.addAttribute("address", address);
            location = getLocation(address);
        }

        page = (page <= 0) ? 0 : page;
        Page<Car> myCars = carService.findCars(carModel, type, brand, electricCar, traditionalCar, rateFiveStar, delivery, page, size, location, time, 0);

        // If the user enters a page number in the url is greater than the calculated page number
        if (!myCars.hasContent() && myCars.getTotalElements() != 0) {
            page = 0;
            myCars = carService.findCars(carModel, type, brand, electricCar, traditionalCar, rateFiveStar, delivery, page, size, location, time, 0);
        }

        List<Integer> carIds = myCars.getContent().stream().map(Car::getId).collect(Collectors.toList());
        Map<Integer, Integer> listCountBooking = bookingService.countBookingsByCarIds(carIds);


        int totalPages = myCars.getTotalPages();
        List<Integer> dataDisplayPages = (totalPages > IConstants.LIMIT_PAGE_DISPLAY) ? paginationService.getComplexPage(totalPages, page) : paginationService.getSimplePage(totalPages);

        initData(model);
        pagingData(type, brand, electricCar, traditionalCar, rateFiveStar, delivery, model);

        if (brand != null && !brand.isEmpty()) {
            List<Renter_Car.Models.Model> models = modelService.getAllModelsByBrandId(Integer.parseInt(brand));
            model.addAttribute("models", models);
            model.addAttribute("carModel", carModel);
        }

        model.addAttribute("displayPages", dataDisplayPages);
        model.addAttribute("resultPage", myCars);
        model.addAttribute("listCount", listCountBooking);

        return "page/customer/search_car";
    }

    private static String getLocation(String address) {
        String[] myAddress = address.split(", ");
        String location = "";

        if (myAddress.length == 3) {
            // Kiểm tra nếu có "Việt Nam"
            if (address.contains("Việt Nam")) {
                // Thực hiện loại bỏ các từ không cần thiết cho địa chỉ
                location = myAddress[1].replace("Thành phố", "").replace("Tỉnh", "").trim();
            } else {
                location = myAddress[2].replace("Thành phố", "").replace("Tỉnh", "").trim();
            }
        } else {
            location = myAddress[myAddress.length - 2].trim();
        }
        return location;
    }

    private void pagingData(List<String> type,
                            String brand,
                            String electricCar,
                            String traditionalCar,
                            String rateFiveStar,
                            String deliver,
                            Model model) {
        model.addAttribute("types", type);
        model.addAttribute("brands", brand);
        model.addAttribute("electricCar", electricCar);
        model.addAttribute("traditionalCar", traditionalCar);
        model.addAttribute("rateFiveStar", rateFiveStar);
        model.addAttribute("deliver", deliver);
    }

    private void initData(Model model) {
        List<CommonValue> carType = commonValueService.getListCommonValueByName(IConstants.CAR_TYPE);
        List<Brand> carBrand = brandService.getAllBrands();

        model.addAttribute("carBrand", carBrand);
        model.addAttribute("carType", carType);
    }
}
