package pl.bartlomiej.emailservice.standardemail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.bartlomiej.emailservice.common.service.AbstractEmailService;
import pl.bartlomiej.emailservice.common.service.EmailServiceImpl;
import pl.bartlomiej.mumcommons.emailintegration.external.model.StandardEmail;

@EmailServiceImpl
public class StandardEmailService extends AbstractEmailService<StandardEmail> {

    private static final Logger log = LoggerFactory.getLogger(StandardEmailService.class);
    private final TemplateEngine templateEngine;

    protected StandardEmailService(JavaMailSender javaMailSender, ResourceLoader resourceLoader, TemplateEngine templateEngine) {
        super(javaMailSender, resourceLoader);
        this.templateEngine = templateEngine;
    }

    @Override
    protected String buildHtmlMessage(StandardEmail email) {
        Context context = new Context();

        context.setVariable(StandardEmailConstants.TITLE, email.getTitle());
        context.setVariable(StandardEmailConstants.MESSAGE, email.getMessage());

        return templateEngine.process(StandardEmailConstants.STANDARD_EMAIL_TEMPLATE_PATH, context);
    }


    @Override
    public StandardEmail send(StandardEmail email) {
        log.debug("Sending StandardEmail.");
        if (email == null) {
            throw new IllegalArgumentException("StandardEmail argument is null.");
        }
        super.processEmailSending(email);
        return email;
    }
}