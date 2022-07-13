package ru.turulin.repos;

import org.springframework.data.repository.CrudRepository;
import ru.turulin.models.HelpRequest;

public interface HelpRequestRepository extends CrudRepository<HelpRequest, String> {

}
