package expense.tracker.service;

import expense.tracker.configuration.RabbitMQConfig;
import expense.tracker.dto.BudgetAlertEmailMessage;
import expense.tracker.entity.BudgetAlert;
import expense.tracker.entity.Expense;
import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.User;
import expense.tracker.repository.BudgetAlertRepository;
import expense.tracker.repository.ExpenseCategoryRepository;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetAlertSchedulerService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetAlertRepository budgetAlertRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public BudgetAlertSchedulerService(UserRepository userRepository,
                                       ExpenseRepository expenseRepository,
                                       BudgetAlertRepository budgetAlertRepository,
                                       RabbitTemplate rabbitTemplate, ExpenseCategoryRepository expenseCategoryRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.budgetAlertRepository = budgetAlertRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.expenseCategoryRepository = expenseCategoryRepository;
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

            List<ExpenseCategory> budgetedCategories = expenseCategoryRepository
                    .findByUserId(user.getId())
                    .stream()
                    .filter(cat -> cat.getBudgetLimit() != null && cat.getBudgetLimit() > 0)
                    .toList();

            for (ExpenseCategory category : budgetedCategories) {
                double total = categoryTotals.getOrDefault(category, 0.0);

                Optional<BudgetAlert> optionalAlert = budgetAlertRepository.findByUserIdAndExpenseCategoryId(user.getId(), category.getId());

                BudgetAlert alert = optionalAlert.orElseGet(() -> {
                    BudgetAlert newAlert = new BudgetAlert();
                    newAlert.setUser(user);
                    newAlert.setExpenseCategory(category);
                    return newAlert;
                });

                boolean shouldSendAlert = total > category.getBudgetLimit() &&
                        (alert.getLastSent() == null || ChronoUnit.DAYS.between(alert.getLastSent(), now) >= 15);
                if (shouldSendAlert) {

                    BudgetAlertEmailMessage message = new BudgetAlertEmailMessage(
                            user.getUsername(),
                            category.getName(),
                            category.getBudgetLimit(),
                            total,
                            user.getUsername()
                    );
                    rabbitTemplate.convertAndSend(RabbitMQConfig.BUDGET_QUEUE, message);

                    alert.setLastSent(now);
                    budgetAlertRepository.save(alert);
                }
            }
        }
    }
}
