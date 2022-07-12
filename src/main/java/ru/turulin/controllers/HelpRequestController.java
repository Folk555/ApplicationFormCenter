package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.turulin.models.HelpRequest;
//import ru.turulin.repos.HelpRequestRepository;

@Controller
@RequestMapping(path="/HelpRequest") //Все url в мапингах будут начинаться с url в @RequestMapping.
public class HelpRequestController {
    //@Autowired // бин UserRepository будет автоматически внедрен в переменную userRepository
    //private HelpRequestRepository helpRequestRepository;

//    @GetMapping
//    public String showAllHelpRequests(Model model){
//        Iterable<HelpRequest> helpRequestIterable = helpRequestRepository.findAll();
//        model.addAttribute("helpRequests", "helpRequestIterable");
//        return "index";
//    }
//    @PostMapping()
//    public String add(@RequestParam String text,
//    @RequestParam String tag,
//    Model model){
//        return "index";
//    }
    @GetMapping()
    public String add(){
        return "index";
    }




//
//    @GetMapping(path="/all")
//    public @ResponseBody Iterable<User> getAllUsers() {
//        // This returns a JSON or XML with the users
//        return userRepository.findAll();
//    }
}
