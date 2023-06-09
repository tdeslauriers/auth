package world.deslauriers.service;

import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.model.dto.BackupRole;
import world.deslauriers.model.dto.BackupUser;
import world.deslauriers.model.dto.BackupUserrole;

public interface RestoreService {
    Mono<User> restoreUser(BackupUser backupUser);

    Mono<Role> restoreRole(BackupRole backupRole);

    Mono<UserRole> restoreUserrole(BackupUserrole backupUserrole);
}
