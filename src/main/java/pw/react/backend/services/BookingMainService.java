package pw.react.backend.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.BookingRepository;
import pw.react.backend.dao.CarRepository;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.User;
import pw.react.backend.models.Car;
import pw.react.backend.models.Booking;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
public class BookingMainService implements BookingService{
    private static final Logger logger = LoggerFactory.getLogger(BookingMainService.class);
    protected final BookingRepository bookingRepository;
    public BookingMainService(BookingRepository bookingRepository)
    {
        this.bookingRepository=bookingRepository;
    }
    @Override
    public Collection<Booking> getByCarId(Long carId)
    {
        return bookingRepository.findByCarId(carId);
    }
    @Override
    public Collection<Booking> getAll() {
        return bookingRepository.findAll();
    }
    @Override
    public Booking updateBooking(Long id, Booking updatedBooking) throws ResourceNotFoundException {
        if (bookingRepository.existsById(id)) {
            updatedBooking.setId(id);
            Booking result = bookingRepository.save(updatedBooking);
            logger.info("Booking with id {} updated.", id);
            return result;
        }
        throw new ResourceNotFoundException(String.format("Booking with id [%d] not found.", id));
    }

    @Override
    public boolean deleteBooking(Long bookingId) {
        boolean result = false;
        if (bookingRepository.existsById(bookingId)) {
            bookingRepository.deleteById(bookingId);
            logger.info("Booking with id {} deleted.", bookingId);
            result = true;
        }
        return result;
    }

    @Override
    public Collection<Booking> batchSave(Collection<Booking> bookings) {
        if (bookings != null && !bookings.isEmpty()) {
            return bookingRepository.saveAll(bookings);
        } else {
            logger.warn("Bookings collection is empty or null.");
            return Collections.emptyList();
        }
    }
    @Override
    public Optional<Booking> getById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public Collection<Booking> getAllOwnersOrderedByDate(Collection<Long> ids,int page) {
        Pageable pageable= PageRequest.of(page,20);
        return bookingRepository.findAllByCarIdInOrderByStartDate(ids,pageable);
    }

    @Override
    public Collection<Booking> getAllUser(Long Id, int page) {
        Pageable pageable =PageRequest.of(page,20);
       return bookingRepository.findAllByUserIdOrderByStartDateDesc(Id,pageable);
    }

    @Override
    public Booking AddBooking(Booking booking) {
        return  bookingRepository.save(booking);
    }

    @Override
    public Collection<Booking> FindOverlapping(LocalDateTime date) {
        return  bookingRepository.findByEndDateAfter(date);
    }
}
