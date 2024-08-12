package pl.bartlomiej.apiservice.emailsending.announcementemail;

import pl.bartlomiej.apiservice.emailsending.common.Email;

public class AnnouncementEmail extends Email {
    public AnnouncementEmail(String receiverEmail, String title, String message) {
        super(receiverEmail, title, message);
    }
}
