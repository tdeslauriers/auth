package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Introspected
public record ValidPasswordDto(
        @NonNull @NotBlank @Size(min = 12, max = 64) String password) {}
