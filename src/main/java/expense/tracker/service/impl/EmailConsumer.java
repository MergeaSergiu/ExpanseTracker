package expense.tracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import expense.tracker.configuration.RabbitMQConfig;
import expense.tracker.dto.BudgetAlertEmailMessage;
import expense.tracker.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    @RabbitListener(queues = RabbitMQConfig.BUDGET_QUEUE)
    public void consumeBudgetAlert(BudgetAlertEmailMessage message) throws MessagingException {

        try {
            consumeAlertEmail(message);
        }catch (Exception e){
            e.printStackTrace();
        }
        // You can trigger your EmailService to send the actual email here
    }

    private void consumeAlertEmail(BudgetAlertEmailMessage message) throws MessagingException {
        Context context = new Context();
        context.setVariable("username", message.username());
        context.setVariable("category", message.category());
        context.setVariable("budgetLimit", message.budgetLimit());
        context.setVariable("currentAmount", message.currentAmount());

        String htmlContent = templateEngine.process("budget-exceeded", context);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(message.to());
            helper.setFrom("expense@domain.com");
            helper.setSubject("Budget Limit Exceeded");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private void consumeAuthEmail(EmailRequest emailRequest) throws MessagingException {
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
