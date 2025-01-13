package org.goblintelligence.pulseboard.services.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goblintelligence.pulseboard.exception.EntityNotFoundException;
import org.goblintelligence.pulseboard.services.auth.data.dto.*;
import org.goblintelligence.pulseboard.services.auth.data.entity.Account;
import org.goblintelligence.pulseboard.services.auth.data.repository.AccountRepository;
import org.goblintelligence.pulseboard.services.auth.exception.AccountDataValidationException;
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
public class AccountService {

    private final AccountRepository accountRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private Account getAccountById(Integer id) throws EntityNotFoundException {
        return accountRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with id: %s not found!".formatted(id)));
    }

    public AccountData getAccountData(Integer accountId) throws EntityNotFoundException {
        return formAccountData(getAccountById(accountId));
    }

    private AccountData formAccountData(Account account) {
        AccountData accountData = new AccountData();

        accountData.setId(account.getId());
        accountData.setUsername(account.getUsername());
        accountData.setEmail(account.getEmail());
        accountData.setName(account.getName());
        accountData.setPhoneNumber(account.getPhoneNumber());
        accountData.setOrganization(account.getOrganization());

        return accountData;
    }

    public void createAccount(CreateAccountRequest request) throws AccountDataValidationException {
        validateAccountCreationData(request);

        Account account = new Account();
        OffsetDateTime now = OffsetDateTime.now();
        String temporalPassword = generateTemporalPassword();

        account.setUsername(request.getUsername());
        account.setPassword(bCryptPasswordEncoder.encode(temporalPassword));
        account.setEmail(request.getEmail());
        account.setName(request.getName());
        account.setPhoneNumber(request.getPhoneNumber());
        account.setOrganization(request.getOrganization());
        account.setEnabled(false);
        account.setCreationTime(now);
        account.setUpdateTime(now);

        accountRepository.save(account);

        mailService.sendMessage(
                formActivationMessage(request.getUsername(), temporalPassword),
                "Your temporal password on Pulse Board",
                request.getEmail());
    }

    private void validateAccountCreationData(CreateAccountRequest request) throws AccountDataValidationException {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AccountDataValidationException("Username: %s already in use!".formatted(request.getUsername()));
        }

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AccountDataValidationException("Email: %s already in use!".formatted(request.getEmail()));
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

    public void updateAccount(UpdateAccountRequest request) throws EntityNotFoundException {
        Account user = getAccountById(request.getId());

        checkAndSetValue(user.getName(), request.getName(), user::setName, true);
        checkAndSetValue(user.getPhoneNumber(), request.getPhoneNumber(), user::setPhoneNumber, true);
        checkAndSetValue(user.getOrganization(), request.getOrganization(), user::setOrganization, true);

        accountRepository.save(user);
    }

    public void changeUsername(ChangeUsernameRequest request) throws AccountDataValidationException, EntityNotFoundException {
        Account account = findAccountAndValidatePassword(request.getAccountId(), request.getCurrentPassword());

        if (accountRepository.existsByUsernameAndIdNot(request.getUsername(), request.getAccountId())) {
            throw new AccountDataValidationException("Username: %s already in use!".formatted(request.getUsername()));
        }

        checkAndSetValue(account.getUsername(), request.getUsername(), account::setUsername);

        accountRepository.save(account);
    }

    public void changeEmail(ChangeEmailRequest request) throws AccountDataValidationException, EntityNotFoundException {
        Account account = findAccountAndValidatePassword(request.getAccountId(), request.getCurrentPassword());

        if (accountRepository.existsByEmailAndIdNot(request.getEmail(), request.getAccountId())) {
            throw new AccountDataValidationException("Email: %s already in use!".formatted(request.getEmail()));
        }

        checkAndSetValue(account.getEmail(), request.getEmail(), account::setEmail);

        accountRepository.save(account);
    }

    public void changePassword(ChangePasswordRequest request) throws AccountDataValidationException, EntityNotFoundException {
        Account account = findAccountAndValidatePassword(request.getAccountId(), request.getCurrentPassword());

        checkAndSetValue(account.getPassword(), bCryptPasswordEncoder.encode(request.getPassword()), account::setPassword);

        accountRepository.save(account);
    }

    private Account findAccountAndValidatePassword(Integer accountId, String passwordToValidate)
            throws AccountDataValidationException, EntityNotFoundException {
        Account account = getAccountById(accountId);

        if (!bCryptPasswordEncoder.matches(passwordToValidate, account.getPassword())) {
            throw new AccountDataValidationException("Password is wrong!");
        }

        return account;
    }

    public void deleteAccount(Integer accountId) throws EntityNotFoundException {
        Account account = getAccountById(accountId);

        accountRepository.delete(account);
    }
}
