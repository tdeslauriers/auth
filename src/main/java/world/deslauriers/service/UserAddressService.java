package world.deslauriers.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.UserAddress;

public interface UserAddressService {
    Flux<UserAddress> getByAddressId(Long id);

    Mono<Long> delete(UserAddress userAddress);
}
