package Renter_Car.Service;

import Renter_Car.Models.CommonValue;

import java.util.List;

public interface CommonValueService {
    List<CommonValue> getListCommonValueByName(String name);
    CommonValue getCommonValueByNameAndKey(String name, String key);
}
