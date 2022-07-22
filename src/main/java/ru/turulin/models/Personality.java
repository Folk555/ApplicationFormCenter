package ru.turulin.models;

import lombok.Data;

import javax.persistence.*;

/**
 * В этой сущности хранятся все возможные персональные данные пользователя.
 */
@Data
@Entity
@Table(name = "personalities")
public class Personality {
    @Id
    @SequenceGenerator(name="personalities_gen",
    sequenceName = "personalities_id_seq",
    allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personalities_gen")
    int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "last_name")
    private String lastName;
}
