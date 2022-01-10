package world.deslauriers.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
public class UserDaoTest {

    @Inject
    private UserRepository userRepository;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "2nd_Worst_password_ever!";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";

    @Test
    void testUserCrud(){

        var dateCreated = LocalDate.now();
        // checking output format
        System.out.println(dateCreated); // 2021-12-28

        var user = new User(VALID_EMAIL, VALID_CLEAR_PASSWORD, VALID_FIRST, VALID_LAST, dateCreated, true, false, false);
        user = userRepository.save(user);
        assertNotNull(user.id());

        user = new User(user.id(), VALID_EMAIL, "This_1_is_bad_2!", VALID_FIRST, VALID_LAST, dateCreated, true, false, false);
        var checkId = user.id();
        user = userRepository.update(user);
        assertNotNull(user.id());
        assertEquals(checkId, user.id());
        assertEquals("This_1_is_bad_2!", user.password());
    }
}
