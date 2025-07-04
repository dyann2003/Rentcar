package Renter_Car.Util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class FileUploadUtil {

    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final String IMAGE_PATTERN = "^(?!\\s*$)(?=.*[\\w\\s._-])[\\w\\s._-]{1,255}\\.(?i)(jpg|png|gif|bmp)$";
    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String TIME_FORMAT = "HHmmss";
    public static final String FILE_NAME_FORMAT = "%s_%s_%s";

    public static boolean isAllowedExtension(final String fileName, final String pattern) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file) {
        // Kiểm tra nếu file null
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Tệp không được để trống.");
        }

        final long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kích thước tệp tối đa là 10MB.");
        }

        final String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tệp không được để trống.");
        }

        // Kiểm tra tên tệp theo quy tắc đặt tên của Windows (bỏ qua các ký tự không hợp lệ)
        String[] invalidChars = {"<", ">", ":", "\"", "/", "\\", "|", "?", "*"};
        for (String invalidChar : invalidChars) {
            if (fileName.contains(invalidChar)) {
                throw new IllegalArgumentException("Tên tệp không được chứa ký tự không hợp lệ: " + invalidChar);
            }
        }

        if (!isAllowedExtension(fileName, IMAGE_PATTERN)) {
            throw new IllegalArgumentException("Chỉ hỗ trợ các tệp jpg, png, gif hoặc bmp.");
        }
    }


    public static String getFileName(final String name) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

        String date = dateFormatter.format(Instant.now().atZone(ZoneId.systemDefault()));
        String time = timeFormatter.format(Instant.now().atZone(ZoneId.systemDefault()));

        return String.format(FILE_NAME_FORMAT, name, date, time);
    }
}
