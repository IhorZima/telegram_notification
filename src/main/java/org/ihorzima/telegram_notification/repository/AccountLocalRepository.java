package org.ihorzima.telegram_notification.repository;

import org.ihorzima.telegram_notification.model.Account;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
public class AccountLocalRepository {

    private Map<String, Account> accounts = new HashMap<>();

    public void saveAccount(List<Account> list) {
        List<Account> accounts = List.copyOf(list);
        this.accounts = accounts.stream().collect(toMap(Account::getLandId, Function.identity()));
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(this.accounts.values());
    }

    public Account getAccountByLandId(String landId) {
        return this.accounts.get(landId);
    }

    public List<Account> getAccountsByLandIdStaringWith(String landIdPrefix) {
        return this.accounts.values().stream()
                .filter(account -> account.getLandId().startsWith(landIdPrefix))
                .toList();
    }
}