package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.CarRepository;
import pw.react.backend.dao.FavoriteCarsRepository;
import pw.react.backend.models.FavoriteCars;

import java.util.Collection;

public class FavoriteCarMainService implements FavoriteCarService{
    private static final Logger logger = LoggerFactory.getLogger(CarMainService.class);

    protected final FavoriteCarsRepository favoriteCarsRepository;
    public FavoriteCarMainService(FavoriteCarsRepository favoriteCarsRepository)
    {
        this.favoriteCarsRepository=favoriteCarsRepository;
    }
    @Override
    public Collection<FavoriteCars> getAllbyUser(Long userId) {
        return favoriteCarsRepository.findAllByUserIdOrderByIdDesc(userId);
    }

    @Override
    public void AddFavorite(Long userId, Long carId) {

        FavoriteCars fc=new FavoriteCars();
        fc.setCarId(carId);
        fc.setUserId(userId);
        if(favoriteCarsRepository.findAllByUserIdAndCarId(userId,carId).isEmpty())
            favoriteCarsRepository.save(fc);

    }

    @Override
    public void deleteFavorite(Long userId, Long carId) {
        favoriteCarsRepository.deleteByUserIdAndCarId(userId,carId);
    }
}
