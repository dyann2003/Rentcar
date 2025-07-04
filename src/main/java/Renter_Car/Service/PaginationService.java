package Renter_Car.Service;

import java.util.ArrayList;
import java.util.List;

public interface PaginationService {
    public List<Integer> getSimplePage(int totalPage);

    public List<Integer> getComplexPage(int totalPage, int currentPage);

}