package pw.react.backend.services;

import java.util.Collection;
import pw.react.backend.models.FavoriteCars;

public interface FavoriteCarService {
    Collection<FavoriteCars> getAllbyUser(Long ownerId);
    void AddFavorite(Long userId,Long carId);
    void deleteFavorite(Long userId,Long carId);

}
