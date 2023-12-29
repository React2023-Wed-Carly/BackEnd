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
import pw.react.backend.models.Payment;
import pw.react.backend.services.BookingService;
import pw.react.backend.services.CarService;
import pw.react.backend.services.PaymentService;
import pw.react.backend.web.CarDto;
import pw.react.backend.web.UserDto;
import pw.react.backend.web.BookingDto;
import pw.react.backend.web.PaymentDto;

import java.util.Collection;
@RestController
@RequestMapping(path = PaymentController.PAYMENTS_PATH)
public class PaymentController {
    public final static String PAYMENTS_PATH = "/payments";

    @Autowired
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "get all payments")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "list of payments",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = PaymentDto.class))}
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Something went wrong"
            )
    })
    @GetMapping(path = "")
    public ResponseEntity<Collection<PaymentDto>> GetPayments() {
        try{
            Collection<PaymentDto> Payments=paymentService.getAll().stream().map(PaymentDto::valueFrom).toList();
            return ResponseEntity.ok(Payments);
        } catch (Exception ex) {
            throw new UserValidationException(ex.getMessage(), PAYMENTS_PATH);
        }
    }

}
