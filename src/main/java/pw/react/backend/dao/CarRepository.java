package pw.react.backend.dao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Car;
import pw.react.backend.models.User;

import java.util.*;

public interface CarRepository extends JpaRepository<Car, Long>{
    Collection<Car> findByOwnerId(Long OwnerId);
    List<Car> findAllByIdIn(List<Long> ids, Pageable pageable);
}
    