package ru.turulin.controllers;

import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.xpath.XPathExpressionException;

import java.net.URI;
import java.util.regex.Matcher;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("testUserAdmin")
@TestPropertySource("/applicationTest.properties")
@Sql(value = {"/helpRequests-before.sql", "/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/helpRequests-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class HelpRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelpRequestController controller;

    //Пока не работает
    /*
    @Test
    void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/helpRequest"))
                .andExpect(authenticated())
                .andExpect(xpath("нужен xml запрос"));
    }
     */

    //nodeCount - кол-во вложенных контейнер-тегов
    @Test
    void helpRequestListTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/helpRequest"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr").nodeCount(5));
    }


    @Test
    void addHelpRequestTest() throws Exception {

        //Аналог post запроса, но в отличии от последнего имеет возсожность отправки файла.
        //Но не включает автоматически токен CSRF!
        RequestBuilder multipart = multipart("/helpRequest")
                .file("file", "123".getBytes())
                .param("messageText", "что-то")
                .param("requestOwner", "кто-то")
                .param("roomNumber", "где-то")
                .with(SecurityMockMvcRequestPostProcessors.csrf());

        this.mockMvc.perform(multipart)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(redirectedUrl("/helpRequest"));

        this.mockMvc.perform(get("/helpRequest"))
                .andExpect(authenticated())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr").nodeCount(6))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]").exists())
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestText']").string(StringContains.containsString("что-то")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestOwner']").string(StringContains.containsString("кто-то")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestRoomNumber']").string(StringContains.containsString("где-то")));



    }

}