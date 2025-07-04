package Renter_Car.Controllers;

import Renter_Car.Constrant.IConstants;
import Renter_Car.DTO.RegistrationDto;
import Renter_Car.Models.AuthUser;
import Renter_Car.Models.Transaction;
import Renter_Car.Models.User;
import Renter_Car.Repository.TransactionRepository;
import Renter_Car.Service.TransactionService;
import Renter_Car.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"", "/"})
    public String index() {
        return "Public/homepage";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "Public/login";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        RegistrationDto account = new RegistrationDto();
        model.addAttribute("registrationDto", account);
        return "Public/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto, BindingResult result) {

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.addError(
                    new FieldError("registrationDto", "confirmPassword", "Password and Confirm Password do not match")
            );
        }

        if (!registrationDto.getPassword().matches(IConstants.PASSWORD_REGEX) && !registrationDto.getPassword().isEmpty() && !result.hasFieldErrors("confirmPassword")) {
            result.addError(
                    new FieldError("registrationDto", "password", "Password must contain at least one letter, one number, and be at least 7 characters long")
            );
        }

        User accountEmailExist = userService.findByEmail(registrationDto.getEmail());

        if (accountEmailExist != null) {
            result.addError(
                    new FieldError("registrationDto", "email"
                            , "Email address is already used")
            );
        }

        User accountPhoneExist = userService.findByPhone(registrationDto.getPhone());

        if (accountPhoneExist != null) {
            result.addError(
                    new FieldError("registrationDto", "phone", "Phone number is already used")
            );
        }

        if (!registrationDto.getPhone().matches(IConstants.MOBILE_REGEX) && !registrationDto.getPhone().isEmpty()) {
            result.addError(
                    new FieldError("registrationDto", "phone", "Phone number is not valid")
            );
        }


        if (result.hasErrors()) {
            return "Public/register";
        }

        userService.saveUser(registrationDto, registrationDto.getRole());

        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal AuthUser authUser, Model model) {

        User user = userService.findById(authUser.getId());

        if (user == null) {
            return "redirect:/"; // Chuyển hướng về trang chính nếu không tìm thấy người dùng
        }

        model.addAttribute("user", user); // Chuyển thông tin người dùng vào mô hình
        return "Public/profile"; // Trả về view cho trang thông tin cá nhân
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(@AuthenticationPrincipal AuthUser authUser, Model model) {

        User user = userService.findById(authUser.getId());

        if (user == null) {
            // Nếu không tìm thấy người dùng, có thể chuyển hướng hoặc thông báo lỗi
            return "redirect:/"; // Hoặc thông báo lỗi
        }

        // Chuyển thông tin người dùng vào mô hình
        model.addAttribute("user", user);
        return "Public/edit-profile"; // Trả về view cho biểu mẫu chỉnh sửa
    }


    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("user") User user,
                                BindingResult result,
                                @RequestParam("drivingLicenseImages") List<MultipartFile> drivingLicenseImages,
                                RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi xác thực
        if (result.hasErrors()) {
            return "Public/edit-profile"; // Trở về trang chỉnh sửa nếu có lỗi
        }

        // Đường dẫn để lưu ảnh
        String uploadDir = "src/main/resources/static/images/drivingLicenses/";
        List<String> drivingLicensePaths = new ArrayList<>();

        // Xử lý từng ảnh giấy phép lái xe
        for (MultipartFile drivingLicenseImage : drivingLicenseImages) {
            if (!drivingLicenseImage.isEmpty()) {
                try {
                    // Tạo tên file duy nhất và lưu ảnh
                    String filename = System.currentTimeMillis() + "_" + drivingLicenseImage.getOriginalFilename();
                    Path path = Paths.get(uploadDir + filename);
                    Files.copy(drivingLicenseImage.getInputStream(), path);
                    drivingLicensePaths.add("/images/drivingLicenses/" + filename); // Thêm đường dẫn vào danh sách
                } catch (IOException e) {
                    // Xử lý ngoại lệ nếu có lỗi xảy ra
                    redirectAttributes.addFlashAttribute("error", "Lỗi khi tải lên tệp: " + e.getMessage());
                    return "redirect:/profile/edit"; // Quay lại trang chỉnh sửa
                }
            }
        }

        // Nếu không có ảnh mới, lấy lại các đường dẫn ảnh hiện tại từ cơ sở dữ liệu
        if (drivingLicensePaths.isEmpty()) {
            List<String> existingImagePaths = userService.findById(user.getId()).getDrivingLicense();
            user.setDrivingLicense(existingImagePaths); // Thiết lập lại đường dẫn ảnh hiện có
        } else {
            user.setDrivingLicense(drivingLicensePaths); // Thiết lập lại đường dẫn ảnh mới
        }

        // Lưu thông tin người dùng
        userService.saveUsers(user);

        // Thêm thông báo thành công và chuyển hướng
        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
        return "redirect:/profile"; // Chuyển hướng đến trang hồ sơ
    }

    @GetMapping("/mywallet")
    public String myWalletPage(@AuthenticationPrincipal AuthUser authUser, Model model) {

        User user = userService.findById(authUser.getId());

        if (user == null) {
            return "redirect:/login"; // Chuyển hướng về trang chính nếu không tìm thấy người dùng
        }

        // Lấy tất cả giao dịch của người dùng với user
        List<Transaction> transactions = transactionService.findTransactionByUser(user);
        double walletAmount = user.getWallet();  // Lấy số dư ví
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedWallet = formatter.format(walletAmount) + " VND";
        model.addAttribute("transactions", transactions);
        model.addAttribute("userWallet", formattedWallet);
        model.addAttribute("user", user); // Chuyển thông tin người dùng vào mô hình
        return "Public/mywallet"; // Trả về view cho trang thông tin cá nhân
    }

    @PostMapping("/mywallet")
    public String searchTransactionByDate(@AuthenticationPrincipal AuthUser authUser,
                                          @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dateFrom,
                                          @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dateTo,
                                          Model model) {

        User user = userService.findById(authUser.getId());

        if (user == null) {
            return "redirect:/login"; // Chuyển hướng về trang chính nếu không tìm thấy người dùng
        }

        // Lấy tất cả giao dịch của người dùng với user
        LocalDateTime startDateTime = (dateFrom != null) ? dateFrom.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime endDateTime = (dateTo != null) ? dateTo.atTime(LocalTime.MAX) : LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        if (dateFrom == null && dateTo == null) {
            transactions = transactionService.findTransactionByUser(user);
        } else {
            transactions = transactionService.findTransactionByDate(startDateTime, endDateTime, user);
        }

        double walletAmount = user.getWallet();  // Lấy số dư ví
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedWallet = formatter.format(walletAmount) + " VND";
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("transactions", transactions);
        model.addAttribute("userWallet", formattedWallet);
        model.addAttribute("user", user); // Chuyển thông tin người dùng vào mô hình
        return "Public/mywallet"; // Trả về view cho trang thông tin cá nhân
    }


    private String saveImage(MultipartFile carImage, String uploadDir) throws IOException {
        // Get the original filename
        String originalFilename = carImage.getOriginalFilename();

        // Validate the filename
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Invalid file name");
        }

        // Create a file name with a timestamp to avoid conflicts
        String fileName = System.currentTimeMillis() + "_" + originalFilename;

        // Define the path to the resources/static/img folder
        Path resourceDirectory = Paths.get(uploadDir);
        Path filePath = resourceDirectory.resolve(fileName);

        // Check if the directory exists (optional, as the folder already exists)
        if (!Files.exists(resourceDirectory)) {
            Files.createDirectories(resourceDirectory);  // Will not run if folder already exists
        }

        // Copy the file to the directory
        Files.copy(carImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path that can be accessed by the front end
        return "drivingLicenses/" + fileName;
    }
}
