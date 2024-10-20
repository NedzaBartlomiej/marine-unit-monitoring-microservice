package pl.bartlomiej.emailservice.domain;

public class StandardEmail extends Email {
    protected StandardEmail(String receiverEmail, String title, String message) {
        super(receiverEmail, title, message);
    }
}