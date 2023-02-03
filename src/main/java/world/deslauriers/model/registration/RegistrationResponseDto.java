package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RegistrationResponseDto(
        Integer status,
        @Nullable String error,
        String message,
        String path
) {}
