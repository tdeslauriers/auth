package world.deslauriers.service;

import jakarta.inject.Singleton;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.UserAddress;
import world.deslauriers.repository.UserAddressRepository;

@Singleton
public class UserAddressServiceImpl implements UserAddressService{

    private final UserAddressRepository userAddressRepository;

    public UserAddressServiceImpl(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public Flux<UserAddress> getByAddressId(Long id) {
        return userAddressRepository.findByAddressId(id);
    }

    @Override
    public Mono<Long> delete(UserAddress userAddress) {
        return userAddressRepository.delete(userAddress);
    }
}
