package Renter_Car.Models;

import jakarta.persistence.*;
import lombok.*;

@Table(name="car_report")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Report {

    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="date")
    private String date;

    @Column(name="mark")
    private int mark;

    @Lob
    @Column(name="comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name="car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne
    @JoinColumn(name="booking_id", referencedColumnName = "id")
    private Booking booking;

    public Report(String date, int mark,
                  String comment,
                  Car car,
                  User user,
                  Booking booking) {
        this.date = date;
        this.mark = mark;
        this.comment = comment;
        this.car = car;
        this.user = user;
        this.booking = booking;
    }
}
