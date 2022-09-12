package ru.turulin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.turulin.exeptions.ExistingElementInDataSourceException;
import ru.turulin.exeptions.NotFoundElementInDataSourceException;
import ru.turulin.models.Account;
import ru.turulin.models.Personality;
import ru.turulin.models.Role;
import ru.turulin.repos.AccountRepo;
import ru.turulin.repos.PersonalityRepo;

import java.util.Collections;
import java.util.UUID;

@Service
@PropertySource("classpath:urls.properties")
public class AFCUserService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private PersonalityRepo personalityRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MailService mailSender;
    @Value("${URLaccountConfirm}")
    private String accountConfirmUrl;

    public Personality addAFCUser(Account account, Personality personality) throws ExistingElementInDataSourceException {
        Account accountFromDB = accountRepo.findByUsername(account.getUsername());
        if (accountFromDB != null) {
            throw new ExistingElementInDataSourceException("Username already exist in database.");
        }
        account.setEnabled(true);
        account.setRoles(Collections.singleton(Role.USER));
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepo.save(account);
        personality.setActivateCode(UUID.randomUUID().toString());
        personality.setAccount(account);
        personalityRepo.save(personality);
        if (personality.getEmail()==null)
            return personality;
        String message = "Центр принятия заявок.\n";
        message += "Здравствуйте " + account.getUsername() +
                ", для подтверждения почты перейдите по ссылке: " + accountConfirmUrl + "/" + personality.getActivateCode();
        mailSender.sendMail(personality.getEmail(), "Подтверждение почты", message);
        return personality;
    }


    public void activateEmail(String UUIDcode) throws NotFoundElementInDataSourceException {
        Personality personality = personalityRepo.findByActivateCode(UUIDcode);
        if (personality == null) {
            throw new NotFoundElementInDataSourceException("Personality by activate code not found in database.");
        }
        personality.setActivateCode(null);
        personalityRepo.save(personality);
    }


}
