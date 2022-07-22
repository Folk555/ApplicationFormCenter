package ru.turulin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

/**
 * Настройка авторизации
 */
//@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/registration").permitAll() //Запросы по этому url
                    .anyRequest().authenticated()            //каждый считается "Авторезированным"
                .and()
                    .formLogin()//Включаем форму авторизации
                    .loginPage("/login")//Если требуется идентификация то Spring редиретит по этому url
                    .permitAll()
                .and()
                    .logout()
                    .permitAll();
    }



    /**
     *Создаем/заносим в систему нового юзера(просто для отладки)
     */

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("u")
                        .password("123")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }



    /**
     * Загружаем юзеров из БД
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("select username, password, active from usrs where username=?")
                .authoritiesByUsernameQuery("select u.username, ur.roles from usrs u inner join user_role ur on u.id = ur.usr_id where u.username=?");

    }
}
