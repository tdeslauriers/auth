package world.deslauriers.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.registration.*;
import world.deslauriers.service.UserService;

import javax.validation.Valid;

@Validated
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/register")
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @Post
    Mono<HttpResponse<ProfileDto>> register(@Body @Valid RegistrationDto registrationDto){

        if (!registrationDto.password().equals(registrationDto.confirmPassword())){
            var err = new RegistrationResponseDto(400, "Bad Request", "Passwords do not match", "/register");
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (userService.userExists(registrationDto.username())) {
            log.warn("Attempt to create existing user: " + registrationDto.username().substring(0,10));
            var err = new RegistrationResponseDto(400, "Bad Request", "Username Unavailable", "/register");
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(err);
        }

        try {
            String message = userService.registerUser(registrationDto);

            // this should return the message for account verification
            var success = new RegistrationResponseDto(201, null, message, "/register");
            return HttpResponse.status(HttpStatus.CREATED).body(success);
        } catch (Exception e){
            log.error("Registration attempt failed.", e.getMessage());
        }

        var err = new RegistrationResponseDto(400, "Bad Request", "Registration failed", "/register");
        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @Post("/user-available")
    Mono<HttpResponse<?>> checkExistingUser(@Body @Valid ExistingUserDto existingUserDto){

        if (userService.userExists(existingUserDto.username())) {
            log.warn("Attempt to create existing user: " + existingUserDto.username().substring(0,10));
            var err = new RegistrationResponseDto(400, "Bad Request", "Username Unavailable", "/register");
            return Mono.just(HttpResponse.status(HttpStatus.BAD_REQUEST).body(err));
        }
        return Mono.just(HttpResponse.ok());
    }

    @Post("/valid-password")
    Mono<HttpResponse<?>> checkPasswordValid (@Body @Valid ValidPasswordDto validPasswordDto){

        // error will be thrown by @Valid annotations on dto
        return Mono.just(HttpResponse.ok());
    }

    @Post("/passwords-match")
    Mono<HttpResponse<?>> checkPasswordsMatch(@Body @Valid PasswordsMatchDto passwordsMatchDto){

        if (!passwordsMatchDto.password().equals(passwordsMatchDto.confirmPassword())) {
            var err = new RegistrationResponseDto(400, "Bad Request", "Username Unavailable", "/register");
            return Mono.just(HttpResponse.status(HttpStatus.BAD_REQUEST).body(err));
        }
        return Mono.just(HttpResponse.ok());
    }

}
