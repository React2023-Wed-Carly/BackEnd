package pw.react.backend.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.CarImage;

import java.util.Collection;
import java.util.Optional;

@Transactional
public interface CarImageRepository extends JpaRepository<CarImage, String>{
    Optional<CarImage> findCarImageByCarId(long carId);
    void deleteByCarId(long carId);
}
