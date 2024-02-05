package pw.react.backend.dao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Car;
import pw.react.backend.models.User;
import pw.react.backend.models.Booking;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.*;
public interface BookingRepository extends JpaRepository<Booking, Long>{
    Collection<Booking> findByCarId(Long carId);
    List<Booking> findAllByCarIdInOrderByStartDate(Collection<Long> CarIds, Pageable pageable);
    List<Booking> findAllByUserIdOrderByStartDateDesc(Long userId,Pageable pageable);
    Collection<Booking> findByCarIdAndEndDateAfter(Long userId,LocalDateTime date);
    List<Booking> findAllByUserIdAndAndIntegratedSystemIdOrderByStartDateDesc(Long userId,Long IntId,Pageable pageable);

}
