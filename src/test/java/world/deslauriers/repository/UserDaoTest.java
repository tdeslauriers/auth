package world.deslauriers.repository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Phone;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserPhone;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class UserDaoTest {

    @Inject private UserRepository userRepository;
    @Inject private PhoneRepository phoneRepository;
    @Inject private UserPhoneRepository userPhoneRepository;

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

        var userphone = new UserPhone(user, phoneRepository.save(new Phone("6665554444", "cell")));
        userphone = userPhoneRepository.save(userphone);
        assertNotNull(userphone.id());
        assertNotNull(userphone.phone().id());

        user = userRepository.findByUsername(user.username()).get();
        assertNotNull(user.userRoles());
        assertEquals(1, user.userPhones().size());
        var phone = user.userPhones().stream().findFirst().get().phone().phone();
        assertEquals("6665554444", phone);

        // existing user
        var username = userRepository.findUsername(VALID_EMAIL);
        assertEquals(VALID_EMAIL, username.get());
        assertTrue(username.isPresent());
        assertFalse(username.isEmpty());

        // doesnt exist in db
        var notuser = userRepository.findUsername("doesnt@exist.com");
        assertTrue(notuser.isEmpty());
        assertFalse(notuser.isPresent());


    }
}

