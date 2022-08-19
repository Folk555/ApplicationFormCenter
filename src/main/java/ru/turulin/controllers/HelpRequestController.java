package ru.turulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.turulin.models.Account;
import ru.turulin.models.HelpRequest;
import ru.turulin.repos.AccountRepo;
import ru.turulin.repos.HelpRequestRepository;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping(path = "/helpRequest") //Все url в мапингах будут начинаться с url в @RequestMapping.
public class HelpRequestController {
    @Autowired // бин UserRepository будет автоматически внедрен в переменную userRepository
    private HelpRequestRepository helpRequestRepository;
    @Autowired
    private AccountRepo accountRepo;
    //Ищем в Property файле переменную
    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping
    public String addHelpRequest(
            @AuthenticationPrincipal User user,
            @RequestParam String messageText,
            @RequestParam String requestOwner,
            @RequestParam String roomNumber,
            @RequestParam MultipartFile file) throws IOException {
        Account account = accountRepo.findByUsername(user.getUsername());
        if (file.isEmpty()) {
            HelpRequest hr = new HelpRequest(messageText, requestOwner, roomNumber, account);
            helpRequestRepository.save(hr);
            return "redirect:/helpRequest";
        }

        //От этого надо избавиться
        //Нужно создать директории при развортывании ПО.
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists())
            uploadDir.mkdir();

        String uniqueFileName = UUID.randomUUID()+"."+file.getOriginalFilename();
        file.transferTo(new File(uploadPath+"/"+uniqueFileName));

        HelpRequest hr = new HelpRequest(messageText, requestOwner, roomNumber, account, uniqueFileName);
        helpRequestRepository.save(hr);

        return "redirect:/helpRequest";
    }

    @GetMapping
    public String showAllHelpRequest(Model model) {
        Iterable<HelpRequest> helpRequestIterable = helpRequestRepository.findAll();
        model.addAttribute("helpRequests", helpRequestIterable);
        return "helpRequest";
    }

}
