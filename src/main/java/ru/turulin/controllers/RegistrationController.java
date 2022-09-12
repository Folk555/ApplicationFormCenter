package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.turulin.models.Account;
import ru.turulin.models.Personality;
import ru.turulin.models.Role;
import ru.turulin.repos.AccountRepo;
import ru.turulin.repos.PersonalityRepo;
import ru.turulin.services.MailService;

import java.util.Collections;
import java.util.UUID;

@Controller
@PropertySource("classpath:urls.properties")
public class RegistrationController {
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

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addAccount(Account account, Personality personality, Model model) {

        Account accountFromDB = accountRepo.findByUsername(account.getUsername());
        if (accountFromDB != null) {
            model.addAttribute("message", "User exist");
            return "registration";
        }
        account.setEnabled(true);
        account.setRoles(Collections.singleton(Role.USER));
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepo.save(account);
        personality.setActivateCode(UUID.randomUUID().toString());
        personality.setAccount(account);
        personality = personalityRepo.save(personality); //Так мы получаем personality вместе с ID которое было присвоено в БД
        String message = "Здравствуйте " + account.getUsername() +
                ", для подтверждения почты перейдите по ссылке: " + accountConfirmUrl + "/" + personality.getActivateCode();
        mailSender.sendMail("sanya.turulin.98@list.ru", "Подтверждение почты", message);

        return "redirect:/login";
    }

    @GetMapping("/registration/activateEmail/{UUIDcode}")
    public String activateEmail(Model model, @PathVariable String UUIDcode) {
        Personality personality = personalityRepo.findByActivateCode(UUIDcode);
        if (personality == null) {
            model.addAttribute("message", "Аккаунт для привязки не найден.");
            return "registration";
        }
        personality.setActivateCode(null);
        personalityRepo.save(personality);
        model.addAttribute("message", "Почта привязана");
        return "registration";
    }
}
