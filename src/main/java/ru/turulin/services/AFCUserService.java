package ru.turulin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.turulin.exeptions.ExistingElementInDataSourceException;
import ru.turulin.exeptions.NotFoundElementInDataSourceException;
import ru.turulin.models.Account;
import ru.turulin.models.Personality;
import ru.turulin.models.Role;
import ru.turulin.repos.AccountRepo;
import ru.turulin.repos.PersonalityRepo;

import java.util.*;
import java.util.stream.Collectors;

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




    public Iterable<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    public Account getAccountById(long accountId) throws NotFoundElementInDataSourceException {
        Optional<Account> optionalAccount = accountRepo.findById(accountId);
        if (!optionalAccount.isPresent())
            throw new NotFoundElementInDataSourceException("Account by ID not found in database.");
        return optionalAccount.get();
    }

    /**
     * @param account
     * @param setRoles - Множество названий ролей.
     * @return
     */
    public Account updateAccount(Account account, Set<String> setRoles) throws NotFoundElementInDataSourceException {
        Optional<Account> optionalAccount = accountRepo.findById(account.getId());
        if (!optionalAccount.isPresent())
            throw new NotFoundElementInDataSourceException("Account by ID not found in database.");
        Account accountFromDB = optionalAccount.get();
        if (!accountFromDB.getPassword().equals(account.getPassword()))
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (!accountFromDB.getUsername().equals(account.getUsername()))
            account.setUsername(account.getUsername());
        Set<String> possibleRoleNames = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        Set<Role> newRoles = new TreeSet<>();
        for (String key : setRoles){
            if (possibleRoleNames.contains(key))
                newRoles.add(Role.valueOf(key));
        }
        account.setRoles(newRoles);
        accountRepo.save(account); //Существующая запись обновится
        return account;
    }

}
