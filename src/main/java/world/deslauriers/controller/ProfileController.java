package world.deslauriers.controller;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.profile.UserDto;
import world.deslauriers.model.registration.RegistrationResponseDto;
import world.deslauriers.service.UserService;

import javax.validation.Valid;
import java.net.URI;
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
    public ProfileDto getProfile(Principal principal){

        return userService.getProfile(principal.getName()).orElse(null);
    }

    // admin
    @Secured({"PROFILE_ADMIN"})
    @Get("/all")
    public Iterable<UserDto> getAll(){

        return userService.getAllUsers();
    }

    @Secured({"PROFILE_ADMIN"})
    @Put("/edit")
    public HttpResponse update(@Body @Valid ProfileDto updatedProfile){

        if (updatedProfile.id() == null){
            var err = new RegistrationResponseDto(400, "Bad Request", "User id required.", "/edit");
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(err);
        }

        userService.updateUser(updatedProfile);

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(updatedProfile.id()).getPath());
    }

    protected URI location(Long id){
        return URI.create("/profiles/edit/" + id);
    }
}
