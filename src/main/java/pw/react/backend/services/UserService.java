package pw.react.backend.services;

import pw.react.backend.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    User validateAndSave(User user);
    User updatePassword(User user, String password);
    Collection<User> batchSave(Collection<User> users);
    Collection<User> GetAll();
    Optional<User> FindByUserName(String username);
    User saveEdited(User user);
}
