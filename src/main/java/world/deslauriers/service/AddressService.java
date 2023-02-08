package world.deslauriers.service;

import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserAddress;

import java.util.HashSet;
import java.util.Optional;

public interface AddressService {

    Mono<Long> deleteAddress(Address address);
}
