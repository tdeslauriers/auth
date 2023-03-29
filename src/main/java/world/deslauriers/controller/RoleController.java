package world.deslauriers.controller;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.dto.RemoveUserRoleCmd;
import world.deslauriers.model.dto.RoleDto;
import world.deslauriers.service.RoleService;

import javax.validation.Valid;
import java.net.URI;

@Secured({"PROFILE_ADMIN"})
@Controller("/roles")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Get
    Flux<Role> getAllRoles(){
        return roleService.getAllRoles();
    }

    @Get("/{id}")
    Mono<Role> getById(Long id){
        return roleService.getById(id);
    }

    @Put
    Mono<HttpResponse<?>> updateRole(@Body @Valid RoleDto cmd){
        return roleService.update(cmd)
                .map(r -> HttpResponse
                        .noContent()
                        .header(HttpHeaders.LOCATION, location(r.id()).getPath()));
    }

    @Post
    Mono<HttpResponse<Role>> save(@Valid @Body RoleDto cmd){
        return roleService.save(cmd)
            .map(r -> HttpResponse
                    .created(r)
                    .headers(headers -> headers.location(location(r.id()))));
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    Mono<Void> delete(long id){
        return roleService.deleteRole(id).then();
    }

    protected URI location(Long id) {
        return URI.create("/roles/" + id);
    }

    protected URI location(Role role) {
        return location(role.id());
    }
}
