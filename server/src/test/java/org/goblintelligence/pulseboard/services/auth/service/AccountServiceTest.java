package org.goblintelligence.pulseboard.services.auth.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import lombok.extern.slf4j.Slf4j;
import org.goblintelligence.pulseboard.PulseBoardApplicationTests;
import org.goblintelligence.pulseboard.exception.EntityNotFoundException;
import org.goblintelligence.pulseboard.services.auth.data.dto.*;
import org.goblintelligence.pulseboard.services.auth.data.entity.Account;
import org.goblintelligence.pulseboard.services.auth.data.repository.AccountRepository;
import org.goblintelligence.pulseboard.services.auth.exception.AccountDataValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests for account service")
class AccountServiceTest extends PulseBoardApplicationTests {

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(new ServerSetup(3025, null, "smtp"))
                    .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test123"))
                    .withPerMethodLifecycle(false);

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @Order(0)
    void createAccount() {
        try {
            accountService.createAccount(formTestCreateAccountRequest());

            assertTrue(accountRepository.findById(1).isPresent());
            assertTrue(greenMail.getReceivedMessages().length > 0);
        } catch (AccountDataValidationException e) {
            log.error("An error occurred during account creation!", e);

            fail(e);
        }
    }

    private CreateAccountRequest formTestCreateAccountRequest() {
        CreateAccountRequest request = new CreateAccountRequest();

        request.setUsername("test");
        request.setName("Test Test");
        request.setEmail("test@test.com");
        request.setOrganization("Test organization");
        request.setPhoneNumber("+99999999999");

        return request;
    }

    @Test
    @Order(1)
    void updateAccount() {
        try {
            accountService.updateAccount(formTestUpdateAccountRequest());

            Account account = getTestAccount();

            assertEquals("Test test 2", account.getName());
            assertEquals("Test organization 2", account.getOrganization());
            assertEquals("+88888888888", account.getPhoneNumber());
        } catch (NoSuchElementException | EntityNotFoundException e) {
            log.error("An error occurred during account update!", e);

            fail(e);
        }
    }

    private UpdateAccountRequest formTestUpdateAccountRequest() {
        UpdateAccountRequest request = new UpdateAccountRequest();

        request.setId(1);
        request.setName("Test test 2");
        request.setOrganization("Test organization 2");
        request.setPhoneNumber("+88888888888");

        return request;
    }

    @Test
    @Order(2)
    void setUpPassword() {
        Account account = getTestAccount();

        account.setPassword(bCryptPasswordEncoder.encode("12345"));

        accountRepository.save(account);
    }

    @Test
    @Order(3)
    void changeUsername() {
        try {
            accountService.changeUsername(formTestChangeUsernameRequest());

            Account account = getTestAccount();

            assertEquals("test2", account.getUsername());
        } catch (AccountDataValidationException | EntityNotFoundException e) {
            log.error("An error occurred during username update!", e);

            fail(e);
        }
    }

    private ChangeUsernameRequest formTestChangeUsernameRequest() {
        ChangeUsernameRequest request = new ChangeUsernameRequest();

        request.setUsername("test2");
        request.setAccountId(1);
        request.setCurrentPassword("12345");

        return request;
    }

    @Test
    @Order(4)
    void changeEmail() {
        try {
            accountService.changeEmail(formTestChangeEmailRequest());

            Account account = getTestAccount();

            assertEquals("test1@test1.com", account.getEmail());
        } catch (AccountDataValidationException | EntityNotFoundException e) {
            log.error("An error occurred during email update!", e);

            fail(e);
        }
    }

    private ChangeEmailRequest formTestChangeEmailRequest() {
        ChangeEmailRequest request = new ChangeEmailRequest();

        request.setEmail("test1@test1.com");
        request.setAccountId(1);
        request.setCurrentPassword("12345");

        return request;
    }

    @Test
    @Order(5)
    void changePassword() {
        try {
            accountService.changePassword(formTestChangePasswordRequest());

            Account account = getTestAccount();

            assertTrue(bCryptPasswordEncoder.matches("123456", account.getPassword()));
        } catch (AccountDataValidationException | EntityNotFoundException e) {
            log.error("An error occurred during password update!", e);

            fail(e);
        }
    }

    private ChangePasswordRequest formTestChangePasswordRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest();

        request.setPassword("123456");
        request.setAccountId(1);
        request.setCurrentPassword("12345");

        return request;
    }

    @Test
    @Order(6)
    void getAccountData() {
        try {
            AccountData accountData = accountService.getAccountData(1);

            assertNotNull(accountData);
            assertEquals(1, accountData.getId());
        } catch (EntityNotFoundException e) {
            log.error("An error occurred during reading account data!", e);

            fail(e);
        }
    }

    @Test
    @Order(7)
    void deleteAccount() {
        try {
            accountService.deleteAccount(1);

            assertFalse(accountRepository.findById(1).isPresent());
        } catch (EntityNotFoundException e) {
            log.error("An error occurred during account deletion!", e);

            fail(e);
        }
    }

    private Account getTestAccount() {
        return accountRepository.findById(1).orElseThrow();
    }
}