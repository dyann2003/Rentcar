package Renter_Car.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "cars") // Đặt lại tên bảng cho phù hợp
public class Car {

    // Add status constants
    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_STOPPED_RENTING = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 17)
    private String VIN;  // Số khung xe gồm cả chữ cả số

    private int numberOfSeats;  // Số chỗ ngồi
    private int productionYear;  // Năm sản xuất

    private String transmissionType;  // Loại hộp số
    private String fuelType;  // Loại nhiên liệu

    private double mileage;  // Số km đã đi
    private double fuelConsumption;  // Mức tiêu thụ nhiên liệu

    private double pricePerDay;  // Giá thuê theo ngày
    private double deposit;  // Tiền đặt cọc

    private String address;  // Địa chỉ
    private String additional_functions;  // Chức năng bổ sung

    private boolean isDelivery;

    private int status;

    private Double rating;

    private String carType;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "model_id")
    private Model model;

    @Column(columnDefinition = "Text")
    private String description;  // Mô tả

    @ElementCollection
    private List<String> imagePaths;

    @ManyToOne
    @JoinColumn(name = "color_id", referencedColumnName = "color_id")
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "car",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Report> report;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings;

}
