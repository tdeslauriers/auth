package world.deslauriers.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import world.deslauriers.model.profile.UserDto;
import world.deslauriers.service.UserService;

@Secured("ADMIN_PROFILE")
@ExecuteOn(TaskExecutors.IO)
@Controller("/admin/profiles")
public class AdminController {

    @Inject
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @Get
    public Iterable<UserDto> getAllProfiles(){

        return userService.getAllUsers();
    }
}
