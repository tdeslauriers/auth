package world.deslauriers.service;

import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserAddress;

import java.util.HashSet;
import java.util.Optional;

public interface AddressService {

    void resolveAddresses(HashSet<Address> addresses, User user);

    void deleteAddress(UserAddress userAddress);

    Optional<UserAddress> findAddressUserXref(String username, Long addressId);
}
