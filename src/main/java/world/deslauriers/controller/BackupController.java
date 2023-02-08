package world.deslauriers.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import world.deslauriers.model.database.User;
import world.deslauriers.service.UserService;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/backup")
public class BackupController {

    private static final Logger log = LoggerFactory.getLogger(BackupController.class);

    private final UserService userService;

    public BackupController(UserService userService) {
        this.userService = userService;
    }

    @Secured({"COLD_STORAGE"})
    @Get
    public Flux<User> backup(){

        return userService.backupAll();
    }
}
