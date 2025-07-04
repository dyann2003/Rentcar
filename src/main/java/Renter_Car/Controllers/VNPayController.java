package Renter_Car.Controllers;

import Renter_Car.Models.AuthUser;
import Renter_Car.Models.Transaction;
import Renter_Car.Models.User;
import Renter_Car.Service.TransactionService;
import Renter_Car.Service.UserService;
import Renter_Car.Service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Controller
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;


    @GetMapping("/payment")
    public String home(Model model, @AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            return "redirect:/login";
        } else {
            User user = userService.findById(authUser.getId());
            String sanitizedFullName = removeVietnameseAccents(user.getFullName());
            model.addAttribute("sanitizedFullName", sanitizedFullName);
        }
        return "Public/payment";
    }

    public String removeVietnameseAccents(String str) {
        if (str == null) return null;
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") String amountStr,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request) {
        // Loại bỏ "VND" và dấu phẩy
        String sanitizedAmount = amountStr.replace(" VND", "").replace(".", "");

        int orderTotal = Integer.parseInt(sanitizedAmount);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public String GetMapping(HttpServletRequest request,
                             @AuthenticationPrincipal AuthUser authUser,
                             Model model) {
        if (authUser == null) {
            return "redirect:/login";
        }
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String amount = request.getParameter("vnp_Amount");

        Double totalPrice = Double.parseDouble(amount) / 100;
        model.addAttribute("orderInfo", orderInfo);
        Locale vietnamLocale = new Locale("vi", "VN");
        NumberFormat formatter = NumberFormat.getInstance(vietnamLocale);
        String formattedTotalPrice = formatter.format(totalPrice) + " VND";
        model.addAttribute("formattedTotalPrice", formattedTotalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        if (paymentStatus == 1) {
            User user = userService.findById(authUser.getId());
            if (user != null) {
                if(user.getWallet() > 0) {
                    user.setWallet(user.getWallet() + totalPrice);
                } else {
                    user.setWallet(totalPrice);
                }
                userService.updateUser(user);

                Transaction transaction = new Transaction(transactionId, user, totalPrice, "Top up", LocalDateTime.now());
                transactionService.saveTransaction(transaction);
            }
            return "Public/ordersuccess";
        } else {
            return "Public/orderfail";
        }
    }
}
