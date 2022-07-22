package ru.turulin.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Java конфикурация сервлета.
 * здесь аннотация @Configuration не нужна.
 */

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    /**
     * Задаем сервлету spring конфигурацию
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {WebConfig.class};
    }

    /**
     * Задаем путь по которому будет соответствовать DispatcherServlet
     */
    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
}
