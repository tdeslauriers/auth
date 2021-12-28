package world.deslauriers.model.database;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.jdbc.annotation.JoinTable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Introspected
@MappedEntity
public record Role(
        @Id @GeneratedValue Long id,
        @NotNull @NotBlank @Size(min = 2, max = 40) String role,

        @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "role")
        @JoinTable(name = "user_role")
        Set<UserRole> userRoles
) {
        public Role(String role) {
                this(null, role, null);
        }

        public Role(Long id, String role) {
                this(id, role, null);
        }
}
