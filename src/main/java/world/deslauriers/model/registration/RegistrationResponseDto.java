package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.model.profile.ProfileDto;

@Serdeable
public record RegistrationResponseDto(
        Integer status,
        @Nullable String error,
        String message,
        String path
) {
}
