package Renter_Car.Service;


import Renter_Car.DTO.RegistrationDto;
import Renter_Car.Models.User;
import jakarta.validation.Valid;

import java.util.Optional;

public interface UserService {
    void saveUser(RegistrationDto registrationDto, String role);
    void saveUsers(User user);
    User findById(int id);
    User findByEmail(String email);
    User findByPhone(String phone);
    User findByResetPasswordToken(String token);
    void updateUser(User user);
    String encodePassword(String password);
    User findByFullName(String fullName);
    void updatePassword(String username, String currentPassword, String newPassword);
    boolean existsByUsername(String fullName);
}
