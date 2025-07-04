package Renter_Car.Service.Implement;

import Renter_Car.Constrant.IConstants;
import Renter_Car.Service.PaginationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaginationServiceImpl implements PaginationService {
    @Override
    public List<Integer> getSimplePage(int totalPage) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < totalPage; i++) {
            list.add(i);
        }
        return list;
    }

    @Override
    public List<Integer> getComplexPage(int totalPage, int currentPage) {
        List<Integer> list = new ArrayList<>();
        if (currentPage < 3) {
            for (int i = 0; i < IConstants.LIMIT_PAGE_DISPLAY; i++) {
                list.add(i);
            }
        } else if (currentPage < totalPage - 2) {
            for (int i = currentPage - 2; i <= currentPage + 2; i++) {
                list.add(i);
            }
        } else {
            for (int i = totalPage - 5; i < totalPage; i++) {
                list.add(i);
            }
        }
        return list;
    }
}
