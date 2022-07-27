package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.turulin.models.Account;
import ru.turulin.models.HelpRequest;
import ru.turulin.repos.AccountRepo;
import ru.turulin.repos.HelpRequestRepository;

import java.security.Principal;

@Controller
@RequestMapping(path = "/helpRequest") //Все url в мапингах будут начинаться с url в @RequestMapping.
public class HelpRequestController {
    @Autowired // бин UserRepository будет автоматически внедрен в переменную userRepository
    private HelpRequestRepository helpRequestRepository;
    @Autowired
    private AccountRepo accountRepo;

    @PostMapping
    public String addHelpRequest(
            @AuthenticationPrincipal User user,
            @RequestParam String messageText,
            @RequestParam String requestOwner,
            @RequestParam String roomNumber,
            Model model) {
        Account account = accountRepo.findByUsername(user.getUsername());
        HelpRequest hr = new HelpRequest(messageText, requestOwner, roomNumber, account);
        helpRequestRepository.save(hr);
        Iterable<HelpRequest> helpRequestIterable = helpRequestRepository.findAll();
        model.addAttribute("helpRequests", helpRequestIterable);
        return "index";
    }

    @GetMapping
    public String showAllHelpRequest(Model model) {
        Iterable<HelpRequest> helpRequestIterable = helpRequestRepository.findAll();
        model.addAttribute("helpRequests", helpRequestIterable);
        return "index";
    }

}
