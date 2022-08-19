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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.turulin.config.WebSecurityConfigBeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("testUserAdmin")
@TestPropertySource("/applicationTest.properties")
@Sql(value = {"/helpRequests-before.sql", "/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/helpRequests-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getAccounts_shouldShow2Accounts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='accounts-list']/tr").nodeCount(3))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='accounts-list']/tr[@id=1]/td[@id='name']").string("testUserAdmin"))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='accounts-list']/tr[@id=1]/td[@id='roles']")
                        .string(StringContains.containsString("ADMIN")));
    }

    @Test
    void getAccount_shouldShowAccountById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/2"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='edit-user']/tr[2]/td[2]/input/@value")
                        .string(StringContains.containsString("testUser2")));
    }

    @Test
    void updateAccountTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                        .param("username", "testUser2updated")
                        .param("id", String.valueOf(2))
                        .param("password", "123")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/accounts"));

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(MockMvcResultMatchers.content().string(StringContains.containsString("testUser2updated")));
    }
}