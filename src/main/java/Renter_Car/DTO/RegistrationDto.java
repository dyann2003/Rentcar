package Renter_Car.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegistrationDto {
    private int id;
    @NotEmpty(message = "Full name is not empty")
    private String fullName;
    @NotEmpty(message = "Password is not empty")
    private String password;
    @NotEmpty(message = "Email is not empty")
    private String email;
    @NotEmpty(message = "Phone number is not empty")
    private String phone;
    @NotEmpty(message = "Confirm password is not empty")
    private String confirmPassword;
    @NotEmpty(message = "You have to select role")
    private String role;
}
