package org.ihorzima.telegram_notification.util;

import org.ihorzima.telegram_notification.model.Account;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InlineAccountCache {
    private static final Map<String, Account> cache = new ConcurrentHashMap<>();

    public static String store(Account account) {
        String key = UUID.randomUUID().toString();
        cache.put(key, account);
        return key;
    }

    public static Account retrieve(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }
}
