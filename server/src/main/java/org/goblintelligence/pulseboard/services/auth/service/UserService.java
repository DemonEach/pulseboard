package org.goblintelligence.pulseboard.services.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goblintelligence.pulseboard.services.auth.data.dto.*;
import org.goblintelligence.pulseboard.services.auth.data.entity.User;
import org.goblintelligence.pulseboard.services.auth.data.repository.UserRepository;
import org.goblintelligence.pulseboard.services.auth.exception.UserDataValidationException;
import org.goblintelligence.pulseboard.services.mail.service.MailService;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static org.goblintelligence.pulseboard.utils.DataUtils.checkAndSetValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private User getUserById(Integer id) throws IllegalArgumentException {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id: %s not found!".formatted(id)));
    }

    public UserData getUserData(Integer userId) {
        return formUserData(getUserById(userId));
    }

    private UserData formUserData(User user) {
        UserData userData = new UserData();

        userData.setId(user.getId());
        userData.setUsername(user.getUsername());
        userData.setEmail(user.getEmail());
        userData.setName(user.getName());
        userData.setPhoneNumber(user.getPhoneNumber());
        userData.setOrganization(user.getOrganization());

        return userData;
    }

    public void createUser(CreateUserRequest request) throws UserDataValidationException {
        validateUserCreationData(request);

        User user = new User();
        OffsetDateTime now = OffsetDateTime.now();
        String temporalPassword = generateTemporalPassword();

        user.setUsername(request.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(temporalPassword));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setOrganization(request.getOrganization());
        user.setEnabled(false);
        user.setCreationTime(now);
        user.setUpdateTime(now);

        userRepository.save(user);

        mailService.sendMessage(
                formActivationMessage(request.getUsername(), temporalPassword),
                "Your temporal password on Pulse Board",
                request.getEmail());
    }

    private void validateUserCreationData(CreateUserRequest request) throws UserDataValidationException {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserDataValidationException("Username: %s already in use!".formatted(request.getUsername()));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserDataValidationException("Email: %s already in use!".formatted(request.getEmail()));
        }
    }

    private String generateTemporalPassword() {
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        CharacterRule alphabeticalRule = new CharacterRule(EnglishCharacterData.Alphabetical);
        CharacterRule digitsRule = new CharacterRule(EnglishCharacterData.Digit);

        alphabeticalRule.setNumberOfCharacters(8);
        digitsRule.setNumberOfCharacters(4);

        return passwordGenerator.generatePassword(16, List.of(alphabeticalRule, digitsRule));
    }

    private String formActivationMessage(String username, String temporalPassword) {
        return """
                Hi %s
                Welcome on Pulse Board!
                Your temporal password is: %s
                Its validity period is 1 day.
                After the first login, it will need to be changed, because the temporary password will no longer be valid after login.
                """.formatted(username, temporalPassword);
    }

    public void updateUser(UpdateUserRequest request) {
        User user = getUserById(request.getId());

        checkAndSetValue(user.getName(), request.getName(), user::setName, true);
        checkAndSetValue(user.getPhoneNumber(), request.getPhoneNumber(), user::setPhoneNumber, true);
        checkAndSetValue(user.getOrganization(), request.getOrganization(), user::setOrganization, true);

        userRepository.save(user);
    }

    public void changeUsername(ChangeUsernameRequest request) throws UserDataValidationException {
        User user = findUserAndValidatePassword(request.getUserId(), request.getCurrentPassword());

        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), request.getUserId())) {
            throw new UserDataValidationException("Username: %s already in use!".formatted(request.getUsername()));
        }

        checkAndSetValue(user.getUsername(), request.getUsername(), user::setUsername);

        userRepository.save(user);
    }

    public void changeUserEmail(ChangeEmailRequest request) throws UserDataValidationException {
        User user = findUserAndValidatePassword(request.getUserId(), request.getCurrentPassword());

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), request.getUserId())) {
            throw new UserDataValidationException("Email: %s already in use!".formatted(request.getEmail()));
        }

        checkAndSetValue(user.getEmail(), request.getEmail(), user::setEmail);

        userRepository.save(user);
    }

    public void changeUserPassword(ChangePasswordRequest request) throws UserDataValidationException {
        User user = findUserAndValidatePassword(request.getUserId(), request.getCurrentPassword());

        checkAndSetValue(user.getPassword(), bCryptPasswordEncoder.encode(request.getPassword()), user::setPassword);

        userRepository.save(user);
    }

    private User findUserAndValidatePassword(Integer userId, String passwordToValidate) throws UserDataValidationException {
        User user = getUserById(userId);

        if (!user.getPassword().equals(bCryptPasswordEncoder.encode(passwordToValidate))) {
            throw new UserDataValidationException("Password is wrong!");
        }

        return user;
    }

    public void deleteUser(Integer userId) {
        User user = getUserById(userId);

        userRepository.delete(user);
    }
}
