package org.ihorzima.telegram_notification.controller;

import lombok.extern.slf4j.Slf4j;
import org.ihorzima.telegram_notification.model.Account;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountLocalRepository accountLocalRepository;

    public AccountController(AccountLocalRepository accountLocalRepository) {
        this.accountLocalRepository = accountLocalRepository;
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public String saveAccount(@RequestBody List<Account> data) {
        accountLocalRepository.saveAccounts(data);
        log.info("{} accounts saved to memory", data.size());
        return "received data for account sheet successfully!";
    }
}
