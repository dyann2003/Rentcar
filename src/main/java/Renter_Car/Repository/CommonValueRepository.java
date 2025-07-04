package Renter_Car.Repository;

import Renter_Car.Models.Car;
import Renter_Car.Models.CommonValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonValueRepository extends JpaRepository<CommonValue, Integer> {
    List<CommonValue> findByName(String name);
    CommonValue findCommonValueByNameAndKey(String name, String key);

}
