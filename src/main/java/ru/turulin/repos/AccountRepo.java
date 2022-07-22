package ru.turulin.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.turulin.models.Account;


public interface AccountRepo extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
}
