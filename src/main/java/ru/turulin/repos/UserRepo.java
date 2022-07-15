package ru.turulin.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.turulin.models.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
