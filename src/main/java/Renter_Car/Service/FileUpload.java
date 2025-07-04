package Renter_Car.Service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    String uploadFile(MultipartFile file, String folder) throws Exception;
}

