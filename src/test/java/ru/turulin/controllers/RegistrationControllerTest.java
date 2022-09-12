package ru.turulin.controllers;

import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/applicationTest.properties")
@Sql(value = {"/helpRequests-before.sql", "/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/helpRequests-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RegistrationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void registrationURL_shouldShowRegisterForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/registration"))
                .andExpect(MockMvcResultMatchers.xpath("/html/body/form").exists());
    }

    @Test
    void addAccount_accNotExist_shouldBeCreated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "newUser")
                        .param("password", "testPass")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "newUser")
                .param("password", "testPass"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/helpRequest"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .with(SecurityMockMvcRequestPostProcessors.user("newUser").password("testPass")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void addAccount_accExist_shouldNotBeCreated() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .param("username", "testUser2")
                        .param("password", "testPass")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.xpath("//div[@id='notification']")
                        .string(StringContains.containsStringIgnoringCase("Пользователь с таким логином уже существует")));
    }
}