package ru.turulin.controllers;

import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.turulin.config.WebSecurityConfigBeans;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    //nodeCount - кол-во соседних контейнер-тегов того же уровня
    @Test
    void showListOfHelpRequestsTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/helpRequest"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr").nodeCount(5));
    }


    @Test
    void givenPostHelpRequestWithAllParameters_shouldSaveHelpRequestWithAllParameters() throws Exception {
        //Аналог post запроса, но в отличии от последнего имеет возсожность отправки файла.
        //Но не включает автоматически токен CSRF!
        RequestBuilder multipart = multipart("/helpRequest")
                .file("file", "123".getBytes())
                .param("messageText", "addHelpRequestTest")
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
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestText']").string(StringContains.containsString("addHelpRequestTest")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestOwner']").string(StringContains.containsString("кто-то")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestRoomNumber']").string(StringContains.containsString("где-то")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestFile']").exists());
    }

    /**
     * Здесь мы обновляем HelpRequest c id=10, новый не создаем, для этого есть другой тест.
     * Также проверяем создание HelpRequest без multipart (file=null)
     * @throws Exception
     */
    @Test
    void givenPostHelpRequestWithNoFile_shouldUpdateHelpRequestWithNoFile() throws Exception {
        RequestBuilder multipart = multipart("/helpRequest")
                .file(new MockMultipartFile("file", new byte[0]))
                .param("messageText", "givenPostHelpRequestWithNoFile_shouldSaveHelpRequestWithNoFile")
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
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestText']").string(StringContains.containsString("givenPostHelpRequestWithNoFile_shouldSaveHelpRequestWithNoFile")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestOwner']").string(StringContains.containsString("кто-то")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestRoomNumber']").string(StringContains.containsString("где-то")))
                .andExpect(MockMvcResultMatchers.xpath("//*[@id='helpRequests-list']/tr[@id=10]/td[@id='requestFile']").doesNotExist());
    }


}