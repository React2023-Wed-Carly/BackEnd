package pw.react.backend.services;

import pw.react.backend.models.Car;

import java.util.Collection;

public interface CarService {
    Collection<Car> getByOwnerID(long id);
}
