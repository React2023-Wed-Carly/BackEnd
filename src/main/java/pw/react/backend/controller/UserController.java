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
import pw.react.backend.models.Car;
import pw.react.backend.services.CarMainService;
import pw.react.backend.services.CarService;
import pw.react.backend.services.FavoriteCarService;
import pw.react.backend.services.UserService;
import pw.react.backend.web.UserDto;
import pw.react.backend.models.User;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import javax.swing.text.html.parser.Entity;


@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String USERS_PATH = "/users";


    private final UserService userService;
    private final CarService carService;
    private final FavoriteCarService favoriteCarService;

    public UserController(UserService userService,CarService carService,FavoriteCarService favoriteCarService) {
        this.userService = userService;
        this.carService=carService;
        this.favoriteCarService=favoriteCarService;
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
            User us= userService.validateAndSave(UserDto.convertToUser(user));
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
    @PostMapping("/favorites/")

    public ResponseEntity<Void> AddFavorite(@RequestParam("carId") Long carId,Authentication auth)
    {
        try
        {
            User us=userService.FindByUserName(auth.getName()).orElseThrow(
                    ()->new ResourceNotFoundException("user doesnt exists"));
            if(us.getId()==1L)
                log.info(us.getId().toString());
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
    @DeleteMapping("/favorites/")
    public ResponseEntity<Void> deleteFavorite(@RequestParam("carId") Long carId,Authentication auth)
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
    @GetMapping("/favorites")
    public ResponseEntity<Object> getFavorites(Authentication auth)
    {
        try
        {
            User us=userService.FindByUserName(auth.getName()).orElseThrow(
                    ()->new ResourceNotFoundException("user doesnt exists"));

            List<Long> carids=favoriteCarService.getAllbyUser(us.getId()).stream()
                    .map((favoriteCars -> favoriteCars.getCarId())).toList();
            Collection<Car> cars=carService.getbyIdIn(carids);
            for (Car c:cars
            ) {
                log.info(c.getId().toString());
            }
            return ResponseEntity.ok(null);
        }
        catch (Exception ex)
        {
            throw new UserValidationException(ex.getMessage(),USERS_PATH+"/favorites");
        }

    }
}
