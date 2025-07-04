package Renter_Car.Service;

import Renter_Car.Models.Model;

import java.util.List;

public interface ModelService {
    List<Model> getAllModelsByBrandId(int brandId);
}
