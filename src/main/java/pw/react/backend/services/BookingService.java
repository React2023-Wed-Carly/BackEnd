package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Car;
import pw.react.backend.models.Booking;

import java.util.Collection;
import java.util.Optional;

public interface BookingService {
    Booking updateBooking(Long id, Booking updatedBooking) throws ResourceNotFoundException;
    boolean deleteBooking(Long bookingId);
    Collection<Booking> batchSave(Collection<Booking> bookings);
    Optional<Booking> getById(Long bookingId);
    Collection<Booking> getAll();
    Collection<Booking> getAllOwnersOrderedByDate(Collection<Long>ids,int page);
    Collection<Booking> getByCarId(Long carId);
}
