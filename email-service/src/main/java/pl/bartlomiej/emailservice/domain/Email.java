package pl.bartlomiej.emailservice.domain;

import jakarta.validation.constraints.NotBlank;

public abstract class Email {

    @NotBlank
    @jakarta.validation.constraints.Email
    private final String receiverEmail;

    @NotBlank
    private final String title;

    @NotBlank
    private final String message;

    protected Email(String receiverEmail, String title, String message) {
        this.receiverEmail = receiverEmail;
        this.title = title;
        this.message = message;
    }
}
