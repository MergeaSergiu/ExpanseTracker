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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    //@Scheduled(cron = "0 0 9 * * ?") // Every day at 09:00 AM (Midnight)
    @Scheduled(cron = "0 0 0 * * 0") // Runs every Sunday at 0:00
    public void sendBudgetExceedingAlerts() {
        LocalDate now = LocalDate.now();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Expense> userExpenses = expenseRepository.findByUser(user);


            List<Expense> currentMonthExpenses = userExpenses.stream()
                    .filter(expense -> expense.getDate().getMonth() == now.getMonth()
                            && expense.getDate().getYear() == now.getYear())
                    .toList();

            Map<ExpenseCategory, Double> categoryTotals = currentMonthExpenses.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getExpenseCategory,
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            List<BudgetAlert> userAlerts = budgetAlertRepository.findByUser(user);

            for (BudgetAlert alert : userAlerts) {
                double total = categoryTotals.getOrDefault(alert.getExpenseCategory(), 0.0);

                boolean alertRecentlySent = alert.getLastSent() != null &&
                        ChronoUnit.DAYS.between(alert.getLastSent(), now) < 15;
                if (total > alert.getBudgetLimit() && !alertRecentlySent) {

                    BudgetAlertEmailMessage message = new BudgetAlertEmailMessage(
                            alert.getUser().getUsername(),
                            alert.getExpenseCategory().getName(),
                            alert.getBudgetLimit(),
                            total,
                            alert.getUser().getUsername()
                    );
                    rabbitTemplate.convertAndSend(RabbitMQConfig.BUDGET_QUEUE, message);

                    alert.setLastSent(now);
                    budgetAlertRepository.save(alert);
                }
            }
        }
    }
}
