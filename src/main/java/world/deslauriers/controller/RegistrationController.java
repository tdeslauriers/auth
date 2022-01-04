package world.deslauriers.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.registration.ExistingUserDto;
import world.deslauriers.model.registration.PasswordsMatchDto;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.registration.ValidPasswordDto;
import world.deslauriers.service.UserService;

import javax.validation.Valid;


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
    public HttpResponse register(@Body @Valid RegistrationDto registrationDto)
            throws HttpStatusException {

        if (!registrationDto.password().equals(registrationDto.confirmPassword())){
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        var existingUser = userService.lookupUserByUsername(registrationDto.username());

        if (existingUser.isPresent()){
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Username unavailable.");
        }

        try {
            String message = userService.registerUser(registrationDto);
            log.info(message);

            // this should return the email uuid message for account verification
            return HttpResponse.ok();
        } catch (Exception e){
            log.error("Registration attempt failed: " + registrationDto.username(), e.getMessage());
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Registration failure.");
    }

    @Post("/user-available")
    public HttpResponse checkExistingUser(@Body @Valid ExistingUserDto existingUserDto)
        throws HttpStatusException{

        if (userService.lookupUserByUsername(existingUserDto.username()).isPresent()) {

            log.warn("Attempt to register name already in system");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Username unavailable.");
        }
        return HttpResponse.ok();
    }

    @Post("/valid-password")
    public HttpResponse checkPasswordValid (@Body @Valid ValidPasswordDto validPasswordDto){

        // error will be thrown by @Valid annotations on dto
        return HttpResponse.ok();
    }

    @Post("/passwords-match")
    public HttpResponse checkPasswordsMatch(@Body @Valid PasswordsMatchDto passwordsMatchDto)
        throws HttpStatusException{

        if (!passwordsMatchDto.password().equals(passwordsMatchDto.confirmPassword()))
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        return HttpResponse.ok();
    }

}
