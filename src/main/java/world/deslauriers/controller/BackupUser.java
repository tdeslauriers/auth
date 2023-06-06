package world.deslauriers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.serde.annotation.Serdeable;
import world.deslauriers.validation.LettersOnly;
import world.deslauriers.validation.PasswordComplexity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Serdeable
public record BackupUser(
        Long id,
        String username,
        String password,
        String firstname,
        String lastname,
        LocalDate dateCreated,
        Boolean enabled,
        Boolean accountExpired,
        Boolean accountLocked,
        LocalDate birthday,
        String uuid
) {
}
