package pw.react.backend.services;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Car;
import pw.react.backend.models.Booking;
import pw.react.backend.models.Payment;

import java.util.Collection;
import java.util.Optional;
public interface PaymentService {
    Payment updatePayment(Long id, Payment updatedPayment) throws ResourceNotFoundException;
    boolean deletePayment(Long paymentId);
    Collection<Payment> batchSave(Collection<Payment> payments);

    Optional<Payment> getById(Long paymentId);
    Collection<Payment> getAll();
    Collection<Payment> getByUserId(Long userId);
    Collection<Payment> getAllOrderedByDate(int page);
}
