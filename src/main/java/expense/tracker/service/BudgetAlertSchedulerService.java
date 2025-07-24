package expense.tracker.service;

import expense.tracker.configuration.RabbitMQConfig;
import expense.tracker.dto.BudgetAlertEmailMessage;
import expense.tracker.entity.BudgetAlert;
import expense.tracker.entity.Expense;
import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.User;
import expense.tracker.repository.BudgetAlertRepository;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetAlertSchedulerService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetAlertRepository budgetAlertRepository;
    private final RabbitTemplate rabbitTemplate;

    public BudgetAlertSchedulerService(UserRepository userRepository,
                                       ExpenseRepository expenseRepository,
                                       BudgetAlertRepository budgetAlertRepository,
                                       RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.budgetAlertRepository = budgetAlertRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Run once every 24 hours
    @Scheduled(cron = "0 0 9 * * ?") // Every day at 12:00 AM (Midnight)
    public void sendBudgetExceedingAlerts() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Expense> userExpenses = expenseRepository.findByUser(user);

            Map<ExpenseCategory, Double> categoryTotals = userExpenses.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getExpenseCategory,
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            List<BudgetAlert> userAlerts = budgetAlertRepository.findByUser(user);

            for (BudgetAlert alert : userAlerts) {
                double total = categoryTotals.getOrDefault(alert.getExpenseCategory(), 0.0);
                if (total > alert.getBudgetLimit()) {

                    BudgetAlertEmailMessage message = new BudgetAlertEmailMessage(
                            alert.getUser().getUsername(),
                            alert.getExpenseCategory().getName(),
                            alert.getBudgetLimit(),
                            total,
                            alert.getUser().getUsername()
                    );
                    rabbitTemplate.convertAndSend(RabbitMQConfig.BUDGET_QUEUE, message);
                }
            }
        }
    }
}
