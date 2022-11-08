package world.deslauriers.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.User;
import world.deslauriers.service.UserService;

@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/backup")
public class BackupController {

    private static final Logger log = LoggerFactory.getLogger(BackupController.class);

    @Inject
    private final UserService userService;

    public BackupController(UserService userService) {
        this.userService = userService;
    }

    @Secured({"COLD_STORAGE"})
    @Get
    public Iterable<User> backup(){

        return userService.backupAll();
    }
}
