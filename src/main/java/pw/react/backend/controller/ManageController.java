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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UnauthorizedException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.Car;
import pw.react.backend.models.CarImage;
import pw.react.backend.services.*;
import pw.react.backend.web.CarDto;
import pw.react.backend.web.UploadFileResponse;
import pw.react.backend.web.UserDto;
import pw.react.backend.models.User;

import java.util.*;

import org.springframework.security.core.Authentication;

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
    public ManageController(UserService userService,CarService carService,FavoriteCarService favoriteCarService) {
        this.userService = userService;
        this.carService=carService;
        this.favoriteCarService=favoriteCarService;
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
            car= carService.save(car);
            if(car==null)
                throw new Exception("car already exists");
            return ResponseEntity.status(HttpStatus.CREATED).body(CarDto.valueFrom(car));
        } catch (Exception ex) {
            throw new ResourceNotFoundException(ex.getMessage());
        }
    }
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
    @PutMapping(path = "/cars/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCar(@RequestHeader HttpHeaders headers, @PathVariable Long carId,
                              @Valid @RequestBody CarDto updatedCar,Authentication auth) {
        User us=userService.FindByUserName(auth.getName()).orElseThrow(
                ()->new ResourceNotFoundException("user doesnt exists"));
        if(!us.isAdmin()||us.getId()!=updatedCar.ownerId())
            throw  new UnauthorizedException("only admin can access",MANAGE_PATH+"/cars");
        carService.updateCar(carId, CarDto.ConvertToCar(updatedCar));
    }

    @PostMapping(path ="/cars/{carId}/image")
    public  ResponseEntity<UploadFileResponse> uploadLogo(@RequestHeader HttpHeaders headers,
                                                          @PathVariable Long carId,
                                                          @RequestParam("file") MultipartFile file) {

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
    @GetMapping(value = "/cars/{carId}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getImg(@RequestHeader HttpHeaders headers, @PathVariable Long carId) {

        CarImage carImage = carImageService.getCarImage(carId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(carImage.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + carImage.getFileName() + "\"")
                .body(new ByteArrayResource(carImage.getData()));
    }
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
}
