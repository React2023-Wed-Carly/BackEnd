package pw.react.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UnauthorizedException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.*;
import pw.react.backend.services.*;
import pw.react.backend.web.BookingDto;
import pw.react.backend.web.CarDto;
import pw.react.backend.web.UploadFileResponse;
import pw.react.backend.web.UserDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.core.Authentication;
@RestController
@RequestMapping(path = CarController.CARS_PATH)
public class CarController {
    public final static String CARS_PATH = "/cars";
    private static final Logger log = LoggerFactory.getLogger(CarController.class);
    private final static Long PARKLY_ID= 5L;
    @Autowired
    private final CarService carService;
    @Autowired
    private final UserService userService;
    private  final PaymentService paymentService;
    private  final BookingService bookingService;
    private ImageService carImageService;

    @Autowired
    private void setCarImageService(ImageService carImageService){this.carImageService=carImageService;}

    public CarController(CarService carService,UserService userService,PaymentService paymentService,BookingService bookingService) {
        this.carService = carService;
        this.userService=userService;
        this.paymentService=paymentService;
        this.bookingService=bookingService;
    }
    @Operation(summary = "get all cars with filters applied, ordered by distance, schema matching car details")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = Map.class))}
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Something went wrong"
            )
    })
    @GetMapping("")
    public ResponseEntity<Collection<Map<String,Object>>> getCars(Authentication auth,@RequestParam("page") int page
    ,@RequestParam("lat") Double lat,@RequestParam("lon") Double lon,@RequestParam("minPrice") Long minP,
                                                      @RequestParam("maxPrice") Long maxP,
                                                      @RequestParam("minSeat") Long minS,
                                                      @RequestParam("maxSeat") Long maxS,
                                                      @RequestParam("trans") String trans)
    {
     try
     {
         String[] transS=trans.split(";");
         Collection<CarDto> cars=carService.getFilterdCars(page,lat,lon,minP,maxP,minS,maxS,transS).stream()
                 .map(CarDto::valueFrom).toList();
         Collection<Map<String,Object>> resp=new ArrayList<>();
         for (CarDto c:cars) {
             CarImage img=carImageService.getCarImage(c.id());
             byte[] bytes;
             if(img!=null)
                 bytes=img.getData();
             else
                 bytes=null;
             Map<String,Object> obj=new HashMap<>();
             obj.put("info",c);
             obj.put("img",bytes);
             resp.add(obj);
         }
        return ResponseEntity.ok(resp);
     }
     catch (Exception ex)
     {
         throw  new ResourceNotFoundException("something went wrong");
     }
    }
@Operation(summary = "get all car info by id, includes info- with all fields and img with car image in binary")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = Map.class))}
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Something went wrong"
            )
    })
    @GetMapping(path = "/{carId}")
    public ResponseEntity<Map<String,Object>> GetCarInfo(@PathVariable("carId") long carId, Authentication auth ) {
        try {

            User us=userService.FindByUserName(auth.getName()).orElseThrow(
                    ()->new UserValidationException("cant find user with given token"));

            CarDto car=CarDto.valueFrom(carService.getById(carId).orElseThrow(
                    ()->new ResourceNotFoundException("car with given id doesnt exist")));

                CarImage img=carImageService.getCarImage(car.id());
                byte[] bytes;
                if(img!=null)
                    bytes=img.getData();
                else
                    bytes=null;
                Map<String,Object> resp=new HashMap<>();
                resp.put("info",car);
                resp.put("img",bytes);
            return ResponseEntity.ok(resp);
            }
         catch (Exception ex) {
            throw new ResourceNotFoundException(ex.getMessage()+" "+CarController.CARS_PATH);
        }
    }
    @Operation(summary = "post booking to car given by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "booking added",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = BookingDto.class))}
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Something went wrong"
            )
    })
    @PostMapping("/{carId}/bookings")
    public ResponseEntity<BookingDto> PostCarBooking(Authentication auth,@PathVariable("carId") long carId,
                                                      @RequestBody  BookingDto bookingDto)
    {

        try {
            Booking booking=BookingDto.ConvertToBooking(bookingDto);
            User us=userService.FindByUserName(auth.getName()).orElseThrow(
                    ()->new UserValidationException("cant find user with given token"));
            if(us.getId().equals(PARKLY_ID)&&booking.getIntegratedSystemId()==null)
                throw new UserValidationException("integreated system booking requires user id");
            booking.setUserId(us.getId());
            booking.setCompleted(Boolean.FALSE);
            if(booking.getCarId()!=carId)
            {
                throw new IllegalAccessException(" path carId doesnt match booking car id");
            }
            Car car=carService.getById(carId).orElseThrow(
                    ()->new ResourceNotFoundException("car with given id doesnt exists")
            );
            List<Booking> collisions=bookingService.FindOverlapping(booking.getCarId(),booking.getStartDate()).stream().toList();
            if(!collisions.isEmpty())
                throw new ResourceNotFoundException("booking dates are overlapping");
            long days=Duration.between(booking.getStartDate(), bookingDto.endDate()).toDays();
            long cost=car.getDailyPrice()*days;
            if(cost>us.getBalance())
                throw new ResourceNotFoundException("not enough money");
            Payment payment=new Payment();
            payment.setAmount(Long.valueOf(cost));
            System.out.println(payment.getAmount());
            payment.setUserId(us.getId());
            payment.setDate(LocalDateTime.now());
            long balance=us.getBalance();
            System.out.println(Long.valueOf(balance-cost));
            us.setBalance(balance-cost);
            userService.saveEdited(us);
            paymentService.savePayment(payment);
            booking.setId(null);

            BookingDto bookingDto1= BookingDto.valueFrom(bookingService.AddBooking(booking));

            return  ResponseEntity.status(202).body(bookingDto1);
        }

        catch (Exception ex) {
            throw new ResourceNotFoundException(ex.getMessage()+" "+CARS_PATH+"/{id}/booking");
        }
    }
    @Operation(summary = "get car bookings")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "bookings",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = BookingDto.class))}
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/{carId}/bookings")
    public ResponseEntity<Collection<BookingDto>> getcarBookings(Authentication auth,@PathVariable("carId") long carId)
    {
        try
        {
            Collection<BookingDto> bookingDtos=bookingService.getByCarId(carId).stream().map(BookingDto::valueFrom).toList();
            return ResponseEntity.ok(bookingDtos);
        }
        catch (Exception ex)
        {
            throw new ResourceNotFoundException(ex.getMessage()+" "+CARS_PATH+"/{id}/booking");
        }
    }
}
