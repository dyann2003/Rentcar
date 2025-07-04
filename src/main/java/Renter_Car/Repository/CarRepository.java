package Renter_Car.Repository;

import Renter_Car.Models.Car;
import Renter_Car.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {
    List<Car> findByUser(User user);

    List<Car> findByAddressContaining(String address);
}
