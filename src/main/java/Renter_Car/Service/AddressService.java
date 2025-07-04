package Renter_Car.Service;
import java.util.List;

public interface AddressService {
    List<String> readAddressesFromExcel(String fileName);
    List<String> getCityFromExcel(String filePath);
    List<String> getDistrictFromExcel(String filePath, String city);
    List<String> readWardFromExcel(String filePath, String district);

}
