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
import pw.react.backend.models.Car;
import pw.react.backend.models.User;
import pw.react.backend.services.CarService;
import pw.react.backend.services.UserService;
import pw.react.backend.web.CarDto;
import pw.react.backend.web.UserDto;

import java.util.Collection;
import java.util.Optional;

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

    public CarController(CarService carService,UserService userService) {
        this.carService = carService;
        this.userService=userService;
    }
@Operation(summary = "get cars by owner id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ok",
                    content = {@Content(mediaType = "application/json", schema = @Schema(allOf = CarDto.class))}
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Something went wrong"
            )
    })
    @GetMapping(path = "/")
    public ResponseEntity<Collection<CarDto>> GetOwnersCars(@RequestParam("OwnerId") long ownerId,Authentication auth ) {
        try {

            Optional<User> us=userService.FindByUserName(auth.getName());
            if(!us.isPresent())
                throw  new UserValidationException("cant find user with given token");
            else
            {
                if(us.get().getId()!=ownerId)
                 throw new UserValidationException("cant access this car, u r not the owner");
                log.info(us.toString());
            }
            Collection<CarDto> Cars = carService.getByOwnerId(ownerId).stream().map(CarDto::valueFrom).toList();

            return ResponseEntity.ok(Cars);

        } catch (Exception ex) {
            throw new ResourceNotFoundException(ex.getMessage()+" "+CarController.CARS_PATH);
        }
    }

}
