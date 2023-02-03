package world.deslauriers.model.database;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static io.micronaut.data.annotation.Relation.Kind;

@Serdeable
@MappedEntity
public record Role(
        @Id @GeneratedValue Long id,
        @NotNull @NotBlank @Size(min = 2, max = 32) String role,
        @NotNull @NotBlank @Size(min = 2, max = 32) String title,
        @NotNull @NotBlank @Size(min = 2, max = 64) String description,

        @Relation(value = Kind.ONE_TO_MANY, mappedBy = "role")
        Set<UserRole> userRoles
) {
        public Role(String role, String title, String description) {
                this(null, role, title, description, null);
        }

        public Role(Long id, String role, String title, String description) {
                this(id, role, title, description, null);
        }
}
