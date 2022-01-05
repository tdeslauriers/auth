package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

@Introspected
public record RegistrationResponseDto(
        Integer status,
        @Nullable String error,
        String message,
        String path
) {}
