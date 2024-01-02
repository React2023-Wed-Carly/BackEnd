package pw.react.backend.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.CarRepository;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.User;
import pw.react.backend.models.Car;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CarMainService implements CarService{
    private static final Logger logger = LoggerFactory.getLogger(CarMainService.class);

    protected final CarRepository carRepository;
    public CarMainService(CarRepository carRepository)
    {
        this.carRepository=carRepository;
    }

    @Override
    public Collection<Car> getByOwnerId(Long id)
    {
       return carRepository.findByOwnerId(id);
    }
    @Override
    public Collection<Car> getAll() {
        return carRepository.findAll();
    }
    @Override
    public Car updateCar(Long id, Car updatedCar) throws ResourceNotFoundException {
        if (carRepository.existsById(id)) {
            updatedCar.setId(id);
            Car result = carRepository.save(updatedCar);
            logger.info("Car with id {} updated.", id);
            return result;
        }
        throw new ResourceNotFoundException(String.format("Car with id [%d] not found.", id));
    }

    @Override
    public boolean deleteCar(Long carId) {
        boolean result = false;
        if (carRepository.existsById(carId)) {
            carRepository.deleteById(carId);
            logger.info("Car with id {} deleted.", carId);
            result = true;
        }
        return result;
    }

    @Override
    public Collection<Car> batchSave(Collection<Car> cars) {
        if (cars != null && !cars.isEmpty()) {
            return carRepository.saveAll(cars);
        } else {
            logger.warn("Cars collection is empty or null.");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Car> getById(Long carId) {
        return carRepository.findById(carId);
    }

}
