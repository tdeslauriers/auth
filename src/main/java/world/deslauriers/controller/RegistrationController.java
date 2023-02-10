package world.deslauriers.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
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
    Mono<MutableHttpResponse<RegistrationResponseDto>> register(@Body @Valid RegistrationDto registrationDto){
        log.info("Registration controller fired.");
        return userService.registerUser(registrationDto)
                .map(registrationResponseDto -> {
                    if (!registrationResponseDto.status().equals(201)) {
                        log.warn("Registration failed.");
                        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(registrationResponseDto);
                    }
                    return HttpResponse.status(HttpStatus.CREATED).body(registrationResponseDto);
                });
    }

    @Post("/user-available")
    Mono<MutableHttpResponse<RegistrationResponseDto>> checkExistingUser(@Body @Valid ExistingUserDto existingUserDto){

        return userService.getUserByUsername(existingUserDto.username())
                .flatMap(user -> Mono.just(HttpResponse.status(HttpStatus.BAD_REQUEST)
                        .body(new RegistrationResponseDto(400, "Bad Request", "Username Unavailable", "/register"))))
                .switchIfEmpty(Mono.defer(() -> Mono.just(HttpResponse.ok())));
    }

    @Post("/valid-password")
    Mono<HttpResponse<?>> checkPasswordValid (@Body @Valid ValidPasswordDto validPasswordDto){

        // error will be thrown by @Valid annotations on dto
        return Mono.just(HttpResponse.ok());
    }

    @Post("/passwords-match")
    Mono<HttpResponse<?>> checkPasswordsMatch(@Body @Valid PasswordsMatchDto passwordsMatchDto){

        if (!passwordsMatchDto.password().equals(passwordsMatchDto.confirmPassword())) {
            var err = new RegistrationResponseDto(400, "Bad Request", "Passwords do not match.", "/register");
            return Mono.just(HttpResponse.status(HttpStatus.BAD_REQUEST).body(err));
        }
        return Mono.just(HttpResponse.ok());
    }

}
