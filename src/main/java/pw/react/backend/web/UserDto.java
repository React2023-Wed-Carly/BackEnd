package pw.react.backend.web;

import jakarta.validation.constraints.Email;
import pw.react.backend.models.User;

public record UserDto(Long id, String username, String password, @Email String email
,String firstname,String lastName,Long balance,Long DistanceTravelled,Boolean isAdmin) {

    public static UserDto valueFrom(User user) {
        return new UserDto(user.getId(), user.getUsername(), null, user.getEmail(),user.getFirstname(),
                user.getLastname(), user.getBalance(),user.getDistanceTravelled(), user.isAdmin());
    }

    public static User convertToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.id);
        user.setUsername(userDto.username);
        user.setEmail(userDto.email);
        user.setPassword(userDto.password);
        user.setBalance(userDto.balance);
        user.setFirstname(userDto.firstname);
        user.setLastname(userDto.lastName);
        user.setAdmin(userDto.isAdmin);
        user.setDistanceTravelled(userDto.DistanceTravelled);
        return user;
    }
}
