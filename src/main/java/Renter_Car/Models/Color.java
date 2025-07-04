package Renter_Car.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "color")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "color_id")
    private int id;

    @Column(name = "color_name")
    private String color;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL)
    private List<Car> cars;

}
