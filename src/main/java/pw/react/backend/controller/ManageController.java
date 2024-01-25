package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UnauthorizedException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.Booking;
import pw.react.backend.models.Car;
import pw.react.backend.models.CarImage;
import pw.react.backend.services.*;
import pw.react.backend.web.*;
import pw.react.backend.models.User;


import java.time.Duration;
import java.util.*;
import java.time.temporal.ChronoUnit.*;
import org.springframework.security.core.Authentication;

import javax.print.attribute.standard.Media;
import javax.swing.text.html.parser.Entity;

@RestController
@RequestMapping(path = ManageController.MANAGE_PATH)
public class ManageController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String MANAGE_PATH = "/manage";

    private final UserService userService;
    private final CarService carService;
    private final FavoriteCarService favoriteCarService;
    private ImageService carImageService;
    private BookingService bookingService;
    private PaymentService paymentService;
    public ManageController(UserService userService,CarService carService,FavoriteCarService favoriteCarService
                            ,BookingService bookingService,PaymentService paymentService) {
        this.userService = userService;
        this.carService=carService;
        this.favoriteCarService=favoriteCarService;
        this.bookingService=bookingService;
        this.paymentService=paymentService;
    }
    @Autowired
    public void setCarImageService(ImageService carImageService)
    {
        this.carImageService=carImageService;
    }

    @Operation(summary = "Create new car for admin")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Car created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = CarDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PostMapping(path = "/cars")
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto carInput,Authentication auth) {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(
                ()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin())
            throw  new UnauthorizedException("only admin can access",MANAGE_PATH+"/cars");
        try {
            Car car=CarDto.ConvertToCar(carInput);
            car.setOwnerId(us.getId());
            car= carService.save(car);
            if(car==null)
                throw new Exception("car already exists");
            return ResponseEntity.status(HttpStatus.CREATED).body(CarDto.valueFrom(car));
        } catch (Exception ex) {
            throw new ResourceNotFoundException(ex.getMessage());
        }
    }
    @Operation(summary = "delete  car for admin")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Car deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @DeleteMapping(path = "/cars/{carId}")
    public ResponseEntity<String> deleteCar(@RequestHeader HttpHeaders headers, @PathVariable Long carId,Authentication auth) {
        Car car=carService.getById(carId).orElseThrow(()->new ResourceNotFoundException("car with given id doesnt exists"));
        User us=userService.FindByUserName(auth.getName()).orElseThrow(
                ()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin()||us.getId()!=car.getOwnerId())
            throw  new UnauthorizedException("only admin of given car can access",MANAGE_PATH+"/cars");
        boolean deleted = carService.deleteCar(carId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(String.format("Car with id %s does not exist.", carId));
        }
        return ResponseEntity.ok(String.format("Car with id %s deleted.", carId));
    }
    @Operation(summary = "Update car for admin")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Car updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = CarDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PutMapping(path = "/cars/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCar(@RequestHeader HttpHeaders headers, @PathVariable Long carId,
                              @Valid @RequestBody CarDto updatedCar,Authentication auth) {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(
                ()->new ResourceNotFoundException("user doesnt exists"));
        Car oldcar=carService.getById(carId).orElseThrow(()->new ResourceNotFoundException("car doesnt exists"));
        if(!us.isAdmin()||us.getId()!=oldcar.getOwnerId())
            throw  new UnauthorizedException("only admin can access",MANAGE_PATH+"/cars");
        Car newCar=CarDto.ConvertToCar(updatedCar);
        newCar.setOwnerId(us.getId());
        carService.updateCar(carId,newCar);
    }

    @Operation(summary = "add image to car")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "image added",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = UploadFileResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @PostMapping(path ="/cars/{carId}/image",
    consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public  ResponseEntity<UploadFileResponse> uploadLogo(Authentication auth,@RequestHeader HttpHeaders headers,
                                                          @PathVariable Long carId,
                                                          @RequestParam("file") MultipartFile file) {
        try {
            User us = userService.FindByUserName(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("user doesnt exists"));
            Car car = carService.getById(carId).orElseThrow(() -> new ResourceNotFoundException("car doest exists"));
            if (!us.isAdmin() || car.getOwnerId() != us.getId())
                throw new IllegalAccessException("cant access this car ");

            CarImage carImage = carImageService.sotreImage(carId, file);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/cars/" + carId + "/image/")
                    .path(carImage.getFileName())
                    .toUriString();
            UploadFileResponse response = new UploadFileResponse(
                    carImage.getFileName(),
                    fileDownloadUri,
                    file.getContentType(),
                    file.getSize()

            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),MANAGE_PATH+"/cars");
        }
    }
    @Operation(summary = "get car imgae")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Car image",
                    content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = Resource.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping(value = "/cars/{carId}/image")
    public ResponseEntity<Resource> getImg(Authentication auth,@RequestHeader HttpHeaders headers, @PathVariable Long carId) {
        try {
            User us = userService.FindByUserName(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("user doesnt exists"));
            Car car = carService.getById(carId).orElseThrow(() -> new ResourceNotFoundException("car doest exists"));
            if (!us.isAdmin() || car.getOwnerId() != us.getId())
                throw new IllegalAccessException("cant access this car ");


            CarImage carImage = carImageService.getCarImage(carId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(carImage.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + carImage.getFileName() + "\"")
                .body(new ByteArrayResource(carImage.getData()));
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),MANAGE_PATH+"/cars");
        }
    }
    @Operation(summary = "delete car image")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Car image deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @DeleteMapping(value = "/cars/{carId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeimage(@RequestHeader HttpHeaders headers, @PathVariable Long carId) {

        carImageService.deleteCarImage(carId);
    }
    @Operation(summary = "get all admin cars")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cars",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = CarDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/cars")
    public ResponseEntity<List<Map<String,Object>>> getAllCars(Authentication auth,@RequestParam("page") int page)
    {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(
                ()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin())
            throw  new UnauthorizedException("only admin can access",MANAGE_PATH+"/cars");
        try
        {

            List<CarDto> cars=carService.getAllbyOwner(us.getId(),page).stream().map(CarDto::valueFrom).toList();
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
            throw new UserValidationException(ex.getMessage(),MANAGE_PATH+"/cars");
        }

    }
    @Operation(summary = "get all users")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "usres",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = UserDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/users")
    ResponseEntity<Collection<UserDto>> getUsers(Authentication auth,@RequestParam int page)
    {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin())
            throw new UnauthorizedException("only admins can access this endpoint",MANAGE_PATH);
        try
        {
            Collection<UserDto> usrs=userService.GetAllNonAdmin(page).stream().map(UserDto::valueFrom).toList();
            return ResponseEntity.ok(usrs);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),MANAGE_PATH+"/users");
        }
    }
    @Operation(summary = "get all bookings ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "bookings",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = BookingDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/bookings")
    ResponseEntity<Collection<BookingDto>> getBookings(Authentication auth, @RequestParam int page)
    {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin())
            throw new UnauthorizedException("only admins can access this endpoint",MANAGE_PATH);
        try
        {
            List<Long> ids=carService.getByOwnerId(us.getId()).stream().map(car -> car.getId()).toList();
            Collection<BookingDto> bookingDtos=bookingService.getAllOwnersOrderedByDate(ids,page).stream().map(
                    BookingDto::valueFrom).toList();
            return ResponseEntity.ok(bookingDtos);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),MANAGE_PATH+"/bookings");
        }
    }
    @Operation(summary = "canel given booking")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "booking canceled"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @DeleteMapping("/bookings")
    ResponseEntity<Void> deleteBookign(Authentication auth,@RequestParam long bookingid)
    {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin())
            throw new UnauthorizedException("only admins can access this endpoint",MANAGE_PATH);
        try
        {
            Booking booking=bookingService.getById(bookingid).orElseThrow(()->new ResourceNotFoundException("booking with given id doesnt exists"));
            Car car=carService.getById(booking.getCarId()).orElseThrow(()->new ResourceNotFoundException("car with given id doesnt exists"));
            if(car.getOwnerId()!=us.getId())
                throw new UnauthorizedException("Admin can only edit his own cars",MANAGE_PATH);
            long days= Duration.between(booking.getEndDate(), booking.getStartDate()).toDays();
            Long cost=car.getDailyPrice()*days;
            User user=userService.findById(booking.getUserId()).orElseThrow(()->new ResourceNotFoundException("invalid booking"));
            Long prev=user.getBalance();
            user.setBalance(prev+cost);
            userService.saveEdited(user);

             boolean b=bookingService.deleteBooking(bookingid);
             if(b)
                 return ResponseEntity.ok(null);
             else
                 throw new Exception("something went wrong");
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),MANAGE_PATH+"/bookings");
        }
    }
    @Operation(summary = "get all paymnets ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "paymnets",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = PaymentDto.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Something went wrong"
            )
    })
    @GetMapping("/payments")
    ResponseEntity<Collection<PaymentDto>> getAllPayments(Authentication auth,@RequestParam int page)
    {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin())
            throw new UnauthorizedException("only admins can access this endpoint",MANAGE_PATH);
        try
        {
            List<PaymentDto> paymentDtos=paymentService.getAllOrderedByDate(page).stream().map(PaymentDto::valueFrom)
                    .toList();
            return ResponseEntity.ok(paymentDtos);

        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),MANAGE_PATH+"/bookings");
        }
    }
}
