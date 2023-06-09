package world.deslauriers.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Serdeable
public record BackupUser(
        Long id,
        String username,
        String password,
        String firstname,
        String lastname,
        String dateCreated,
        Boolean enabled,
        Boolean accountExpired,
        Boolean accountLocked,
        @Nullable String birthday,
        String uuid
) {
}
