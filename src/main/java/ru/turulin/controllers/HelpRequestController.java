package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.turulin.models.HelpRequest;
import ru.turulin.repos.HelpRequestRepository;

@Controller
@RequestMapping(path = "/HelpRequest") //Все url в мапингах будут начинаться с url в @RequestMapping.
public class HelpRequestController {
    @Autowired // бин UserRepository будет автоматически внедрен в переменную userRepository
    private HelpRequestRepository helpRequestRepository;


    @PostMapping
    public String addHelpRequest(
            @RequestParam String messageText,
            @RequestParam String requestOwner,
            @RequestParam String roomNumber,
            Model model) {
        HelpRequest hr = new HelpRequest(messageText, requestOwner, roomNumber);
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
