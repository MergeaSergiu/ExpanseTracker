package expense.tracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import expense.tracker.configuration.RabbitMQConfig;
import expense.tracker.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailConsumer {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailConsumer(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EmailRequest request = objectMapper.readValue(message, EmailRequest.class);

            // Now send the email using JavaMailSender
            consumeAuthEmail(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void consumeAuthEmail(EmailRequest emailRequest) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailRequest.to());
        helper.setFrom("expense@domain.com");
        helper.setSubject("Account activation");

        // Prepare the Thymeleaf context with variables
        Context context = new Context();
        context.setVariable("email", emailRequest.to());  // or pass a better param if you have it

        // Process the HTML template with context
        String htmlContent = templateEngine.process("welcome-email", context);

        helper.setText(htmlContent, true); // Enable HTML

        mailSender.send(message);
    }
}
