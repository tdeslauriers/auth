package world.deslauriers.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.profile.UserDto;
import world.deslauriers.service.UserService;

import java.security.Principal;

@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/profiles")
public class ProfileController {

    @Inject
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // users
    @Get("/user")
    public ProfileDto getprofile(Principal principal){

        return userService.getProfile(principal.getName()).orElse(null);
    }

    // admin
    @Secured({"PROFILE_ADMIN"})
    @Get("/all")
    public Iterable<UserDto> getAll(){

        return userService.getAllUsers();
    }
}
