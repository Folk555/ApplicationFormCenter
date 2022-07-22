package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.turulin.models.Account;
import ru.turulin.models.Role;
import ru.turulin.repos.AccountRepo;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }
    @PostMapping("/registration")
    public String addAccount(Account account, Model model) {
        Account accountFromDB = accountRepo.findByUsername(account.getUsername());
        if (accountFromDB != null) {
            model.addAttribute("message","User exist");
            return "registration";
        }
        account.setEnabled(true);
        account.setRoles(Collections.singleton(Role.USER));
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepo.save(account);
        return "redirect:/login";
    }
}
