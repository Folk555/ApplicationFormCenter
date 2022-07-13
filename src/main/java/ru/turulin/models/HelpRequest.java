package ru.turulin.models;

import javax.persistence.*;

@Entity
@Table(name = "help_requests")
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

    public long getId() {
        return id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getRequestOwner() {
        return requestOwner;
    }

    public void setRequestOwner(String requestOwner) {
        this.requestOwner = requestOwner;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public HelpRequest() {
    }

    public HelpRequest(String messageText, String requestOwner, String roomNumber) {
        this.messageText = messageText;
        this.requestOwner = requestOwner;
        this.roomNumber = roomNumber;
    }
}
