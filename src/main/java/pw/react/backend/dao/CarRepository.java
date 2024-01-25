package pw.react.backend.dao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pw.react.backend.models.Car;
import pw.react.backend.models.FavoriteCars;
import pw.react.backend.models.User;

import java.util.*;

public interface CarRepository extends JpaRepository<Car, Long>{
    Collection<Car> findByOwnerId(Long OwnerId);
    List<Car> findAllByIdIn(List<Long> ids, Pageable pageable);
    List<Car> findAllByOwnerIdOrderByIdDesc(Long ownerId,Pageable pageable);
    @Query(
            value = "SELECT * FROM Car c where c.daily_price>=?3 and c.daily_price<=?4 and c.seating_capacity>=?5" +
                    " and c.seating_capacity<=?6 and c.transmission in ?7" +
                    " order by (c.latitude-?1)*(c.latitude-?1)+(c.longitude-?2)*(c.longitude-?2) ",
            countQuery = "SELECT count(*) FROM Car",
            nativeQuery = true)
    List<Car> findAllUsersWithPagination(Double lat,Double lon,Long minP,Long maxP,Long minS,Long maxS,String[] trans,Pageable pageable);
}
    