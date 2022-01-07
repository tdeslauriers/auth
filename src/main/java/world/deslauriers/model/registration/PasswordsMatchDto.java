package world.deslauriers.model.registration;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import world.deslauriers.validation.PasswordComplexity;

import javax.validation.constraints.NotBlank;

@Introspected
public record PasswordsMatchDto(
        @NonNull @NotBlank @PasswordComplexity String password,
        @NonNull @NotBlank @PasswordComplexity String confirmPassword
) {
}
