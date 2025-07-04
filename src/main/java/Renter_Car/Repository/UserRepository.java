package Renter_Car.Repository;

import Renter_Car.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findAccountByEmail(String email);
    public User findAccountByFullName(String fullName);
    public User findAccountByPhone(String phone);
    User findAccountByResetPasswordToken(String resetPasswordToken);
    public User findByFullName(String fullName);
}
