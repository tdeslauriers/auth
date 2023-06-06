package world.deslauriers.service;

import reactor.core.publisher.Mono;
import world.deslauriers.controller.BackupUser;
import world.deslauriers.model.database.User;

public interface RestoreService {
    Mono<?> restoreUser(BackupUser backupUser);
}
