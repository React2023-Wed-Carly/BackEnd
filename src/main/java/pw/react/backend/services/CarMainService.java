package pw.react.backend.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.CarRepository;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.User;
import pw.react.backend.models.Car;
import java.util.Collection;

public class CarMainService implements CarService{
    private static final Logger log = LoggerFactory.getLogger(CarMainService.class);

    protected final CarRepository carRepository;
    public CarMainService(CarRepository carRepository)
    {
        this.carRepository=carRepository;
    }

    @Override
    public Collection<Car> getByOwnerID(long id)
    {
       return carRepository.findCarsByOwnerID(id);
    }

}
