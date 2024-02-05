package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pw.react.backend.models.FavoriteCars;

import java.util.*;

public interface FavoriteCarsRepository extends JpaRepository<FavoriteCars,Long> {
    void deleteByUserIdAndCarId(Long UserId,Long CarId);
    Collection<FavoriteCars> findAllByUserIdOrderByIdDesc(Long UserId);
    Collection<FavoriteCars> findAllByUserIdAndCarId(Long userId,Long carId);
    Optional<FavoriteCars> findByUserIdAndCarId(Long userId,Long carId);
    void deleteByCarId(Long CarId);
}
