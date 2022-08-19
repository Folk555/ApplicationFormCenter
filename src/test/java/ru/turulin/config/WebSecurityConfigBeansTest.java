package ru.turulin.config;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.turulin.controllers.HelpRequestController;

import javax.sql.DataSource;

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
class WebSecurityConfigBeansTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Test
    public void webSecurityConfigBeansTestConstructorTest() {
        Assert.assertNotNull(dataSource);
        Assert.assertNotNull(passwordEncoder);
        Assert.assertNotNull(userDetailsService);
        Assert.assertNotNull(daoAuthenticationProvider);
    }
    @Test
    public void userDetailsServiceCorrectedTest(){
        Assert.assertNotNull(userDetailsService.loadUserByUsername("test"));
    }

    @Test
    public void freeResourcesTest() throws Exception {
        this.mockMvc.perform(get("/img/logo.jpg"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    public void loginPageEnableTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("User Name :")));
    }

    @Test
    public void logoutPageEnableTest() throws Exception {
        this.mockMvc.perform(get("/logout"))
                .andDo(print()).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
    }

    @Test
    public void tokenCRFExistTest() throws Exception {
        this.mockMvc.perform(get("/")
                        .param("username", "test")
                        .param("password", "test"))
                .andDo(print()).andExpect(MockMvcResultMatchers.cookie().exists("XSRF-TOKEN"));

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
        this.mockMvc.perform(post("/login").param("user", "tiktok"))
                .andExpect(status().isForbidden());
    }

    @Test
    //Тип кодировщика BCryptPasswordEncoder(8)
    public void passwordEncoderCorrectedTest(){
        String password = "testPassword";

        String encodedPassword = passwordEncoder.encode(password);

        Assert.assertNotEquals(password, encodedPassword);
        Assert.assertTrue(passwordEncoder.matches(password, "$2a$08$sxZKf/LbAYnfUZXs2mUG0OwqvwR.GT/tmQrE0DOIsVklbFHiwg8VO"));
    }

}