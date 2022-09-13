package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.turulin.exeptions.NotFoundElementInDataSourceException;
import ru.turulin.models.Account;
import ru.turulin.models.Role;
import ru.turulin.repos.AccountRepo;
import ru.turulin.services.AFCUserService;

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
    AFCUserService afcUserService;

    @GetMapping
    public String getAccounts(Model model) {
        model.addAttribute("accounts", afcUserService.getAllAccounts());
        return "accounts";
    }

    @GetMapping("{accountId}")
    public String getAccount(
            @PathVariable long accountId,
            Model model) {
        try {
            model.addAttribute("editingAccount", afcUserService.getAccountById(accountId));
        } catch (NotFoundElementInDataSourceException e) {
            e.printStackTrace();
            model.addAttribute("message", "Не удалось найти пользователя по ID." +
                    "Свяжитесь с тех. поддержкой.");
            return "notificationPage";
        }
        return "accountEdit";
    }

    /**
     * @param account
     * @param form    - нужен для поиска ролей,
     *                так как иного способа заинжектить выбранные юзером роли - нет.
     * @return
     */
    @PostMapping
    public String updateAccount(Account account, @RequestParam Map<String, String> form, Model model) {
        try {
            afcUserService.updateAccount(account, form.keySet());
        } catch (NotFoundElementInDataSourceException e) {
            e.printStackTrace();
            model.addAttribute("message", "Не удалось найти пользователя по ID." +
                    "Свяжитесь с тех. поддержкой.");
            return "notificationPage";
        }
        return "redirect:/accounts";
    }

}
