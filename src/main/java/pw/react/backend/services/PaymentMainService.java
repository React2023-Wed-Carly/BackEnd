package pw.react.backend.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.BookingRepository;
import pw.react.backend.dao.CarRepository;
import pw.react.backend.dao.PaymentRepository;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.User;
import pw.react.backend.models.Car;
import pw.react.backend.models.Booking;
import pw.react.backend.models.Payment;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
public class PaymentMainService implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentMainService.class);
    protected final PaymentRepository paymentRepository;
    public PaymentMainService(PaymentRepository paymentRepository)
    {
        this.paymentRepository=paymentRepository;
    }
    @Override
    public Collection<Payment> getByUserId(Long userId)
    {
        return paymentRepository.findByUserId(userId);
    }
    @Override
    public Collection<Payment> getAll() {
        return paymentRepository.findAll();
    }
    @Override
    public Payment updatePayment(Long id, Payment updatedPayment) throws ResourceNotFoundException {
        if (paymentRepository.existsById(id)) {
            updatedPayment.setId(id);
            Payment result = paymentRepository.save(updatedPayment);
            logger.info("Payment with id {} updated.", id);
            return result;
        }
        throw new ResourceNotFoundException(String.format("Payment with id [%d] not found.", id));
    }

    @Override
    public boolean deletePayment(Long paymentId) {
        boolean result = false;
        if (paymentRepository.existsById(paymentId)) {
            paymentRepository.deleteById(paymentId);
            logger.info("Payment with id {} deleted.", paymentId);
            result = true;
        }
        return result;
    }

    @Override
    public Collection<Payment> batchSave(Collection<Payment> payments) {
        if (payments != null && !payments.isEmpty()) {
            return paymentRepository.saveAll(payments);
        } else {
            logger.warn("Payments collection is empty or null.");
            return Collections.emptyList();
        }
    }
    @Override
    public Optional<Payment> getById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public Collection<Payment> getAllOrderedByDate(int page) {
        Pageable pageable =PageRequest.of(page,20);
        return paymentRepository.findAllByOrderByDate(pageable);
    }
    @Override
    public Collection<Payment> getUsersByDate(Long UserId,int page)
    {
        Pageable pageable =PageRequest.of(page,20);
        return paymentRepository.findAllByUserIdOrderByDateDesc(UserId,pageable);
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
