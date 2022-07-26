package ru.turulin.models;

import lombok.Data;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;

@Entity
@Table(name = "help_requests")
@Data
public class HelpRequest {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "help_requests_gen",
            sequenceName = "help_requests_id_seq",
            allocationSize=1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "help_requests_gen")
    //GenerationType.SEQUENCE не работает с полем INTEGER!!!!
    private int id;
    @Column(name = "message_text")
    private String messageText;
    @Column(name = "request_owner")
    private String requestOwner;
    @Column(name = "room_number")
    private String roomNumber;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account author;

    public HelpRequest() {
    }

    public HelpRequest(String messageText, String requestOwner, String roomNumber) {
        this.messageText = messageText;
        this.requestOwner = requestOwner;
        this.roomNumber = roomNumber;
    }

    public HelpRequest(String messageText, String requestOwner, String roomNumber, Account account) {
        this.messageText = messageText;
        this.requestOwner = requestOwner;
        this.roomNumber = roomNumber;
        this.author = account;
    }

    public String getUsernameAuthor() {
        return author != null ? author.getUsername() : "<none>";
    }
}
