package org.goblintelligence.pulseboard.services.auth.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.goblintelligence.pulseboard.exception.EntityNotFoundException;
import org.goblintelligence.pulseboard.services.auth.data.dto.*;
import org.goblintelligence.pulseboard.services.auth.exception.AccountDataValidationException;
import org.goblintelligence.pulseboard.services.auth.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountData> getAccountData(@NotNull(message = "Account id cannot be empty!")
                                                      @PathVariable("accountId") Integer accountId) throws EntityNotFoundException {
        return ResponseEntity.ok(accountService.getAccountData(accountId));
    }

    @PostMapping
    public ResponseEntity<Void> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest)
            throws AccountDataValidationException {
        accountService.createAccount(createAccountRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateAccount(@Valid @RequestBody UpdateAccountRequest updateAccountRequest)
            throws EntityNotFoundException {
        accountService.updateAccount(updateAccountRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/username")
    public ResponseEntity<Void> changeUsername(@Valid @RequestBody ChangeUsernameRequest changeUsernameRequest)
            throws EntityNotFoundException, AccountDataValidationException {
        accountService.changeUsername(changeUsernameRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/email")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest changeEmailRequest)
            throws EntityNotFoundException, AccountDataValidationException {
        accountService.changeEmail(changeEmailRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest)
            throws EntityNotFoundException, AccountDataValidationException {
        accountService.changePassword(changePasswordRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@NotNull(message = "Account id cannot be empty!")
                                              @PathVariable("accountId") Integer accountId) throws EntityNotFoundException {
        accountService.deleteAccount(accountId);

        return ResponseEntity.ok().build();
    }
}
