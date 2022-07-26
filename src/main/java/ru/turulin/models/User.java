package ru.turulin.models;

import javax.persistence.*;
import java.util.Set;

//@Entity
//@Table(name = "usrs")
public class User {

    @Id
    @SequenceGenerator(name = "usrs_gen",
            sequenceName = "usrs_id_seq",
            allocationSize=1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usrs_gen")
    private long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "last_name")
    private String lastName;
    private String username;
    private String password;
    private String role;
    private boolean enabled;
    //Аналог @OneToMany. Предпочтителен для перечеслений.
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name="user_role",
            joinColumns=@JoinColumn(name="usr_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean active) {
        this.enabled = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User() {
    }
}
