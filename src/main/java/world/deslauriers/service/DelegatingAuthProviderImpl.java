package world.deslauriers.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class DelegatingAuthProviderImpl implements AuthenticationProvider {

    @Inject
    private final UserService userService;

    @Inject
    private final PasswordEncoderService passwordEncoderService;

    public DelegatingAuthProviderImpl(UserService userService, UserRepository userRepository, PasswordEncoderService passwordEncoderService) {
        this.userService = userService;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authenticationRequest) {

        return Flux.create(emitter -> {

                    var user = fetchUser(authenticationRequest);
                    var authFailed = validate(user, authenticationRequest);
                    if (authFailed.isPresent()){

                        emitter.error(new AuthenticationException(authFailed.get()));
                    } else {

                        emitter.next(createSuccessfulAuthResponse(user.get()));
                        emitter.complete();
                    }
                }, FluxSink.OverflowStrategy.ERROR);
    }

    // get user from db if exist
    private Optional<User> fetchUser(AuthenticationRequest authenticationRequest) {

        final String username = authenticationRequest.getIdentity().toString();
        return userService.lookupUserByUsername(username);
    }

    // check user is enabled, etc, + pw matches != false
    private Optional<AuthenticationFailed> validate(
            Optional<User> user, AuthenticationRequest authenticationRequest){

        AuthenticationFailed authenticationFailed = null;
        if (user.isEmpty()){

            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.USER_NOT_FOUND);
        } else if (!user.get().enabled()) {

            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.USER_DISABLED);
        } else if (user.get().accountExpired()){

            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.ACCOUNT_EXPIRED);
        } else if (user.get().accountLocked()){

            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.ACCOUNT_LOCKED);
        } else if (!passwordEncoderService.matches(
                authenticationRequest.getSecret().toString(), user.get().password())){

            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }

        return Optional.ofNullable(authenticationFailed);
    }

    // successful login
    private AuthenticationResponse createSuccessfulAuthResponse(User user) {

        List<String> authorities = new ArrayList<>();
        user.userRoles().forEach(userRole -> {

            authorities.add(userRole.role().role());
        });

        return AuthenticationResponse.success(user.username(), authorities);
    }
}
