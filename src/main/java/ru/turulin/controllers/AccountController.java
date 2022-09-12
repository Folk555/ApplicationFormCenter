package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.turulin.models.Account;
import ru.turulin.models.Role;
import ru.turulin.repos.AccountRepo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер обработки аккаунтов.
 */
@Controller
@RequestMapping(path = "/accounts")
//Доступ к контроллеру имеют только ADMIN юзеры
@PreAuthorize("hasAuthority('ADMIN')")
public class AccountController {

    @Autowired
    AccountRepo accountRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping
    public String getAccounts(Model model) {
        Iterable<Account> accountIterable = accountRepo.findAll();
        model.addAttribute("accounts", accountIterable);
        return "accounts";
    }

    @GetMapping("{accountId}")
    public String getAccount(
            @PathVariable long accountId,
            Model model) {
        Optional<Account> optionalAccount = accountRepo.findById(accountId);
        if (!optionalAccount.isPresent()) return "accounts";
        model.addAttribute("editingAccount", optionalAccount.get());
        return "accountEdit";
    }

    /**
     * @param account
     * @param form - нужен для поиска ролей, так как иного способа заинжектить выбранные юзером роли - нет.
     * @return
     */
    @PostMapping
    public String updateAccount(Account account, @RequestParam Map<String, String> form){
        Optional<Account> optionalAccount = accountRepo.findById(account.getId());
        if (!optionalAccount.isPresent()) return "redirect:/accounts";
        Account accountFromDB = optionalAccount.get();
        if (!accountFromDB.getPassword().equals(account.getPassword()))
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (!accountFromDB.getUsername().equals(account.getUsername()))
            account.setUsername(account.getUsername());
        Set<String> possibleRoleNames = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        Set<Role> newRoles = new TreeSet<>();
        for (String key : form.keySet()){
            if (possibleRoleNames.contains(key))
                newRoles.add(Role.valueOf(key));
        }
        account.setRoles(newRoles);
        accountRepo.save(account); //Существующая запись обновится

        return "redirect:/accounts";
    }

}
