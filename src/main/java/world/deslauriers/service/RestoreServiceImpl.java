package world.deslauriers.service;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import world.deslauriers.controller.BackupUser;
import world.deslauriers.model.database.User;

@Singleton
public class RestoreServiceImpl implements RestoreService {

    private static final Logger log = LoggerFactory.getLogger(RestoreServiceImpl.class);

    private final UserService userService;

    public RestoreServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<?> restoreUser(BackupUser backupUser) {

        // TODO: add decryption/type-conversion logic after initial data restore.
        // temp
        var user = new User(
                backupUser.id(),
                backupUser.username(),
                backupUser.password(),
                backupUser.firstname(),
                backupUser.lastname(),
                backupUser.dateCreated(),
                backupUser.enabled(),
                backupUser.accountExpired(),
                backupUser.accountLocked(),
                backupUser.birthday(),
                backupUser.uuid()
        );
        return userService.saveUser(user);
    }
}
