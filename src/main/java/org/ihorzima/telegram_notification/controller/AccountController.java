package org.ihorzima.telegram_notification.controller;

import org.ihorzima.telegram_notification.model.Account;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        accountLocalRepository.saveAccount(data);
        return "received data for account sheet successfully!";
    }
}
