package world.deslauriers.service;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Refresh;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static io.micronaut.security.errors.IssuingAnAccessTokenErrorCode.INVALID_GRANT;

@Singleton
public class RefreshTokenPersistenceImpl implements RefreshTokenPersistence {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenPersistenceImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshTokenPersistenceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Override
    public void persistToken(RefreshTokenGeneratedEvent event) {
        if (event != null &&
                event.getRefreshToken() != null &&
                event.getAuthentication() != null &&
                event.getAuthentication().getName() != null){
            refreshTokenRepository
                    .save(new Refresh(event.getAuthentication().getName(), event.getRefreshToken(), false, Instant.now()))
                    .subscribe();
        }
    }

    @Override
    public Publisher<Authentication> getAuthentication(String refreshToken) {
        return Flux.create(emitter -> {
            refreshTokenRepository.findByRefreshToken(refreshToken)
                    .switchIfEmpty(Mono.defer(() -> {
                        log.error("Refresh token not found.");
                        emitter.error(new OauthErrorResponseException(INVALID_GRANT, "Refresh token not found", null));
                        return Mono.empty();
                    }))
                    .subscribe(refresh -> {
                        if (refresh.revoked()){
                            log.error("{} attempted to use revoked refresh token", refresh.username());
                            emitter.error(new OauthErrorResponseException(INVALID_GRANT, "Refresh token revoked", null));
                        } else if (refresh.dateCreated().isBefore(Instant.now().minus(24, ChronoUnit.HOURS))) {
                            refreshTokenRepository.delete(refresh).subscribe();
                            log.error("{}'s refresh token expired.  Deleting from db.", refresh.username());
                            emitter.error(new OauthErrorResponseException(INVALID_GRANT, "Refresh token expired", null));
                        } else {
                            userService.getUserByUsername(refresh.username())
                                    .subscribe(u -> {
                                        emitter.next(buildAccessToken(u));
                                        emitter.complete();
                                    });
                        }
                    });
        }, FluxSink.OverflowStrategy.ERROR);
    }

    private Authentication buildAccessToken(User user){

        var authorities = user.userRoles()
                .stream()
                .map(userRole -> userRole.role().role())
                .toList();
        var attributes = new HashMap<String, Object>();
        attributes.put("firstname", user.firstname());
        attributes.put("lastname", user.lastname());
        attributes.put("user_uuid", user.uuid());

        return Authentication.build(user.username(), authorities, attributes);
    }
}
