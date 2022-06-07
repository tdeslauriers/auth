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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.service.UserService;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/auth/profiles")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @Inject
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // users
    @Get("/user")
    public Optional<ProfileDto> getProfile(Principal principal){

        // may only get your own record.
        return userService.getProfile(principal.getName());
    }

    @Put("/user")
    public HttpResponse updateProfile(@Body @Valid ProfileDto updatedProfile, Principal principal){

        var allowed = userService.lookupUserByUsername(principal.getName());

        if (allowed.isEmpty()){
            log.warn("Attempt to edit non-existent user: " + principal.getName());
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body("User does not exist.");
        }

        // may only update your own record.
        userService.updateUser(allowed.get(), new ProfileDto(
                allowed.get().id(),
                allowed.get().username(),
                updatedProfile.firstname(),
                updatedProfile.lastname(),
                allowed.get().dateCreated(),
                allowed.get().enabled(),
                allowed.get().accountExpired(),
                allowed.get().accountLocked(),
                updatedProfile.roles(),
                updatedProfile.addresses(),
                updatedProfile.phones()));

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, URI.create("/user").getPath());
    }

    // admin
    @Secured({"PROFILE_ADMIN"})
    @Get("/all")
    public Iterable<ProfileDto> getAll(){

        return userService.getAllUsers();
    }

    @Secured({"PROFILE_ADMIN"})
    @Get("/{id}")
    public Optional<ProfileDto> getById(Long id){

        return userService.getProfileById(id);
    }

    @Secured({"PROFILE_ADMIN"})
    @Put("/edit")
    public HttpResponse updateUser(@Body @Valid ProfileDto updatedProfile){

        var user = userService.lookupUserById(updatedProfile.id());

        if (user.isEmpty()){
            log.error("Attempt to edit invalid User Id: " + updatedProfile.id() + " - does not exist.");
            throw new IllegalArgumentException("Invalid user Id.");
        }

        return HttpResponse.ok().body(userService.updateUser(user.get(), updatedProfile).get());
    }
}
