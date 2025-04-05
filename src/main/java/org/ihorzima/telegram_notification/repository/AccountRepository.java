package org.ihorzima.telegram_notification.repository;

import org.ihorzima.telegram_notification.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByLandIdStartingWith(String landIdStr);
}
