package ru.turulin.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import ru.turulin.models.Account;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
}
