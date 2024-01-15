package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Car;
import pw.react.backend.models.FavoriteCars;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CarService {
    Collection<Car> getAllbyOwner(Long ownerId);
    Car updateCar(Long id, Car updatedCar) throws ResourceNotFoundException;
    boolean deleteCar(Long carId);
    Collection<Car> batchSave(Collection<Car> cars);
    Car save(Car car);
    Optional<Car> getById(Long carId);
    Collection<Car> getAll();
    Collection<Car> getByOwnerId(Long id);
    Collection<Car> getbyIdIn(List<Long> ids,int page);
}
