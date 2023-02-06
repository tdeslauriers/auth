package world.deslauriers.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;

import java.util.HashSet;

public interface RoleService {


    Mono<Role> getRole(String role);

    Mono<Role> getById(Long id);

    Flux<Role> getAllRoles();

    Mono<Role> update(Role role);

    Mono<Role> save(Role role);

    void resolveRoles(HashSet<Role> roles, User user);
}
