package Renter_Car.Service.Implement;

import Renter_Car.DTO.RegistrationDto;
import Renter_Car.Models.Role;
import Renter_Car.Models.User;
import Renter_Car.Repository.RoleRepository;
import Renter_Car.Repository.UserRepository;
import Renter_Car.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(RegistrationDto registrationDto, String role) {
        User account = new User();
        account.setFullName(registrationDto.getFullName());
        account.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        account.setEmail(registrationDto.getEmail());
        account.setPhone(registrationDto.getPhone());
        account.setIsDelete(false);

        Role myRole = roleRepository.findByRoleName(role);
        Set<Role> roles = new HashSet<>();
        roles.add(myRole);
        account.setRoles(roles);
        account.setIsDelete(false);
        account.setWallet(0.0);

        userRepository.save(account);
    }

    @Override
    public void saveUsers(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Kiểm tra nếu user có ID thì cập nhật, nếu không thì tạo mới
        Integer userId = user.getId(); // Giả sử getId() trả về Long

        if (userId != null) {
            // Nếu user đã tồn tại (có ID), thực hiện cập nhật
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Cập nhật các thuộc tính cần thiết
            existingUser.setFullName(user.getFullName());
            existingUser.setDob(user.getDob());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setDrivingLicense(user.getDrivingLicense());
            existingUser.setNationalId(user.getNationalId());
//            existingUser.setWallet(user.getWallet());
//            existingUser.setIsDelete(user.getIsDelete());

            userRepository.save(existingUser); // Lưu lại thông tin đã cập nhật
        } else {
            // Nếu user chưa có ID (mới), thực hiện lưu mới
            userRepository.save(user);
        }
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    @Override
    public User findByEmail(String email) {
        return userRepository.findAccountByEmail(email);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findAccountByPhone(phone);
    }

    @Override
    public User findByResetPasswordToken(String token) {
        return userRepository.findAccountByResetPasswordToken(token);
    }

    @Override
    public void updateUser(User user) {
        // Check if the user exists in the database
        Optional<User> existingUserOpt = userRepository.findById(user.getId());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Update the user fields with new values
            existingUser.setFullName(user.getFullName());
            existingUser.setDob(user.getDob());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setDrivingLicense(user.getDrivingLicense());
            existingUser.setNationalId(user.getNationalId());
            existingUser.setWallet(user.getWallet());
            existingUser.setUpdateAt(new Date(System.currentTimeMillis()));  // Update timestamp
            existingUser.setIsDelete(user.getIsDelete());
            existingUser.setResetPasswordToken(user.getResetPasswordToken());
            existingUser.setTokenExpiryDate(user.getTokenExpiryDate());
            existingUser.setIsDelete(user.getIsDelete());
            existingUser.setWallet(user.getWallet());

            // If roles are being updated, set them as well
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                existingUser.setRoles(user.getRoles());
            }

            // Save the updated user object
            userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
    }


    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public User findByFullName(String fullName) {
       return userRepository.findByFullName(fullName);
    }
    @Transactional
    @Override
    public void updatePassword(String fullName, String currentPassword, String newPassword) {
        User user = userRepository.findByFullName(fullName);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the current password");
        }

        // Check if the new password is at least 6 characters long
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        // Check if the new password ends with a special character
        if (!newPassword.matches(".*[!@#$%^&*(),.?\":{}|<>]$")) {
            throw new IllegalArgumentException("New password must end with a special character");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        try {
            userRepository.save(user);
        } catch (Exception e) {
            // Log the exception for troubleshooting
            System.err.println("Error updating password: " + e.getMessage());
            throw new RuntimeException("Could not commit JPA transaction");
        }
    }


    @Override
    public boolean existsByUsername(String fullName) {
        return userRepository.findByFullName(fullName) != null; // So sánh với null thay vì isPresent()
    }


}
