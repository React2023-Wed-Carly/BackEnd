package pw.react.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.Booking;
import pw.react.backend.models.User;
import pw.react.backend.services.BookingService;
import pw.react.backend.services.CarService;
import pw.react.backend.web.CarDto;
import pw.react.backend.web.UserDto;
import pw.react.backend.web.BookingDto;

import java.util.Collection;

@RestController
@RequestMapping(path = BookingController.BOOKINGS_PATH)
public class BookingController {
    public final static String BOOKINGS_PATH = "/bookings";

    @Autowired
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "get all bookings")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "list of bookings",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = BookingDto.class))}
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Something went wrong"
            )
    })
    @GetMapping(path = "")
    public ResponseEntity<Collection<BookingDto>> GetBookings() {
        try{
            Collection<BookingDto> Bookings=bookingService.getAll().stream().map(BookingDto::valueFrom).toList();
            return ResponseEntity.ok(Bookings);
        } catch (Exception ex) {
            throw new UserValidationException(ex.getMessage(), BOOKINGS_PATH);
        }
    }

}
