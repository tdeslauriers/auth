package world.deslauriers.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import world.deslauriers.service.AddressService;
import world.deslauriers.service.UserAddressService;
import world.deslauriers.service.UserService;

import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/addresses")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    protected final AddressService addressService;
    protected final UserAddressService userAddressService;
    protected final UserService userService;

    public AddressController(AddressService addressService, UserAddressService userAddressService, UserService userService) {
        this.addressService = addressService;
        this.userAddressService = userAddressService;
        this.userService = userService;
    }


    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    Mono<Disposable> deleteAddress(Principal principal, Long id){

        // user must own address to delete it
        return Mono.just(userService.getUserByUsername(principal.getName())
                .flatMapMany(user -> userAddressService.getByAddressId(id)
                        .filter(userAddress -> userAddress.user().equals(user)))
                .flatMap(userAddress -> userAddressService.delete(userAddress)
                            .flatMap(xref -> {
                                log.info("Deleted xref {}: user id {}; address id {}.",
                                        xref, userAddress.user().id(), userAddress.address().id());
                                return addressService.deleteAddress(userAddress.address());
                            }))
                .subscribe(addressId -> log.info("Deleted address id: {}", addressId)));
    }

    @Secured({"PROFILE_ADMIN"})
    @Delete("/delete/{id}")
    @Status(HttpStatus.NO_CONTENT)
    Mono<Disposable> deleteUsersAddress(Long id){

        return Mono.just(userAddressService.getByAddressId(id)
                .flatMap(userAddress -> userAddressService.delete(userAddress)
                        .flatMap(xref -> {
                            log.info("Deleted xref {}: user: {}; address id: {}",
                                xref, userAddress.user().id(), userAddress.address().id());
                            return addressService.deleteAddress(userAddress.address());
                        }))
                .subscribe(addressId -> log.info("Deleted address id: {}", addressId)));
    }
}
