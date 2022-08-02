package ru.turulin.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.turulin.controllers.HelpRequestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest //Аннотация говорит Spring Boot найти конфигурационные файлы(классы) + @SpringBootApplication.
//Это нужно для старта Spring контекста который может понадобиться.
@AutoConfigureMockMvc //Аннотация запускает Spring контекст, но не сервер.
// Так мы избавляемся от ненужных затрат произвдительности.
class WebSecurityConfig2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelpRequestController controller;

    @Test
    public void contextLoads() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("User Name :")));

        //assertThat(controller).isNotNull();
        //assertThat(WebSecurityConfig2.class).isNotNull();
    }

    @Test
    public void accessDeniedTest() throws Exception {
        this.mockMvc.perform(get("/helpRequest"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }


    @Test
    public void correctLoginTest() throws Exception {
        this.mockMvc.perform(formLogin().user("test").password("test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/helpRequest"));
    }

    @Test
    public void badCredentialsTest() throws Exception {
        this.mockMvc.perform(post("/login").param("user", "titok"))
                .andExpect(status().isForbidden());
    }

}