package pw.react.backend.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.models.User;
import pw.react.backend.services.UserMainService;

import java.util.Collection;
import java.util.Collections;

public class UserBatchService extends UserMainService {

    private static final Logger log = LoggerFactory.getLogger(UserBatchService.class);
    private final BatchRepository<User> batchRepository;

    public UserBatchService(UserRepository userRepository, PasswordEncoder passwordEncoder, BatchRepository<User> batchRepository) {
        super(userRepository, passwordEncoder);
        this.batchRepository = batchRepository;
    }

    @Override
    public Collection<User> batchSave(Collection<User> users) {
        log.info("Batch insert.");
        if (users != null && !users.isEmpty()) {
            Collection<User> insertedUsers = batchRepository.insertAll(users.stream()
                    .peek(it -> it.setPassword(passwordEncoder.encode(it.getPassword())))
                    .toList()
            );
            return userRepository.findAllByUsernameIn(insertedUsers.stream().map(User::getUsername).toList());

        } else {
            log.warn("User collection is empty or null.");
            return Collections.emptyList();
        }
    }
}
