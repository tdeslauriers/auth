package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import world.deslauriers.validation.PasswordComplexity;

import javax.validation.constraints.NotBlank;

@Introspected
public record ValidPasswordDto(
        @NonNull @NotBlank @PasswordComplexity String password) {}
