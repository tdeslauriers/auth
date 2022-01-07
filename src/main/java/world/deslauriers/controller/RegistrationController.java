package world.deslauriers.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.registration.*;
import world.deslauriers.service.UserService;

import javax.validation.Valid;

@Validated
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/register")
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @Inject
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @Post
    public HttpResponse register(@Body @Valid RegistrationDto registrationDto){

        if (!registrationDto.password().equals(registrationDto.confirmPassword())){
            var err = new RegistrationResponseDto(400, "Bad Request", "Passwords do not match", "/register");
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(err);
        }

        var existingUser = userService.lookupUserByUsername(registrationDto.username());

        if (existingUser.isPresent()){
            log.warn("Attempt to create existing user, User ID: " + existingUser.get().id());
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
    public HttpResponse checkExistingUser(@Body @Valid ExistingUserDto existingUserDto){

        var existingUser = userService.lookupUserByUsername(existingUserDto.username());

        if (existingUser.isPresent()) {
            log.warn("Attempt to create existing user, User ID: " + existingUser.get().id());
            var err = new RegistrationResponseDto(400, "Bad Request", "Username Unavailable", "/register");
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(err);
        }
        return HttpResponse.ok();
    }

    @Post("/valid-password")
    public HttpResponse checkPasswordValid (@Body @Valid ValidPasswordDto validPasswordDto){

        // error will be thrown by @Valid annotations on dto
        return HttpResponse.ok();
    }

    @Post("/passwords-match")
    public HttpResponse checkPasswordsMatch(@Body @Valid PasswordsMatchDto passwordsMatchDto){

        if (!passwordsMatchDto.password().equals(passwordsMatchDto.confirmPassword())) {
            var err = new RegistrationResponseDto(400, "Bad Request", "Username Unavailable", "/register");
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(err);
        }
        return HttpResponse.ok();
    }

}
