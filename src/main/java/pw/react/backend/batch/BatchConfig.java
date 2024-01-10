package pw.react.backend.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import pw.react.backend.dao.*;

import pw.react.backend.models.User;
import pw.react.backend.services.*;

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
    public PaymentService paymentService(PaymentRepository paymentRepository)
    {
        return new PaymentMainService(paymentRepository);
    }
    @Bean
    public ImageService imageService(CarImageRepository carImageRepository)
    {
        return new CarImageService(carImageRepository);
    }
    @Bean
    public FavoriteCarService favoriteCarService(FavoriteCarsRepository favoriteCarRepository)
    {
        return new FavoriteCarMainService(favoriteCarRepository);
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
