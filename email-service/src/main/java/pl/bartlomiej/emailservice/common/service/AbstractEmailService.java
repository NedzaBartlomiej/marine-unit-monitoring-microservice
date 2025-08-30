package pl.bartlomiej.emailservice.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import pl.bartlomiej.mumcommons.emailintegration.external.model.Email;

@Service
public abstract class AbstractEmailService<T extends Email> implements EmailService<T> {

    private static final String APP_LOGO_PATH = "static/marine-unit-monitoring-app-logo.png";
    private static final String APP_LOGO = "app-logo";
    private static final Logger log = LoggerFactory.getLogger(AbstractEmailService.class);
    private final JavaMailSender javaMailSender;
    private final ResourceLoader resourceLoader;

    protected AbstractEmailService(JavaMailSender javaMailSender, ResourceLoader resourceLoader) {
        this.javaMailSender = javaMailSender;
        this.resourceLoader = resourceLoader;
    }

    protected abstract String buildHtmlMessage(final T email);

    protected MimeMessage buildMimeMessage(final T email) throws MessagingException {
        log.trace("Building Mime Message for emailHashcode='@{}'", email.hashCode());
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setTo(email.getReceiverEmail());
        mimeMessageHelper.setSubject(email.getTitle());
        mimeMessageHelper.setText(this.buildHtmlMessage(email), true);

        Resource appLogo = resourceLoader.getResource(
                ResourceLoader.CLASSPATH_URL_PREFIX + APP_LOGO_PATH);
        mimeMessageHelper.addInline(APP_LOGO, appLogo);

        return mimeMessage;
    }

    protected void processEmailSending(final T email) {
        log.trace("Processing email sending for emailHashcode='@{}'", email.hashCode());
        if (email.getReceiverEmail() == null || email.getReceiverEmail().isBlank() ||
                email.getTitle() == null || email.getTitle().isBlank()
        ) {
            log.error("BUG: Processed email has null or blank required field(s)! emailHashcode='@{}'", email.hashCode());
            throw new IllegalStateException("Email missing required fields: " + email);
        }
        try {
            javaMailSender.send(this.buildMimeMessage(email));
            log.info("Successfully processed email sending. Email sent.");
        } catch (MessagingException e) {
            log.error("Something go wrong on building an email message: {}", e.getMessage());
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}