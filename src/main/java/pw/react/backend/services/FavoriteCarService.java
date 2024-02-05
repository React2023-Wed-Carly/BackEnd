package pw.react.backend.services;

import java.util.Collection;
import java.util.Optional;

import pw.react.backend.models.FavoriteCars;

public interface FavoriteCarService {
    Collection<FavoriteCars> getAllbyUser(Long ownerId);
    void AddFavorite(Long userId,Long carId);
    void deleteFavorite(Long userId,Long carId);
    Optional<FavoriteCars>  findIfFavorite(Long userId,Long carId);
    void deleteByCarId(Long carId);

}
