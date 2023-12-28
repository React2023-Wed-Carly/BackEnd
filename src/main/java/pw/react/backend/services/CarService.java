package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Car;

import java.util.Collection;
import java.util.Optional;

public interface CarService {
    Car updateCar(Long id, Car updatedCar) throws ResourceNotFoundException;
    boolean deleteCar(Long carId);
    Collection<Car> batchSave(Collection<Car> cars);
    Optional<Car> getById(Long carId);
    Collection<Car> getAll();
    Collection<Car> getByOwnerID(Long id);
}
