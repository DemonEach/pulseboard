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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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
    @Autowired
    TransactionTemplate transactionTemplate;

    @Test
    @Transactional
    @Rollback
    void createAccountTest() {
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

    Integer createTestAccount() {
        return transactionTemplate.execute(status -> {
            Account account = new Account();
            OffsetDateTime now = OffsetDateTime.now();

            account.setUsername("test1");
            account.setPassword(bCryptPasswordEncoder.encode("test123"));
            account.setEmail("test1@gmail.com");
            account.setName("Test Test Test");
            account.setPhoneNumber("+77777777777");
            account.setOrganization("Test Organization");
            account.setEnabled(false);
            account.setCreationTime(now);
            account.setUpdateTime(now);

            accountRepository.save(account);

            return account.getId();
        });
    }

    @Test
    @Transactional
    @Rollback
    void updateAccount() {
        try {
            Integer accountId = createTestAccount();

            accountService.updateAccount(formTestUpdateAccountRequest(accountId));

            Account account = getTestAccount(accountId);

            assertEquals("Test test 2", account.getName());
            assertEquals("Test organization 2", account.getOrganization());
            assertEquals("+88888888888", account.getPhoneNumber());
        } catch (NoSuchElementException | EntityNotFoundException e) {
            log.error("An error occurred during account update!", e);

            fail(e);
        }
    }

    private UpdateAccountRequest formTestUpdateAccountRequest(Integer accountId) {
        UpdateAccountRequest request = new UpdateAccountRequest();

        request.setId(accountId);
        request.setName("Test test 2");
        request.setOrganization("Test organization 2");
        request.setPhoneNumber("+88888888888");

        return request;
    }

    @Test
    @Transactional
    @Rollback
    void changeUsername() {
        try {
            Integer accountId = createTestAccount();

            accountService.changeUsername(formTestChangeUsernameRequest(accountId));

            Account account = getTestAccount(accountId);

            assertEquals("test2", account.getUsername());
        } catch (AccountDataValidationException | EntityNotFoundException e) {
            log.error("An error occurred during username update!", e);

            fail(e);
        }
    }

    private ChangeUsernameRequest formTestChangeUsernameRequest(Integer accountId) {
        ChangeUsernameRequest request = new ChangeUsernameRequest();

        request.setUsername("test2");
        request.setAccountId(accountId);
        request.setCurrentPassword("test123");

        return request;
    }

    @Test
    void changeEmail() {
        try {
            Integer accountId = createTestAccount();

            accountService.changeEmail(formTestChangeEmailRequest(accountId));

            Account account = getTestAccount(accountId);

            assertEquals("test1@test1.com", account.getEmail());
        } catch (AccountDataValidationException | EntityNotFoundException e) {
            log.error("An error occurred during email update!", e);

            fail(e);
        }
    }

    private ChangeEmailRequest formTestChangeEmailRequest(Integer accountId) {
        ChangeEmailRequest request = new ChangeEmailRequest();

        request.setEmail("test1@test1.com");
        request.setAccountId(accountId);
        request.setCurrentPassword("test123");

        return request;
    }

    @Test
    void changePassword() {
        try {
            Integer accountId = createTestAccount();

            accountService.changePassword(formTestChangePasswordRequest(accountId));

            Account account = getTestAccount(accountId);

            assertTrue(bCryptPasswordEncoder.matches("123456", account.getPassword()));
        } catch (AccountDataValidationException | EntityNotFoundException e) {
            log.error("An error occurred during password update!", e);

            fail(e);
        }
    }

    private ChangePasswordRequest formTestChangePasswordRequest(Integer accountId) {
        ChangePasswordRequest request = new ChangePasswordRequest();

        request.setPassword("123456");
        request.setAccountId(accountId);
        request.setCurrentPassword("test123");

        return request;
    }

    @Test
    void getAccountData() {
        try {
            Integer accountId = createTestAccount();
            AccountData accountData = accountService.getAccountData(accountId);

            assertNotNull(accountData);
            assertEquals(accountId, accountData.getId());
        } catch (EntityNotFoundException e) {
            log.error("An error occurred during reading account data!", e);

            fail(e);
        }
    }

    @Test
    void deleteAccount() {
        try {
            Integer accountId = createTestAccount();
            accountService.deleteAccount(accountId);

            assertFalse(accountRepository.findById(accountId).isPresent());
        } catch (EntityNotFoundException e) {
            log.error("An error occurred during account deletion!", e);

            fail(e);
        }
    }

    private Account getTestAccount(Integer accountId) {
        return accountRepository.findById(accountId).orElseThrow();
    }
}