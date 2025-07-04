package Renter_Car.Service.Implement;

import Renter_Car.Service.AddressService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook; // Import cho XLS
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AddressServiceImpl implements AddressService {

    public List<String> readAddressesFromExcel(String filePath) {
        List<String> addresses = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = filePath.endsWith(".xls") ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên
            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false; // Bỏ qua hàng đầu tiên
                    continue;
                }
                Cell communeCell = row.getCell(1); // Cột xã
                Cell districtCell = row.getCell(3); // Cột huyện
                Cell cityCell = row.getCell(5); // Cột thành phố

                String commune = communeCell != null ? communeCell.getStringCellValue() : "";
                String district = districtCell != null ? districtCell.getStringCellValue() : "";
                String city = cityCell != null ? cityCell.getStringCellValue() : "";

                String address = String.format("%s, %s, %s", commune, district, city);
                addresses.add(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public List<String> getCityFromExcel(String filePath) {
        Set<String> addresses = new LinkedHashSet<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = filePath.endsWith(".xls") ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên
            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false; // Bỏ qua hàng đầu tiên
                    continue;
                }
                Cell cityCell = row.getCell(5); // Cột thành phố

                String city = cityCell != null ? cityCell.getStringCellValue().trim() : ""; // Trimming spaces
                if (!city.isEmpty()) { // Kiểm tra nếu city không rỗng
                    addresses.add(city); // Thêm vào Set
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Chuyển Set về List để trả về
        return new ArrayList<>(addresses);
    }

    public List<String> getDistrictFromExcel(String filePath, String cityName) {
        Set<String> districts = new LinkedHashSet<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = filePath.endsWith(".xls") ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên
            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false; // Bỏ qua hàng đầu tiên
                    continue;
                }

                Cell districtCell = row.getCell(3); // Cột huyện
                Cell cityCell = row.getCell(5); // Cột thành phố

                String district = districtCell != null ? districtCell.getStringCellValue() : "";
                String city = cityCell != null ? cityCell.getStringCellValue() : "";

                // Chỉ thêm vào nếu thành phố khớp
                if (city.equalsIgnoreCase(cityName)) {
                    districts.add(district);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(districts); // Chuyển Set về List
    }

    public List<String> readWardFromExcel(String filePath, String districts) {
        Set<String> ward = new LinkedHashSet<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = filePath.endsWith(".xls") ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên
            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false; // Bỏ qua hàng đầu tiên
                    continue;
                }

                Cell wardCell = row.getCell(1);

                Cell districtCell = row.getCell(3); // Cột huyện

                String district = districtCell != null ? districtCell.getStringCellValue() : "";
                String wards = wardCell != null ? wardCell.getStringCellValue() : "";

                // Chỉ thêm vào nếu thành phố khớp
                if (district.equalsIgnoreCase(districts)) {
                    ward.add(wards);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(ward); // Chuyển Set về List
    }

}
