package pw.react.backend.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import pw.react.backend.dao.CarRepository;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.dao.BookingRepository;

import pw.react.backend.models.User;
import pw.react.backend.services.CarMainService;
import pw.react.backend.services.CarService;
import pw.react.backend.services.UserMainService;
import pw.react.backend.services.UserService;
import pw.react.backend.services.BookingService;
import pw.react.backend.services.BookingMainService;

import javax.sql.DataSource;

@Profile({"batch", "*mysql*"})
public class BatchConfig {

    private final DataSource dataSource;

    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
    @Bean
    public CarService carService(CarRepository carRepository)
    {
        return new CarMainService(carRepository);
    }

    @Bean
    public BookingService bookingService(BookingRepository bookingRepository)
    {
        return new BookingMainService(bookingRepository);
    }
    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder, BatchRepository<User> userBatchRepository) {
        return new UserMainService(userRepository, passwordEncoder);
    }


    @Bean
    public UserBatchRepository userBatchRepository(JdbcTemplate jdbcTemplate) {
        return new UserBatchRepository(jdbcTemplate);
    }
}
