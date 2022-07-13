package ru.turulin.models;

//import javax.persistence.*;

//@Entity

public class HelpRequest {
    //@Id
    //@GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String messageText;
    private String requestOwner;
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
