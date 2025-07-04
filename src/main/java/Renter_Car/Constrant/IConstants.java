package Renter_Car.Constrant;

public interface IConstants {
    String MOBILE_REGEX = "(\\+84|0)(3[2-9]|5[5689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}";
    String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    String IMAGE_USER = "Renter_Car/Avatar";
    String IMAGE_PRODUCT = "Renter_Car/Product";
    String IMAGE_FEEDBACK = "Renter_Car/Feedback";
    String IMAGE_DRIVER_LICENSE = "Renter_Car/Driver_License";
    String ADDITIONAL_FUNCTION = "ADDITIONAL_FUNCTION";
    String CAR_STATUS = "CAR_STATUS";
    String BOOKING_STATUS = "BOOKING_STATUS";
    String PAYMENT_METHOD = "PAYMENT_METHOD";
    String CAR_TYPE = "CAR_TYPE";
    Integer LIMIT_PAGE_DISPLAY = 5;
    Integer LIMIT_PAGE = 999999;
}
