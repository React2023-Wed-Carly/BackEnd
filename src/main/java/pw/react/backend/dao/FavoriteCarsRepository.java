package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pw.react.backend.models.FavoriteCars;

import java.util.*;

public interface FavoriteCarsRepository extends JpaRepository<FavoriteCars,Long> {
    void deleteByUserIdAndCarId(Long UserId,Long CarId);
    Collection<FavoriteCars> findAllByUserId(Long UserId);
    Collection<FavoriteCars> findAllByUserIdAndCarId(Long userId,Long carId);
}
