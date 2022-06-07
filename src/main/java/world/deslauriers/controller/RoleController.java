package world.deslauriers.controller;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Role;
import world.deslauriers.service.RoleService;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@ExecuteOn(TaskExecutors.IO)
@Secured({"PROFILE_ADMIN"})
@Controller("/api/auth/roles")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    @Inject
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Get
    public Iterable<Role> getAllRoles(){

        return roleService.getAllRoles();
    }

    @Get("/{id}")
    public Optional<Role> getById(Long id){

        return roleService.getById(id);
    }

    @Put
    public HttpResponse updateRole(@Body @Valid Role role){

        var lookup = roleService.getById(role.id());

        if (lookup.isEmpty()){
            log.error("Attempt to edit invalid Role id: " + role.id() + " - does not exist.");
            throw new IllegalArgumentException("Invalid role id.");
        }

        var updated = roleService.update(role);
        log.info(lookup.get() + " edited to " + updated);

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(updated.id()).getPath());
    }

    @Post
    public HttpResponse<Role> save(@Valid @Body Role role){

        var add = roleService.save(role);
        log.info(add + " created.");

        return HttpResponse
                .created(add)
                .headers(headers -> headers.location(location(add.id())));
    }

    protected URI location(Long id) {
        return URI.create("/roles/" + id);
    }

    protected URI location(Role role) {
        return location(role.id());
    }
}
