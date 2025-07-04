package Renter_Car.Service.Implement;

import Renter_Car.Models.Model;
import Renter_Car.Repository.ModelRepository;
import Renter_Car.Service.ModelService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;

    public ModelServiceImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public List<Model> getAllModelsByBrandId(int brandId) {
        return new ArrayList<>(modelRepository.findByBrand_id(brandId));
    }
}
