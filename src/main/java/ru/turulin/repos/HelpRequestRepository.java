package ru.turulin.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.turulin.models.HelpRequest;

@Repository
public interface HelpRequestRepository extends CrudRepository<HelpRequest, String> {

}
