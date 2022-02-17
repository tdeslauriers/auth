package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.repository.AddressRepository;
import world.deslauriers.repository.UserAddressRepository;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AddressServiceTest {

    @Inject private AddressRepository addressRepository;
    @Inject private AddressService addressService;
    @Inject private UserService userService;
    @Inject private UserAddressRepository userAddressRepository;

    @Test
    void testAddressServiceMethods(){

        // from test data
        var user = userService.lookupUserByUsername("admin@deslauriers.world").get();
        var userAddresses = userAddressRepository.findByUser(user);
        assertTrue(userAddresses.iterator().hasNext());
        assertEquals(2, ((Collection<?>) userAddresses).size());

        var address = addressRepository.findByAddress(
                userAddresses.iterator().next().address().address(),
                userAddresses.iterator().next().address().city(),
                userAddresses.iterator().next().address().state(),
                userAddresses.iterator().next().address().zip());
        assertTrue(address.isPresent());

        // edit existing
        var addresses = new HashSet<Address>();
        addresses.add(new Address(
                userAddresses.iterator().next().id(),
                "111 New Address Street",
                userAddresses.iterator().next().address().city(),
                userAddresses.iterator().next().address().state(),
                userAddresses.iterator().next().address().zip()));
        addressService.resolveAddresses(addresses, user);
        userAddresses = userAddressRepository.findByUser(user);
        assertEquals(2, ((Collection<?>) userAddresses).size());
        assertEquals("111 New Address Street", ((Collection<UserAddress>) userAddresses)
                .stream()
                .filter(userAddress -> userAddress.address().address().equals("111 New Address Street"))
                .findFirst().get().address().address());

        // add new address to existing
        addresses.clear();
        addresses.add(new Address("789 Condo Lane", "Cityopolis", "DC", "33333"));
        addressService.resolveAddresses(addresses, user);

        userAddresses = userAddressRepository.findByUser(user);
        assertTrue(userAddresses.iterator().hasNext());
        assertEquals(3, ((Collection<?>) userAddresses).size());

        var added = ((Collection<UserAddress>) userAddresses)
                .stream()
                .filter(userAddress -> userAddress.address().zip().equals("33333"))
                .findFirst().get().address();
        assertEquals("33333", added.zip());

        // attempt to add duplicate
        // repeats above since it should already exist
        var thrown = assertThrows(IllegalArgumentException.class, () -> {
            addressService.resolveAddresses(addresses, user);
        });
        assertEquals("Cannot add duplicate addresses.", thrown.getMessage());

        // add existing address w/ no addresses associated.
        // remove associations
        userAddresses.forEach(userAddress -> userAddressRepository.delete(userAddress));
        userAddresses = userAddressRepository.findByUser(user);
        assertFalse(userAddresses.iterator().hasNext());

        addressService.resolveAddresses(addresses, user);
        userAddresses = userAddressRepository.findByUser(user);
        assertTrue(userAddresses.iterator().hasNext());
        assertEquals(1, ((Collection<?>)userAddresses).size());
        assertEquals(added.id(), userAddresses.iterator().next().address().id());

        // add new address w/ no addresses associated.
        // remove associations
        userAddresses.forEach(userAddress -> userAddressRepository.delete(userAddress));
        userAddresses = userAddressRepository.findByUser(user);
        assertFalse(userAddresses.iterator().hasNext());

        addresses.clear();
        addresses.add(new Address("222 No Path", "Nowheresville", "DC", "33333"));
        addressService.resolveAddresses(addresses, user);
        userAddresses = userAddressRepository.findByUser(user);
        assertTrue(userAddresses.iterator().hasNext());
        assertEquals(1, ((Collection<?>) userAddresses).size());
        assertNotNull(userAddresses.iterator().next().address().id());
        assertEquals("222 No Path", userAddresses.iterator().next().address().address());

        // fail validation
        addresses.clear();
        addresses.add(new Address("", "Nowheresville", "DC", "33333"));

        var fail = assertThrows(ConstraintViolationException.class, () -> {
            addressService.resolveAddresses(addresses, user);
        });
        assertEquals("save.entity.address: must not be blank", fail.getMessage());
    }

}
