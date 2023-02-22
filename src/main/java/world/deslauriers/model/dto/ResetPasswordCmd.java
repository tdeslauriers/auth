package world.deslauriers.model.dto;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.validation.PasswordComplexity;

import javax.validation.constraints.NotBlank;

@Serdeable
public record ResetPasswordCmd(

        @NonNull @NotBlank @PasswordComplexity String current,
        @NonNull @NotBlank @PasswordComplexity String updated,
        @NonNull @NotBlank @PasswordComplexity String confirm
) {
}
