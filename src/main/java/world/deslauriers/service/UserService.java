package world.deslauriers.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.PasswordHistory;
import world.deslauriers.model.database.User;
import world.deslauriers.model.dto.ProfileDto;
import world.deslauriers.model.dto.RemoveUserRoleCmd;
import world.deslauriers.model.dto.ResetPasswordCmd;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.registration.RegistrationResponseDto;

public interface UserService {

    Mono<RegistrationResponseDto> registerUser(RegistrationDto registrationDto);
    Mono<User> getUserByUsername(String email);
    Mono<User> getUserById(Long id);
    Flux<User> backupAll();
    Flux<ProfileDto> getAllUsers();
    Mono<ProfileDto> getProfile(String username);
    Mono<ProfileDto> getProfileByUuid(String uuid);
    Mono<User> updateUser(User user, ProfileDto updatedProfile);
    Mono<PasswordHistory> resetPassword(User user, ResetPasswordCmd cmd);
    Mono<Void> removeUserRole(RemoveUserRoleCmd cmd);
}
