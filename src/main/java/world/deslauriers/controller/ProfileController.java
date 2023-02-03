package world.deslauriers.controller;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.service.UserService;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/profiles")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // users
    @Get("/user")
    Mono<ProfileDto> getProfile(Principal principal){

        // may only get your own record.
        return userService.getProfile(principal.getName());
    }

    @Put("/user")
    Mono<HttpResponse<?>> updateProfile(@Body @Valid ProfileDto updatedProfile, Principal principal){

        var existing = userService.lookupUserByUsername(principal.getName());

        if (existing.isEmpty()){
            log.warn("Attempt to edit non-existent user: " + principal.getName());
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body("User does not exist.");
        }

        // may only update your own record.
        return userService.updateUser(existing.get(), new ProfileDto(
                existing.get().id(),
                existing.get().username(),
                updatedProfile.firstname(),
                updatedProfile.lastname(),
                existing.get().dateCreated(),
                existing.get().enabled(),
                existing.get().accountExpired(),
                existing.get().accountLocked(),
                updatedProfile.birthday(),
                existing.get().uuid(),
                null,  // user not allowed to update roles.
                updatedProfile.addresses(),
                updatedProfile.phones()))
                .thenReturn(HttpResponse
                        .noContent()
                        .header(HttpHeaders.LOCATION, URI.create("/user").getPath()));

    }

    // admin
    @Secured({"PROFILE_ADMIN"})
    @Get
    Flux<ProfileDto> getAll(){

        return userService.getAllUsers();
    }

    @Secured({"PROFILE_ADMIN", "PROFILE_READ"})
    @Get("/{uuid}")
    Mono<ProfileDto> getByUuid(String uuid){

        return userService.getProfileByUuid(uuid);
    }

    @Secured({"PROFILE_ADMIN"})
    @Put("/edit")
    Mono<HttpResponse<?>> updateUser(@Body @Valid ProfileDto updatedProfile){

        var user = userService.lookupUserById(updatedProfile.id());

        if (user.isEmpty()){
            log.error("Attempt to edit invalid User Id: " + updatedProfile.id() + " - does not exist.");
            throw new IllegalArgumentException("Invalid user Id.");
        }

        return HttpResponse.ok().body(userService.updateUser(user.get(), updatedProfile).get());
    }
}
