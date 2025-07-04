package Renter_Car.Service;

import Renter_Car.Models.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CarService {
    Optional<Car> getCar(int id);

    Car findById(int carId);

    void updateCar(int id, Car car);

    void deleteCar(int carId);

    void saveCar(Car car); // Add this method

    Page<Car> findCars(List<String> carModel,
                       List<String> type,
                       String brand,
                       String electricCar,
                       String traditionalCar,
                       String rateFiveStar,
                       String delivery,
                       int page, int size,
                       String address,
                       String time,
                       int userId);

    List<Car> findCars(String address);
}
