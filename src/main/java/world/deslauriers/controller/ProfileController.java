package world.deslauriers.controller;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.dto.ProfileDto;
import world.deslauriers.model.dto.RemoveUserRoleCmd;
import world.deslauriers.model.dto.ResetPasswordCmd;
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

        // may only update your own record.
        // may only update specific fields.
        return userService.getUserByUsername(principal.getName())
                .flatMap(user -> userService.updateUser(user, new ProfileDto(
                        user.id(),
                        user.username(),
                        updatedProfile.firstname(),
                        updatedProfile.lastname(),
                        user.dateCreated(),
                        user.enabled(),
                        user.accountExpired(),
                        user.accountLocked(),
                        updatedProfile.birthday(),
                        user.uuid(),
                        null,  // user not allowed to update roles.
                        updatedProfile.addresses(),
                        updatedProfile.phones())))
                .thenReturn(HttpResponse
                        .noContent()
                        .header(HttpHeaders.LOCATION, URI.create("/user").getPath()));
    }

    @Post("/reset")
    Mono<HttpResponse<?>> resetPassword(@Body @Valid ResetPasswordCmd cmd, Principal principal){

        // can only reset your own pw.
        // complexity standards will be executed by @Valid
        return userService.getUserByUsername(principal.getName())
                .flatMap(user -> userService.resetPassword(user, cmd))  // exceptions handled/thrown in service.
                .thenReturn(HttpResponse.noContent());

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

        return userService.getUserById(updatedProfile.id())
                .flatMap(user -> userService.updateUser(user, updatedProfile))
                .thenReturn(HttpResponse
                        .noContent()
                        .header(HttpHeaders.LOCATION, URI.create("/edit").getPath()));
    }

    // Deletes xref, technically updating user record => Put
    // request body needed.
    @Secured({"PROFILE_ADMIN"})
    @Put("/remove/userrole")
    Mono<HttpResponse<?>> removeUserRole(@Body @Valid RemoveUserRoleCmd cmd){
        return userService.removeUserRole(cmd)
                .thenReturn(HttpResponse
                        .noContent()
                        .header(HttpHeaders.LOCATION, URI.create("/remove/userrole").getPath()));
    }
}
