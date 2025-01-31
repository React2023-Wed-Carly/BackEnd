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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.*;
import pw.react.backend.services.*;
import pw.react.backend.web.BookingDto;
import pw.react.backend.web.CarDto;
import pw.react.backend.web.PaymentDto;
import pw.react.backend.web.UserDto;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.core.Authentication;



@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String USERS_PATH = "/users";
    private final static Long PARKLY_ID=5L;

    private final UserService userService;
    private final CarService carService;
    private final FavoriteCarService favoriteCarService;
    private ImageService carImageService;
    private final PaymentService paymentService;
    private final BookingService bookingService;

    public UserController(UserService userService,CarService carService,FavoriteCarService favoriteCarService
    ,PaymentService paymentService,BookingService bookingService) {
        this.userService = userService;
        this.carService=carService;
        this.favoriteCarService=favoriteCarService;
        this.paymentService=paymentService;
        this.bookingService=bookingService;
    }
    @Autowired
    public void setCarImageService(ImageService carImageService)
    {
        this.carImageService=carImageService;
    }
    @Operation(summary = "get all user details")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User details sent",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = UserDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/details")
    public ResponseEntity<UserDto> UserDetails(Authentication auth)
    {
        try{
            User us=userService.FindByUserName(auth.getName())
                    .orElseThrow(()->new ResourceNotFoundException("couldnt find user"));
            return ResponseEntity.ok(UserDto.valueFrom(us));
        }
        catch (Exception exception)
        {
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }
    @Operation(summary = "get all user payments")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User payments sent",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = PaymentDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/details/payments")
    public ResponseEntity<Collection<PaymentDto>> UserPayments(Authentication auth,@RequestParam("page") int page)
    {
        try{
            User us=userService.FindByUserName(auth.getName())
                    .orElseThrow(()->new ResourceNotFoundException("couldnt find user"));
            Collection<PaymentDto> paymentDtos=paymentService.getUsersByDate(us.getId(),page)
                    .stream().map(PaymentDto::valueFrom).toList();
            return ResponseEntity.ok(paymentDtos);
        }
        catch (Exception exception)
        {
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }
    @Operation(summary = "get all user bookings")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User bookings sent",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = BookingDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/details/bookings")
    public ResponseEntity<Collection<BookingDto>> UserBookings(Authentication auth,@RequestParam("page") int page)
    {
        try{
            User us=userService.FindByUserName(auth.getName())
                    .orElseThrow(()->new ResourceNotFoundException("couldnt find user"));
            Collection<BookingDto> bookingDtos=bookingService.getAllUser(us.getId(),page)
                    .stream().map(BookingDto::valueFrom).toList();
            return ResponseEntity.ok(bookingDtos);
        }
        catch (Exception exception)
        {
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }
    @Operation(summary = "get all parkly user bookings")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User bookings sent",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = BookingDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/details/bookings/{IntegratedId}")
    public ResponseEntity<Collection<BookingDto>> IntegratedUserBookings(@PathVariable("IntegratedId")Long intId, Authentication auth,@RequestParam("page") int page)
    {
        try{

            User us=userService.FindByUserName(auth.getName())
                    .orElseThrow(()->new ResourceNotFoundException("couldnt find user"));
            if(us.getId()!=PARKLY_ID)
                throw new UserValidationException("only Parkly proxy can access this endpoint");
            Collection<BookingDto> bookingDtos=bookingService.getAllInegratedUser(us.getId(),page,intId)
                    .stream().map(BookingDto::valueFrom).toList();
            return ResponseEntity.ok(bookingDtos);
        }
        catch (Exception exception)
        {
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }
    @Operation(summary = "top up user account by given amount")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "amount added to account"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PostMapping("/details/Topup")
    public ResponseEntity<Void> TopUpAccount(@RequestParam("amount") Long amount,Authentication auth)
    {
        try{
            User us=userService.FindByUserName(auth.getName())
                    .orElseThrow(()->new ResourceNotFoundException("couldnt find user"));
            Long oldBalance=us.getBalance();
            us.setBalance(oldBalance+amount);
            userService.saveEdited(us);
            Payment payment =new Payment();
            payment.setUserId(us.getId());
            payment.setDate(LocalDateTime.now());
            payment.setAmount(Long.valueOf(amount));
            paymentService.savePayment(payment);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        catch (Exception ex)
        {
            throw new ResourceNotFoundException(ex.getMessage());
        }
    }
    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = UserDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PostMapping(path = "")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
        try {
            User us=UserDto.convertToUser(user);
            us.setAdmin(false);
            us.setBalance(Long.valueOf(0));
            us.setDistanceTravelled(Long.valueOf(0));
             us= userService.validateAndSave(us);
            if(us==null)
                throw new UserValidationException("username already exists");
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.valueFrom(us));
        } catch (Exception ex) {
            throw new UserValidationException(ex.getMessage(), USERS_PATH);
        }
    }
    @Operation(summary = "add car to favorites")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "favorite added"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PostMapping("/favorites/{carId}")
    public ResponseEntity<Void> AddFavorite(@PathVariable Long carId,Authentication auth)
        {
        try
        {
            User us=userService.FindByUserName(auth.getName()).orElseThrow(
                    ()->new ResourceNotFoundException("user doesnt exists"));
            Car car=carService.getById(carId).orElseThrow(
                    ()->new ResourceNotFoundException("car doesnt exists")
            );
            favoriteCarService.AddFavorite(us.getId(),carId);
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),USERS_PATH+"/favorites");
        }
    }
    @Operation(summary = "delete car from favorites")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "favorite deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @Transactional
    @DeleteMapping("/favorites/{carId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long carId,Authentication auth)
    {
        try
        {
            User us=userService.FindByUserName(auth.getName()).orElseThrow(
                    ()->new ResourceNotFoundException("user doesnt exists"));
            favoriteCarService.deleteFavorite(us.getId(),carId);
            return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),USERS_PATH+"/favorites");
        }
    }
    @Operation(summary = "get favorite cars, the same schema as in getting cars")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "favorite cars",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = Map.class))}

            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/favorites")
    public ResponseEntity<List<Map<String,Object>>> getFavorites(Authentication auth,@RequestParam("page") int page)
    {
        try
        {
            User us=userService.FindByUserName(auth.getName()).orElseThrow(
                    ()->new ResourceNotFoundException("user doesnt exists"));

            List<Long> carids=favoriteCarService.getAllbyUser(us.getId()).stream()
                    .map((favoriteCars -> favoriteCars.getCarId())).toList();

            Collection<CarDto> cars=carService.getbyIdIn(carids,page).stream().map(CarDto::valueFrom).toList();
            List<Map<String,Object>> resp=new LinkedList<>();
            for (CarDto c:cars
            ) {
                Map<String,Object>obj=new HashMap<>();
                CarImage img=carImageService.getCarImage(c.id());
                byte[] bytes;
                if(img!=null)
                    bytes=img.getData();
                else
                    bytes=null;

                obj.put("info",c);
                obj.put("img",bytes);
                resp.add(obj);
            }
            return ResponseEntity.ok(resp);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),USERS_PATH+"/favorites");
        }

    }
    @Operation(summary = "check if car is favorite")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "boolean info",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = Boolean.class))}

            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("favorites/isFavorite/{carId}")
    public ResponseEntity<Boolean> isFavorite(Authentication auth,@PathVariable("carId") long carId)
    {
        try {
            User us = userService.FindByUserName(auth.getName()).orElseThrow(
                    () -> new ResourceNotFoundException("user doesnt exists"));
            Optional<FavoriteCars> fc=favoriteCarService.findIfFavorite(us.getId(),carId);
            if(fc.isPresent())
                return ResponseEntity.ok(Boolean.TRUE);
            else
                return ResponseEntity.ok(Boolean.FALSE);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),USERS_PATH+"/favorites");
        }

    }
}
