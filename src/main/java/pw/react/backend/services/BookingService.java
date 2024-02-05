package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Car;
import pw.react.backend.models.Booking;

import java.awt.print.Book;
import java.time.LocalDateTime;
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
    Collection<Booking> getAllUser(Long Id,int page);
    Collection<Booking> getAllInegratedUser(Long Id,int page,Long intId);
    Booking AddBooking(Booking booking);
    Collection<Booking> FindOverlapping(Long carId,LocalDateTime date);
}
