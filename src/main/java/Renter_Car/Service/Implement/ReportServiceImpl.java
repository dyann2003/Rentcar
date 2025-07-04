package Renter_Car.Service.Implement;

import Renter_Car.Models.Report;
import Renter_Car.Repository.ReportRepository;
import Renter_Car.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;


    @Override
    public Page<Report> getAllReports(int userid, List<String> mark, int page, int size) {
        return reportRepository.findAllReportOfCarOwner(userid, mark, PageRequest.of(page, size));
    }

    @Override
    public double getTotalMark(int userId) {
        return reportRepository.totalMarkofCarOwner(userId) == 0 ? 0 : reportRepository.totalMarkofCarOwner(userId);
    }

    @Override
    public Integer countMark(int userId, String mark) {
        return reportRepository.numberOfMark(userId,mark) == null ? 0: reportRepository.numberOfMark(userId,mark);
    }

    @Override
    public List<Report> getReportByCarId(int carId) {
        return reportRepository.getReportByCarId(carId);
    }

    @Override
    public void insertReport(Report report) {
        reportRepository.saveAndFlush(report);
    }
}
