package Renter_Car.Controllers.Car_Owner;

import Renter_Car.Constrant.IConstants;
import Renter_Car.Models.*;
import Renter_Car.Repository.CarRepository;
import Renter_Car.Service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Fixing import here
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.sql.Timestamp;


import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/car-owner")
public class CarOwnerController {

    private final CarRepository carRepository;
    private final CarService carService;
    private final UserService userService;
    private final FileUpload fileUpload;
    private final BrandService brandService;
    private final ModelService modelService;
    private final ColorService colorService;
    private final CommonValueService commonValueService;
    private final ReportService reportService;
    private final PaginationService paginationService;
    private final BookingService bookingService;

    @Autowired
    public CarOwnerController(CarService carService, CarRepository carRepository, UserService userService, FileUpload fileUpload, BrandService brandService, ModelService modelService, ColorService colorService, CommonValueService commonValueService, ReportService reportService, PaginationService paginationService, BookingService bookingService) {
        this.carService = carService;
        this.carRepository = carRepository;
        this.userService = userService;
        this.fileUpload = fileUpload;
        this.brandService = brandService;
        this.modelService = modelService;
        this.colorService = colorService;
        this.commonValueService = commonValueService;
        this.reportService = reportService;
        this.paginationService = paginationService;
        this.bookingService = bookingService;
    }

    @GetMapping({"", "/"})
    public String homePage() {
        return "page/car-owner/homepage";
    }

    @GetMapping("/viewAllCar")
    public String showProductList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size,
                                  @AuthenticationPrincipal AuthUser authUser) {
        User user = userService.findById(authUser.getId());
        Page<Car> myCars = carService.findCars(new ArrayList<>(), new ArrayList<>(), "", "", "", "", "", page, size, "", "", user.getId());

        List<Integer> carIds = myCars.getContent().stream().map(Car::getId).collect(Collectors.toList());

        Map<Integer, Integer> listCountBooking = bookingService.countBookingsByCarIds(carIds);

        int totalPages = myCars.getTotalPages();
        List<Integer> dataDisplayPages = (totalPages > IConstants.LIMIT_PAGE_DISPLAY)
                ? paginationService.getComplexPage(totalPages, page)
                : paginationService.getSimplePage(totalPages);

        model.addAttribute("displayPages", dataDisplayPages);
        model.addAttribute("resultPage", myCars);
        model.addAttribute("listCount", listCountBooking);

        return "page/car-owner/viewAllCar";
    }

    @GetMapping("/post-car")
    public String showPagePostCar(Model model) {
        List<Brand> brands = brandService.getAllBrands();
        List<Color> colors = colorService.getColors();
        List<CommonValue> addFunc = commonValueService.getListCommonValueByName(IConstants.ADDITIONAL_FUNCTION);
        List<CommonValue> carType = commonValueService.getListCommonValueByName(IConstants.CAR_TYPE);

        model.addAttribute("carType", carType);
        model.addAttribute("addFunc", addFunc);
        model.addAttribute("colors", colors);
        model.addAttribute("brands", brands);
        return "page/car-owner/post-car";
    }

    @GetMapping("/get-model-by-brandId")
    @ResponseBody
    public List<Renter_Car.Models.Model> getModeByBrandId(@RequestParam int brandId) {
        List<Renter_Car.Models.Model> models = modelService.getAllModelsByBrandId(brandId);
        for (Renter_Car.Models.Model model : models) {
            model.setBrand(null);
            model.setCar(null);
        }
        return models;
    }

    @PostMapping("/stop-renting/{carId}")
    public String stopRentingCar(@PathVariable int carId,
                                 @AuthenticationPrincipal AuthUser authUser,
                                 @RequestParam(required = false) String address,
                                 @RequestParam(required = false) String time) {
        Car car = carService.findById(carId);

        // Check if the authenticated user is the owner of the car
        if (authUser.getId() != car.getUser().getId()) {
            return "redirect:/error";
        }

        // Check if car is not being rented and is available for rent
        Timestamp currentDate = new Timestamp(System.currentTimeMillis());
        List<Booking> currentBookings = bookingService.findByCarIdAndEndDateAfter(car.getId(), currentDate);

        if (!currentBookings.isEmpty()) {
            return "redirect:/error"; // Car is currently being rented
        }

        // Set car status to 'stopped renting'
        car.setStatus(Car.STATUS_STOPPED_RENTING);
        carService.saveCar(car);

        // Redirect back to the car detail page
        return "redirect:/cars/detail?id=" + carId +
                (address != null ? "&address=" + address : "") +
                (time != null ? "&time=" + time : "");
    }


    @PostMapping("/start-renting/{carId}")
    public String startRentingCar(@PathVariable int carId,
                                  @AuthenticationPrincipal AuthUser authUser,
                                  @RequestParam(required = false) String address,
                                  @RequestParam(required = false) String time) {
        Car car = carService.findById(carId);

        // Check if the authenticated user is the owner of the car
        if (authUser.getId() != car.getUser().getId()) {
            return "redirect:/error";
        }

        // Only allow starting to rent if the car is currently stopped from renting
        if (car.getStatus() != Car.STATUS_STOPPED_RENTING) {
            return "redirect:/error";
        }

        // Set car status to 'active'.
        car.setStatus(Car.STATUS_ACTIVE);
        carService.saveCar(car);

        // Redirect back to the car detail page
        return "redirect:/cars/detail?id=" + carId +
                (address != null ? "&address=" + address : "") +
                (time != null ? "&time=" + time : "");
    }

    @PostMapping("/post-car")
    public String postCar(@Valid @ModelAttribute("car") Car car,
                          @RequestParam("carImages") List<MultipartFile> carImages,
                          @RequestParam("additional_functions") List<Integer> additional_functions,
                          @RequestParam("carImagesMultiple") List<MultipartFile> carMoreImages,
                          @AuthenticationPrincipal AuthUser authUser) {

        if (authUser == null) {
            // Handle the error, e.g., redirect to an error page or return an error message
            return "redirect:/login"; // Example redirect, modify as needed
        }

        carImages.addAll(carMoreImages);

        List<String> imagePaths = new ArrayList<>();
        // Loop through all the uploaded images
        for (MultipartFile carImage : carImages) {
            if (!carImage.isEmpty()) { // Check if the image is not empty
                try {
                    String imageURL = fileUpload.uploadFile(carImage, IConstants.IMAGE_PRODUCT);
                    imagePaths.add(imageURL);
                } catch (Exception e) {
                    // You may want to log the exception
                    System.err.println("Image upload failed: " + e.getMessage());
                    // Optionally, you can return an error message to the user
                }
            }
        }

        User user = userService.findById(authUser.getId());

        // Ensure user is found before setting
        if (user != null) {
            // Set the list of image paths in the car entity
            car.setImagePaths(imagePaths);
            car.setUser(user);
            car.setStatus(Car.STATUS_ACTIVE);

            String addFunc = "";

            for (int i = 0; i < additional_functions.size(); i++) {
                addFunc += additional_functions.get(i);
                // Kiểm tra nếu phần tử không phải là phần tử cuối cùng
                if (i != additional_functions.size() - 1) {
                    addFunc += ", ";
                }
            }

            // Check for empty amenities before setting
            if (!addFunc.isEmpty()) {
                car.setAdditional_functions(addFunc);
            }

            // Save the car to the database
            carRepository.save(car);
        } else {
            // Handle the case where the user is not found
            return "redirect:/error";
        }
        return "redirect:/car-owner/viewAllCar";
    }

    @GetMapping("/list-rented-car")
    public String showListRentedCar(Model model,
                                    @AuthenticationPrincipal AuthUser authUser,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "3") int size,
                                    @RequestParam(name = "updateDate", defaultValue = "bookingDate") String sortBy,
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
                statusOrder);

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

        return "page/car-owner/list_rented";
    }

    private void pagingData(String sortOrder, String sortBy, String status, Model model) {
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("status", status);
    }

    // GET: Show car edit form with car details
    @GetMapping("/edit-car/{carId}")
    public String showEditCarPage(@PathVariable int carId, Model model) {
        Car car = carService.findById(carId);
        model.addAttribute("car", car);
        model.addAttribute("imagePaths", car.getImagePaths());
        return "page/car-owner/edit-car";
    }

    // POST: Process the form to update the car details
    @PostMapping("/edit-car/save")
    public String editCar(@RequestParam("carId") String carId,
                          @ModelAttribute Car car,
                          @RequestParam("carImages") List<MultipartFile> carImages,
                          @AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            return "redirect:/login";
        }

        int id = 0;
        if (carId != null) {
            try {
                id = Integer.parseInt(carId);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }

        List<String> imagePaths = new ArrayList<>();
        if (carImages != null) {
            for (MultipartFile carImage : carImages) {
                if (!carImage.isEmpty()) {
                    try {
                        String imageURL = fileUpload.uploadFile(carImage, IConstants.IMAGE_PRODUCT);
                        imagePaths.add(imageURL);
                    } catch (Exception e) {
                        System.err.println("Image upload failed: " + e.getMessage());
                        // Optionally, you might want to add an error message to be displayed to the user
                    }
                }
            }
        }

        User user = userService.findById(authUser.getId());

        if (user != null) {
            // Update the car's image paths
            if (!imagePaths.isEmpty()) {
                car.setImagePaths(imagePaths); // Update with new images
            } else {
                // Keep existing images if no new images were uploaded
                List<String> existingImagePaths = car.getImagePaths();
                if (existingImagePaths != null && !existingImagePaths.isEmpty()) {
                    car.setImagePaths(existingImagePaths);
                }
            }

            car.setUser(user);
            carService.updateCar(id, car);
        } else {
            return "redirect:/error"; // Handle errors gracefully
        }
        return "redirect:/car-owner/viewAllCar";
    }

    @GetMapping("/delete/{carId}")
    public String deleteCar(@PathVariable("carId") int carId) {
        try {
            carService.deleteCar(carId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/car-owner/viewAllCar";
    }

    @GetMapping("")
    public String carOwnerDashboard(Model model) {
        return "/page/car-owner/homepage";
    }

    @GetMapping("/report")
    public String viewReport(Model model, @AuthenticationPrincipal AuthUser authUser,
                             @RequestParam(name = "size", defaultValue = "7") int size,
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "mark", defaultValue = "1,2,3,4,5") String mark) {
        List<String> markList = Arrays.stream(mark.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        int id = authUser.getId();
        Page<Report> report = reportService.getAllReports(id, markList, page - 1, size);
        int totalPage = report.getTotalPages();
        List<Integer> listPage = (totalPage < IConstants.LIMIT_PAGE_DISPLAY)
                ? paginationService.getSimplePage(totalPage)
                : paginationService.getComplexPage(totalPage, page);

        int point1 = reportService.countMark(id, "1");
        int point2 = reportService.countMark(id, "2");
        int point3 = reportService.countMark(id, "3");
        int point4 = reportService.countMark(id, "4");
        int point5 = reportService.countMark(id, "5");
        double avg = reportService.getTotalMark(id) / (point1 + point2 + point3 + point4 + point5);
        model.addAttribute("point1", point1);
        model.addAttribute("point2", point2);
        model.addAttribute("point3", point3);
        model.addAttribute("point4", point4);
        model.addAttribute("point5", point5);
        model.addAttribute("mark", avg);
        model.addAttribute("reports", report);
        model.addAttribute("listPage", listPage);
        return "page/car-owner/viewreport";
    }

}
