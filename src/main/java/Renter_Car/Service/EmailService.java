package Renter_Car.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendBookingConfirmation(
            String toEmail,
            String fullName,
            String carName,
            String pickupTime,
            String dropoffTime,
            String pickupLocation,
            double totalRentalPrice,
            double totalDeposit) {

        // Định dạng tiền tệ Việt Nam
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);

        String subject = "Xác nhận đặt xe thành công";
        String message = "Chào " + fullName + ",\n\n"
                + "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. Bạn đã đặt xe thành công với thông tin sau:\n\n"
                + "-----------------------------------------\n"
                + "- Xe: " + carName + "\n"
                + "- Thời gian nhận xe: " + pickupTime + "\n"
                + "- Thời gian trả xe: " + dropoffTime + "\n"
                + "- Địa điểm nhận xe: " + pickupLocation + "\n"
                + "- Tổng tiền thuê: " + currencyFormatter.format(totalRentalPrice) + "\n"
                + "- Tổng tiền cọc: " + currencyFormatter.format(totalDeposit) + "\n"
                + "-----------------------------------------\n\n"
                + "Chúc bạn có một hành trình tốt đẹp!\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ";

        sendEmail(toEmail, subject, message);
    }


    public void sendCancellationConfirmation(
            String toEmail,
            String fullName,
            String carName,
            String reason) {

        // Định dạng thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String cancellationTime = LocalDateTime.now().format(formatter);

        // Tiêu đề email
        String subject = "Xác nhận hủy chuyến";

        // Nội dung email
        String message = "Chào " + fullName + ",\n\n"
                + "Bạn đã hủy chuyến xe thành công. Thông tin chi tiết như sau:\n\n"
                + "-----------------------------------------\n"
                + "- Xe: " + carName + "\n"
                + "- Thời gian hủy: " + cancellationTime + "\n"
                + (reason != null && !reason.isEmpty() ? "- Lý do hủy: " + reason + "\n" : "")
                + "-----------------------------------------\n\n"
                + "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ";

        // Gửi email
        sendEmail(toEmail, subject, message);
    }

    public void sendBookingConfirmationToOwner(
            String toEmailOwner,
            String carName,
            String customerName,
            String pickupTime,
            String dropoffTime,
            String pickupLocation,
            double totalRentalPrice,
            double totalDeposit) {

        // Định dạng tiền tệ Việt Nam
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);

        String subject = "Thông báo đặt xe mới";
        String message = "Chào chủ xe,\n\n"
                + "Một khách hàng đã đặt xe của bạn thành công với thông tin sau:\n\n"
                + "-----------------------------------------\n"
                + "- Xe: " + carName + "\n"
                + "- Tên khách hàng: " + customerName + "\n"
                + "- Thời gian nhận xe: " + pickupTime + "\n"
                + "- Thời gian trả xe: " + dropoffTime + "\n"
                + "- Địa điểm nhận xe: " + pickupLocation + "\n"
                + "- Tổng tiền thuê: " + currencyFormatter.format(totalRentalPrice) + "\n"
                + "- Tổng tiền cọc: " + currencyFormatter.format(totalDeposit) + "\n"
                + "-----------------------------------------\n\n"
                + "Vui lòng kiểm tra và chuẩn bị xe cho khách hàng.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ";

        sendEmail(toEmailOwner, subject, message);
    }

    public void sendCancellationConfirmationToOwner(
            String toEmailOwner,
            String carName,
            String customerName,
            String reason) {

        // Định dạng thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String cancellationTime = LocalDateTime.now().format(formatter);

        String subject = "Thông báo hủy chuyến";
        String message = "Chào chủ xe,\n\n"
                + "Khách hàng đã hủy chuyến xe của bạn. Thông tin chi tiết như sau:\n\n"
                + "-----------------------------------------\n"
                + "- Xe: " + carName + "\n"
                + "- Tên khách hàng: " + customerName + "\n"
                + "- Thời gian hủy: " + cancellationTime + "\n"
                + (reason != null && !reason.isEmpty() ? "- Lý do hủy: " + reason + "\n" : "")
                + "-----------------------------------------\n\n"
                + "Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ";

        sendEmail(toEmailOwner, subject, message);
    }

    public void sendCancellationConfirmationByOwner(
            String toEmailCustomer,
            String toEmailOwner,
            String carName,
            String customerName,
            String ownerName,
            String reason) {

        // Định dạng thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String cancellationTime = LocalDateTime.now().format(formatter);

        // Thông báo gửi đến khách hàng
        String subjectCustomer = "Thông báo hủy chuyến từ chủ xe";
        String messageCustomer = "Chào " + customerName + ",\n\n"
                + "Chuyến xe của bạn đã bị hủy bởi chủ xe. Thông tin chi tiết như sau:\n\n"
                + "-----------------------------------------\n"
                + "- Xe: " + carName + "\n"
                + "- Chủ xe: " + ownerName + "\n"
                + "- Thời gian hủy: " + cancellationTime + "\n"
                + (reason != null && !reason.isEmpty() ? "- Lý do hủy: " + reason + "\n" : "")
                + "-----------------------------------------\n\n"
                + "Chúng tôi rất tiếc về sự bất tiện này. Nếu cần hỗ trợ, vui lòng liên hệ với chúng tôi.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ";

        // Thông báo gửi đến chủ xe
        String subjectOwner = "Xác nhận hủy chuyến";
        String messageOwner = "Chào " + ownerName + ",\n\n"
                + "Bạn đã hủy chuyến xe. Thông tin chi tiết như sau:\n\n"
                + "-----------------------------------------\n"
                + "- Xe: " + carName + "\n"
                + "- Tên khách hàng: " + customerName + "\n"
                + "- Thời gian hủy: " + cancellationTime + "\n"
                + (reason != null && !reason.isEmpty() ? "- Lý do hủy: " + reason + "\n" : "")
                + "-----------------------------------------\n\n"
                + "Nếu bạn cần thêm thông tin, vui lòng liên hệ với chúng tôi.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ";

        // Gửi email cho khách hàng
        sendEmail(toEmailCustomer, subjectCustomer, messageCustomer);

        // Gửi email cho chủ xe
        sendEmail(toEmailOwner, subjectOwner, messageOwner);
    }


}
