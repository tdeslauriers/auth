package world.deslauriers.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.dto.RemoveUserRoleCmd;
import world.deslauriers.model.dto.RoleDto;

import java.util.HashSet;

public interface RoleService {


    Mono<Role> getRole(String role);

    Mono<Role> getById(Long id);

    Flux<Role> getAllRoles();

    Mono<Role> update(RoleDto cmd);

    Mono<Role> save(RoleDto cmd);

    void resolveRoles(HashSet<Role> roles, User user);

    Mono<Void> deleteRole(long id);

    Mono<Void> removeUserRole(RemoveUserRoleCmd cmd);
}
