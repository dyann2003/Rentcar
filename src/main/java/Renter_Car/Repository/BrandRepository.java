package Renter_Car.Repository;

import Renter_Car.Models.Booking;
import Renter_Car.Models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
