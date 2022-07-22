package ru.turulin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.ui.Model;
import ru.turulin.models.Account;

import javax.sql.DataSource;

/**
 * Настройка авторизации.
 * Дока Spring предлагает использовать лямбда выражения. Но тогда придется обрабатывать некоторые исключения
 * прямо в блоке лямбды, что уменьшает читабельность.
 * {@ru.turulin.config.WebSecurityConfig2#dataSource} - источник данных
 * {@ru.turulin.config.WebSecurityConfig2#passwordEncoder} - Кодировщик пороля
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig2 {

    @Autowired
    private DataSource dataSource;
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Фильтр URL который будет запрещать или наоборот разрешать доступ к ресурсам.
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                            .antMatchers("/", "/registration", "/img/**").permitAll()//Запросы по этому url
                            .anyRequest().authenticated() //каждый считается "Авторезированным"
                        .and()
                            .formLogin()//Включаем форму авторизации
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .loginPage("/login")//Если требуется идентификация то Spring редиретит по этому url
                            .defaultSuccessUrl("/helpRequest")
                            .permitAll()
                        .and()
                            .logout()
                            .permitAll();
        return http.build();
    }

    //не будет работать если есть JDBCUserDetailsManager или LAPDUserDetailsManager и наоборот.
    //Отлично подходит для тестирования доступа.
    //@Bean
    public InMemoryUserDetailsManager userDetailsService() {

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // outputs {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
        // remember the password that is printed out and use in the next step
        //System.out.println(encoder.encode("admin10"));

        UserDetails user = User.withUsername("MainAdmin")
                //.password("{bcrypt}$2a$10$Snf1cognH3/BvMqezbrTiuRtcWAEoSEtSheB/BxCqYexb1ca5gm7O")
                .password("123")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Создаем сервис авторизазии юзеров.
     * Плюс юзера "Admin" с ролью администратора по умолчанию, если его нет в БД.
     * @return
     */
    @Bean
    public UserDetailsService UserDetailsService() {

        JdbcUserDetailsManager usersManager = new JdbcUserDetailsManager(dataSource);
        usersManager.setUsersByUsernameQuery("select username, password, enabled from accounts where username=?");
        usersManager.setAuthoritiesByUsernameQuery("select acc.username, ar.roles from accounts acc " +
                "inner join account_roles ar on acc.id = ar.account_id where acc.username=?");


        usersManager.setCreateUserSql("INSERT INTO accounts (id, username, password, enabled) " +
                "VALUES (nextval('account_id_seq'), ?, ?, ?);");
        usersManager.setCreateAuthoritySql("insert into account_roles (account_id, roles)" +
                "SELECT id, ? FROM accounts WHERE username = ?");
        usersManager.setUserExistsSql("select username from accounts where username = ?");

        //Не работает как надо!!!
        //Если дефолтнгого юзера в БД нет, то создаем.
        if (!usersManager.userExists("Admin")) {
            UserDetails user = User.builder()
                    .username("Admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles("ADMIN")
                    .build();
            usersManager.createUser(user);

        }


        return usersManager;
    }

    /**
     * Кодировщик пороля юзера. Так, если БД сольют будет известны только хеши поролей но не сами пороли.
     * @return
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
        return passwordEncoder;
    }

    /**
     * Создаем провайдер для БД вручную, так как дефолтный не имеет кодировщика пороля.
     * @return
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(UserDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder); //Все ради этой строчки
        return authProvider;
    }

}
