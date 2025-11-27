package pl.bartlomiej.emailservice.linkedemail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.bartlomiej.emailservice.common.service.AbstractEmailService;
import pl.bartlomiej.mumcommons.emailintegration.external.model.LinkedEmail;

@Service
public class LinkedEmailService extends AbstractEmailService<LinkedEmail> {

    private static final Logger log = LoggerFactory.getLogger(LinkedEmailService.class);
    private final TemplateEngine templateEngine;

    protected LinkedEmailService(JavaMailSender javaMailSender, ResourceLoader resourceLoader, TemplateEngine templateEngine) {
        super(javaMailSender, resourceLoader);
        this.templateEngine = templateEngine;
    }

    @Override
    protected String buildHtmlMessage(LinkedEmail email) {
        Context context = new Context();

        context.setVariable(LinkedEmailConstants.TITLE, email.getTitle());
        context.setVariable(LinkedEmailConstants.MESSAGE, email.getMessage());
        context.setVariable(LinkedEmailConstants.LINK, email.getLink());
        context.setVariable(LinkedEmailConstants.LINK_BUTTON_TEXT, email.getLinkButtonText());

        return templateEngine.process(LinkedEmailConstants.STANDARD_EMAIL_TEMPLATE_PATH, context);
    }


    @Override
    public LinkedEmail send(LinkedEmail email) {
        log.debug("Sending LinkedEmail.");
        if (email == null) {
            throw new IllegalArgumentException("LinkedEmail argument is null.");
        }
        super.processEmailSending(email);
        return email;
    }
}