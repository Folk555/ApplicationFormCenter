package ru.turulin.models;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * В Spring security по умолчанию используется две таблица "БД".
 * "users" (username, password, enabled) и "authorities" (username, authority).
 * Для хранения пользователей и их ролей соответственно.
 * Я же использую единую таблицу accounts.
 */
@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @SequenceGenerator(name = "account_gen",
            sequenceName = "account_id_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_gen")
    private long id;
    private String username;
    private String password;
    private boolean enabled;
    //Аналог @OneToMany. Предпочтителен для перечеслений.
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name="account_roles",
            joinColumns=@JoinColumn(name="account_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
