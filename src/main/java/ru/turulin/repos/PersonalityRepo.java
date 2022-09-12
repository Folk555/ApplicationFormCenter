package ru.turulin.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.turulin.models.Account;
import ru.turulin.models.Personality;

@Repository
public interface PersonalityRepo extends CrudRepository<Personality, Long> {
    Personality findByActivateCode(String code);
}
