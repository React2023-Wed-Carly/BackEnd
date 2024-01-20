package pw.react.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.models.Car;
import pw.react.backend.models.CarImage;
import pw.react.backend.models.User;
import pw.react.backend.services.CarService;
import pw.react.backend.services.ImageService;
import pw.react.backend.services.UserService;
import pw.react.backend.web.CarDto;
import pw.react.backend.web.UploadFileResponse;
import pw.react.backend.web.UserDto;

import java.util.*;

import org.springframework.security.core.Authentication;
@RestController
@RequestMapping(path = CarController.CARS_PATH)
public class CarController {
    public final static String CARS_PATH = "/cars";
    private static final Logger log = LoggerFactory.getLogger(CarController.class);
    @Autowired
    private final CarService carService;
    @Autowired
    private final UserService userService;
    private ImageService carImageService;

    @Autowired
    private void setCarImageService(ImageService carImageService){this.carImageService=carImageService;}

    public CarController(CarService carService,UserService userService) {
        this.carService = carService;
        this.userService=userService;
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
}
