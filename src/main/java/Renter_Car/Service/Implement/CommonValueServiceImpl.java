package Renter_Car.Service.Implement;

import Renter_Car.Models.CommonValue;
import Renter_Car.Repository.CommonValueRepository;
import Renter_Car.Service.CommonValueService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonValueServiceImpl implements CommonValueService {

    private final CommonValueRepository commonValueRepository;

    public CommonValueServiceImpl(CommonValueRepository commonValueRepository) {
        this.commonValueRepository = commonValueRepository;
    }

    @Override
    public List<CommonValue> getListCommonValueByName(String name) {
        return commonValueRepository.findByName(name);
    }

    @Override
    public CommonValue getCommonValueByNameAndKey(String name, String key) {
        return commonValueRepository.findCommonValueByNameAndKey(name, key);
    }

}
