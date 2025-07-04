package Renter_Car.Service.Implement;

import Renter_Car.Models.Car;
import Renter_Car.Repository.CarRepository;
import Renter_Car.Service.CarService;
import Renter_Car.Specification.CarSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Car findById(int carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isPresent()) {
            return optionalCar.get();
        } else {
            throw new RuntimeException("Car not found with id: " + carId);
        }
    }

    @Override
    public void saveCar(Car car) {
        carRepository.save(car);
    }

    @Override
    public void updateCar(int id, Car car) {
        // Find the existing car by ID
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));

        existingCar.setBrand(car.getBrand());
        existingCar.setModel(car.getModel());
        existingCar.setVIN(car.getVIN());
        existingCar.setColor(car.getColor());
        existingCar.setNumberOfSeats(car.getNumberOfSeats());
        existingCar.setProductionYear(car.getProductionYear());
        existingCar.setTransmissionType(car.getTransmissionType());
        existingCar.setFuelType(car.getFuelType());
        existingCar.setMileage(car.getMileage());
        existingCar.setFuelConsumption(car.getFuelConsumption());
        existingCar.setPricePerDay(car.getPricePerDay());
        existingCar.setDeposit(car.getDeposit());
        existingCar.setAddress(car.getAddress());
        existingCar.setAdditional_functions(car.getAdditional_functions());
        existingCar.setDescription(car.getDescription());
        existingCar.setCarType(car.getCarType());

        if (car.getImagePaths() != null) {
            existingCar.setImagePaths(car.getImagePaths());  // Update with new images
        }

        // Save the updated car
        carRepository.saveAndFlush(existingCar);
    }


    @Override
    public void deleteCar(int carId) {
        Optional<Car> carOptional = getCar(carId);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setStatus(Car.STATUS_INACTIVE);
            carRepository.save(car);
        }
    }

    @Override
    public Page<Car> findCars(List<String> carModel,
                              List<String> type,
                              String brand,
                              String electricCar,
                              String traditionalCar,
                              String rateFiveStar,
                              String delivery,
                              int page, int size,
                              String address,
                              String time,
                              int userId) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Car> specification = CarSpecification.findByCondition(carModel, type, brand, electricCar, traditionalCar, rateFiveStar, delivery, address, time, userId);
        return carRepository.findAll(specification, pageable);
    }

    @Override
    public List<Car> findCars(String address) {
        return carRepository.findByAddressContaining(address);
    }

    @Override
    public Optional<Car> getCar(int id) {
        return carRepository.findById(id);
    }
}
