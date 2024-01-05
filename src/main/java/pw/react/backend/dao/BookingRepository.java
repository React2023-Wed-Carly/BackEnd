package pw.react.backend.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Car;
import pw.react.backend.models.User;
import pw.react.backend.models.Booking;

import java.util.*;
public interface BookingRepository extends JpaRepository<Booking, Long>{
    Collection<Booking> findByCarId(Long carId);


}
