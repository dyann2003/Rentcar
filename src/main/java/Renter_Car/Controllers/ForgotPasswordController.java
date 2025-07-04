package Renter_Car.Controllers;

import Renter_Car.Models.User;
import Renter_Car.Service.EmailService;
import Renter_Car.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Controller
public class ForgotPasswordController {
    java.util.Date utilDate = new java.util.Date();
    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("email", "");
        return "Public/forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        User account = userService.findByEmail(email);

        if (account == null) {
            model.addAttribute("error", "No account associated with this email.");
            return "Public/forgotPassword";
        }

        String token = UUID.randomUUID().toString();
        account.setResetPasswordToken(token);
        // Convert util.Date to sql.Date for token expiry
        account.setTokenExpiryDate(new Timestamp(System.currentTimeMillis() + 3600000)); // 1 hour = 3600000 milliseconds
        userService.updateUser(account);

        String resetPasswordLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendEmail(email, "Reset Password", "Click the link to reset your password: " + resetPasswordLink);

        model.addAttribute("message", "We have sent a reset password link to your email.");
        return "Public/forgotPassword";
    }


    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        User account = userService.findByResetPasswordToken(token);

        // Debugging output
        System.out.println("Token received: " + token);
        System.out.println("Account retrieved: " + account);

        if (account != null) {
            System.out.println("Token expiry date: " + account.getTokenExpiryDate());
            System.out.println("Current time: " + new java.sql.Timestamp(System.currentTimeMillis()));
        }

        if (account == null || account.getTokenExpiryDate().before(new java.sql.Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "Public/resetPassword";
        }

        model.addAttribute("token", token);
        return "Public/resetPassword";
    }


    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
    ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "Public/resetPassword";
        }

        User account = userService.findByResetPasswordToken(token);

        if (account == null || account.getTokenExpiryDate().before(new Date())) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "Public/resetPassword";
        }

        account.setPassword(userService.encodePassword(password));
        account.setResetPasswordToken(null);
        account.setTokenExpiryDate(null);

        userService.updateUser(account);

        return "redirect:/login?resetSuccess";
    }
}
