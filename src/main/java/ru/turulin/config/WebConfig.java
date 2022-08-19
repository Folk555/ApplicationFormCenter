package ru.turulin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * Конфигурируем Spring
 */

@Configuration
@ComponentScan("ru.turulin.*")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Value("${upload.path}")
    private String uploadPath;

    private final ApplicationContext applicationContext;

    @Autowired
    public WebConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Настраеваем путь к представлениям
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/views/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    /**
     * Внедряем в spring шаблонизатор Thymeleaf
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        registry.viewResolver(resolver);
    }
    /**
     * Для страниц, которые никак не обрабатываются сервером(контроллером), а просто возвращают страницу, маппинг можно настроить в конфигурации.
     * Страница обрабатывается Spring Security контроллером по умолчанию, поэтому для неё отдельный контроллер не требуется.
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("").setViewName("login");
        registry.addViewController("/logout").setViewName("login");
    }

    /**
     * Указываем Spring web где лежат ресурсы(кртинки, CSS, файлы, прочее).
     * Нужно для корректного отображения сайта.
     * Не забудь проверить естьли доступ к этим ресупсам у разных юзеров в {@link WebSecurityConfigBeans}.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("img/**").addResourceLocations("/views/img/");
        registry.addResourceHandler("css/**").addResourceLocations("/views/css/");
        registry.addResourceHandler("uploads/**").addResourceLocations("file://"+uploadPath+"/");
    }


}
