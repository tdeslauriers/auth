package world.deslauriers.model.dto;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RemoveUserRoleCmd(
        @NonNull Long roleId,
        @NonNull Long userId
) {
}
