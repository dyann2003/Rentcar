package Renter_Car.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;  // Xe thuê

    private Timestamp startDate;

    private Timestamp endDate;

    private String addressPickup;

    private double totalAmount;

    private Timestamp bookingDate;

    private boolean isDelivery;

    private String status;

    private String notes;

    private String paymentMethod;

    private Timestamp updateDate;

    @NotEmpty(message = "Tên người thuê không được để trống")
    private String driverName;

    @NotEmpty(message = "Ngày sinh không được để trống")
    private String driverDob;

    @NotEmpty(message = "Số điện thoại không được để trống")
    private String driverPhone;

    @NotEmpty(message = "Email không được để trống")
    private String driverEmail;

    @NotEmpty(message = "Số định danh công dân không được để trống")
    private String NationID;

    @NotEmpty(message = "Bằng lái xe không được để trống")
    private String driverLiense;

    @NotEmpty(message = "Địa chỉ không được để trống")
    private String driverAddress;
}
