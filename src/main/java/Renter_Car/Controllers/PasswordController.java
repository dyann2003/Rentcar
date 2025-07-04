package Renter_Car.Controllers;

import Renter_Car.Models.User;
import Renter_Car.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class PasswordController {
    @Autowired
    private UserService userService;

    // Handles GET requests to "/update-password" to render the password update form.
    @GetMapping("/update-password")
    public String updatePasswordForm() {
        return "Public/update-password";
    }

    // Handles POST requests to "/update-password" for updating user password securely.
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmNewPassword,
                                 Principal principal, Model model) {
        try {
            // Validates and updates the password for the authenticated user.
            if (!newPassword.equals(confirmNewPassword)) {
                model.addAttribute("error", "New passwords do not match");
                return "Public/update-password";
            }

            userService.updatePassword(principal.getName(), currentPassword, newPassword);
            model.addAttribute("message", "Password updated successfully");
        } catch (Exception e) {
            // Handles exceptions during password update process.
            model.addAttribute("error", e.getMessage());
        }
        return "Public/update-password";
    }

}
