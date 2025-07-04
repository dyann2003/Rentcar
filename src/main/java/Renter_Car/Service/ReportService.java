package Renter_Car.Service;

import Renter_Car.Models.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ReportService{

   public Page<Report> getAllReports(int userid,List<String> mark, int page , int size);

   public double getTotalMark(int userId);

   public Integer countMark(int userId,String mark);

   public List<Report> getReportByCarId(int carId);

   public void insertReport(Report report);
}
