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
import ru.turulin.exeptions.ExistingElementInDataSourceException;
import ru.turulin.exeptions.NotFoundElementInDataSourceException;
import ru.turulin.models.Account;
import ru.turulin.models.Personality;
import ru.turulin.models.Role;
import ru.turulin.repos.AccountRepo;
import ru.turulin.repos.PersonalityRepo;
import ru.turulin.services.AFCUserService;
import ru.turulin.services.MailService;

import java.util.Collections;
import java.util.UUID;

@Controller
public class RegistrationController {
    @Autowired
    private AFCUserService afcUserService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addAFCUser(Account account, Personality personality, Model model) {
        try {
            afcUserService.addAFCUser(account, personality);
        } catch (ExistingElementInDataSourceException e) {
            e.printStackTrace();
            model.addAttribute("message", "Пользователь с таким логином уже существует.");
            return "notificationPage";
        }
        return "redirect:/login";
    }

    @GetMapping("/registration/activateEmail/{UUIDcode}")
    public String activateEmail(Model model, @PathVariable String UUIDcode) {
        try {
            afcUserService.activateEmail(UUIDcode);
        } catch (NotFoundElementInDataSourceException e) {
            e.printStackTrace();
            model.addAttribute("message", "Не удалось найти код активации почты. " +
                    "Свяжитесь с тех. поддержкой.");
            return "notificationPage";
        }
        model.addAttribute("message", "Почта успешно привязана.");
        return "notificationPage";
    }
}
