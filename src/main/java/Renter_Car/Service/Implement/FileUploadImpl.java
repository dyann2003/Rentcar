package Renter_Car.Service.Implement;

import Renter_Car.Service.FileUpload;
import Renter_Car.Util.FileUploadUtil;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadImpl implements FileUpload {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        // Sử dụng FileUploadUtil để xác thực file
        FileUploadUtil.assertAllowed(file); // Xác thực ảnh

        // Tải ảnh lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                Map.of("public_id", FileUploadUtil.getFileName(file.getName()),
                        "folder", folder)
        );

        // Trả về URL của ảnh đã tải lên
        return uploadResult.get("url").toString();
    }
}
