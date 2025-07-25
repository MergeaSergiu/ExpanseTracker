package expense.tracker.configuration;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "authenticationQueue";
    public static final String EXCHANGE = "emailExchange";
    public static final String ROUTING_KEY = "authemail";

    public static final String BUDGET_QUEUE = "budgetAlertQueue";
    public static final String BUDGET_EXCHANGE = "budgetExchange";
    public static final String BUDGET_ROUTING_KEY = "budget.email";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public Queue authenticationQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public DirectExchange authenticationExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding authenticationBinding(Queue authenticationQueue, DirectExchange authenticationExchange) {
        return BindingBuilder
                .bind(authenticationQueue)
                .to(authenticationExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Queue budgetAlertQueue() {
        return new Queue(BUDGET_QUEUE);
    }

    @Bean
    public DirectExchange budgetExchange() {
        return new DirectExchange(BUDGET_EXCHANGE);
    }

    @Bean
    public Binding budgetBinding(Queue budgetAlertQueue, DirectExchange budgetExchange) {
        return BindingBuilder
                .bind(budgetAlertQueue)
                .to(budgetExchange)
                .with(BUDGET_ROUTING_KEY);
    }
}
