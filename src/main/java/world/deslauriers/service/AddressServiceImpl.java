package world.deslauriers.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.repository.AddressRepository;
import world.deslauriers.repository.UserAddressRepository;

import java.util.HashSet;

@Singleton
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Inject
    private final AddressRepository addressRepository;

    @Inject
    private final UserAddressRepository userAddressRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserAddressRepository userAddressRepository) {
        this.addressRepository = addressRepository;
        this.userAddressRepository = userAddressRepository;
    }


    @Override
    public void resolveAddresses(HashSet<Address> addresses, User user) {

        // check if user has address(es)
        var userAddresses = userAddressRepository.findByUser(user);

        // user has no addresses
        if (!userAddresses.iterator().hasNext()){
            addresses.forEach(address -> {
                linkUserToAddress(address, user);
            });
        }

        // user has addresses
        if (userAddresses.iterator().hasNext()){
            addresses.forEach(address -> {
                // add
                if (address.id() == null){
                    userAddresses.forEach(userAddress -> {
                        if (address.address().equals(userAddress.address().address()) &&
                                address.city().equals(userAddress.address().city()) &&
                                address.state().equals(userAddress.address().state()) &&
                                address.zip().equals(userAddress.address().zip())){

                            log.error("Attempt to add duplicate address: " + address + " to user Id: " + user.id() + " - " + user.username());
                            throw new IllegalArgumentException("Cannot add duplicate addresses.");
                        }
                    });
                    linkUserToAddress(address, user);
                }

                // edit
                if (address.id() != null){
                    userAddresses.forEach(userAddress -> {
                        var sb = new StringBuilder();
                        if (address.id().equals(userAddress.address().id())){

                            if (!address.address().equals(userAddress.address().address())){
                                sb.append("\nAddress " + address.id() + "street address: " + userAddress.address().address() + " --> " + address.address());
                            }
                            if (!address.city().equals(userAddress.address().city())){
                                sb.append("\nAddress " + address.id() + "city: " + userAddress.address().city() + " --> " + address.city());
                            }
                            if (!address.state().equals(userAddress.address().state())){
                                sb.append("\nAddress " + address.id() + "state: " + userAddress.address().state() + " --> " + address.state());
                            }
                            if (!address.zip().equals(userAddress.address().zip())){
                                sb.append("\nAddress " + address.id() + "zip: " + userAddress.address().zip() + " --> " + address.zip());
                            }

                            if (sb.length() > 0){
                                var updated = addressRepository.update(new Address(
                                        address.id(),
                                        address.address(),
                                        address.city(),
                                        address.state(),
                                        address.zip()));
                                log.info("Updated user " + user.username() + " address: " + sb);
                            }
                        }
                    });
                }
            });
        }
    }

    private void linkUserToAddress(Address address, User user){

        var lookupAddress = addressRepository.findByAddress(
                address.address(),
                address.city(),
                address.state(),
                address.zip());

        if (lookupAddress.isEmpty()){
            var linkedAddress = userAddressRepository.save(new UserAddress(user, addressRepository.save(address)));
            log.info("Created new address: " + linkedAddress.address());
            log.info(user.id() + ":" + user.username() + " associated to: " + linkedAddress.address());
        }

        if (lookupAddress.isPresent()){
            var linkedAddress = userAddressRepository.save(new UserAddress(user, lookupAddress.get()));
            log.info(user.id() + ":" + user.username() + " associated to: " + linkedAddress.address());
        }
    }
}
