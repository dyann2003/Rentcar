package Renter_Car.Repository;

import Renter_Car.Models.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Integer> {
    List<Model> findByBrand_id(int brandId);
}
