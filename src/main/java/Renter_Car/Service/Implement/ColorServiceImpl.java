package Renter_Car.Service.Implement;

import Renter_Car.Models.Color;
import Renter_Car.Repository.ColorRepository;
import Renter_Car.Service.ColorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;

    public ColorServiceImpl(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Override
    public List<Color> getColors() {
        return colorRepository.findAll();
    }
}
