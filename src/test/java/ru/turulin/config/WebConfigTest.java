package ru.turulin.config;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //Аннотация говорит Spring Boot найти конфигурационные файлы(классы) + @SpringBootApplication.
//Это нужно для старта Spring контекста который может понадобиться.
@AutoConfigureMockMvc //Аннотация запускает Spring контекст, но не сервер.
// Так мы избавляемся от ненужных затрат произвдительности.
class WebConfigTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Test
    void applicationContextNotNullTest() {
        Assert.assertNotNull(webAppContext);
        Assertions.assertThat(WebConfig.class).isNotNull();
    }
}