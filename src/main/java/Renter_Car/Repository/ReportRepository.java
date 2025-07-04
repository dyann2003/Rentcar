package Renter_Car.Repository;

import Renter_Car.Models.Car;
import Renter_Car.Models.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer>  {

    @Query("select r from Report  r where  r.car.user.id= :userId and r.mark IN  :mark" )
    Page<Report> findAllReportOfCarOwner(@Param("userId") int userId,@Param("mark") List<String> mark,Pageable pageable);

    @Query("select sum(r.mark) from  Report  r where  r.car.user.id= :userId")
    Double totalMarkofCarOwner(@Param("userId") int userId );

    @Query("select count(r.mark) from  Report  r where  r.car.user.id= :userId and r.mark= :mark")
    Integer numberOfMark(@Param("userId") int userId , @Param("mark") String mark);

    List<Report> getReportByCarId(int carId);
}
