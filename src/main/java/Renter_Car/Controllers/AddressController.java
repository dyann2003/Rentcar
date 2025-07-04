package Renter_Car.Controllers;

import Renter_Car.Service.AddressService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/addresses")
    @ResponseBody
    public List<String> suggestAddresses(String input) {
        String filePath = "src/main/resources/static/list of values/Address value list.xls";
        List<String> allAddresses = addressService.readAddressesFromExcel(filePath);

        List<String> suggestions = allAddresses.stream()
                .filter(address -> address.toLowerCase().contains(input.toLowerCase()))
                .toList();

        return suggestions;
    }

    @GetMapping("/addresses/city")
    @ResponseBody
    public List<String> suggestCity() {
        String filePath = "src/main/resources/static/list of values/Address value list.xls";
        List<String> allAddresses = addressService.getCityFromExcel(filePath);

        List<String> suggestions = allAddresses.stream()
                .toList();

        return suggestions;
    }

    @GetMapping("/addresses/district")
    @ResponseBody
    public List<String> suggestDistrict(@RequestParam String city) {
        String filePath = "src/main/resources/static/list of values/Address value list.xls";
        List<String> allAddresses = addressService.getDistrictFromExcel(filePath, city);
        return allAddresses;
    }

    @GetMapping("/addresses/ward")
    @ResponseBody
    public List<String> suggestWard(@RequestParam String district) {
        String filePath = "src/main/resources/static/list of values/Address value list.xls";
        List<String> allAddresses = addressService.readWardFromExcel(filePath, district);

        return allAddresses;
    }

}
