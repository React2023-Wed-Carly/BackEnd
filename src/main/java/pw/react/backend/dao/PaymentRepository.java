package pw.react.backend.dao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Car;
import pw.react.backend.models.User;
import pw.react.backend.models.Booking;
import pw.react.backend.models.Payment;

import java.util.*;
public interface PaymentRepository extends JpaRepository<Payment, Long>{
    Collection<Payment> findByUserId(Long userId);
    List<Payment> findAllByOrderByDate(Pageable pageable);
}
