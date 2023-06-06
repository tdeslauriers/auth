package world.deslauriers.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import world.deslauriers.service.RestoreService;

@Secured({"COLD_STORAGE"})
@Controller("/restore")
public class RestoreController {

    private static final Logger log = LoggerFactory.getLogger(RestoreController.class);

    private final RestoreService restoreService;

    public RestoreController(RestoreService restoreService) {
        this.restoreService = restoreService;
    }

    @Post("/user")
    public Mono<HttpResponse<?>> restoreUser(BackupUser backupUser){
        return restoreService
                .restoreUser(backupUser)
                .thenReturn(HttpResponse.noContent());
    }
}
