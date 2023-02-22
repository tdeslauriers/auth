package world.deslauriers.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Singleton
public class DelegatingAuthProviderImpl implements AuthenticationProvider {
    private final UserService userService;
    private final PasswordEncoderService passwordEncoderService;

    public DelegatingAuthProviderImpl(UserService userService, PasswordEncoderService passwordEncoderService) {
        this.userService = userService;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authenticationRequest) {

        return Flux.create(emitter -> {
                    var user = fetchUser(authenticationRequest).block();
                    var authFailed = validate(user, authenticationRequest);
                    if (authFailed != null){
                        emitter.error(new AuthenticationException(authFailed));
                    } else {
                        emitter.next(createSuccessfulAuthResponse(user)); // null check in validate
                        emitter.complete();
                    }
                }, FluxSink.OverflowStrategy.ERROR);
    }

    // get user from db if exist
    private Mono<User> fetchUser(AuthenticationRequest authenticationRequest) {
        final String username = authenticationRequest.getIdentity().toString();
        return userService.getUserByUsername(username);
    }

    // check user is enabled, etc, + pw matches != false
    private AuthenticationFailed validate(User user, AuthenticationRequest authenticationRequest){
        AuthenticationFailed authenticationFailed = null;
        if (user == null){
            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.USER_NOT_FOUND);

        } else if (!user.enabled()) {
            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.USER_DISABLED);

        } else if (user.accountExpired()){
            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.ACCOUNT_EXPIRED);

        } else if (user.accountLocked()){
            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.ACCOUNT_LOCKED);

        } else if (!passwordEncoderService.matches(authenticationRequest.getSecret().toString(), user.password())){
            authenticationFailed = new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }

        return authenticationFailed;
    }

    // successful login
    private AuthenticationResponse createSuccessfulAuthResponse(User user) {

        List<String> authorities = new ArrayList<>();
        user.userRoles().forEach(userRole -> {
            authorities.add(userRole.role().role());
        });

        var attributes = new HashMap<String, Object>();
        attributes.put("firstname", user.firstname());
        attributes.put("lastname", user.lastname());
        attributes.put("user_uuid", user.uuid());

        return AuthenticationResponse.success(user.username(), authorities, attributes);
    }
}
