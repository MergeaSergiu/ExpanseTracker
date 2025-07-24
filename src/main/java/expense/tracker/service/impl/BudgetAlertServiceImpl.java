package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;
import expense.tracker.dto.BudgetAlertDto;
import expense.tracker.dto.BudgetAlertResponse;
import expense.tracker.entity.BudgetAlert;
import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.User;
import expense.tracker.repository.BudgetAlertRepository;
import expense.tracker.repository.ExpenseCategoryRepository;
import expense.tracker.service.BudgetAlertService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetAlertServiceImpl implements BudgetAlertService {

    private final UtilsMethod utilsMethod;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final BudgetAlertRepository budgetAlertRepository;

    public BudgetAlertServiceImpl(UtilsMethod utilsMethod, ExpenseCategoryRepository expenseCategoryRepository, BudgetAlertRepository budgetAlertRepository) {
        this.utilsMethod = utilsMethod;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.budgetAlertRepository = budgetAlertRepository;
    }

    @Override
    public void createOrUpdateAlert(String authorization, BudgetAlertDto budgetDto) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);

        ExpenseCategory category = expenseCategoryRepository.findByIdAndUserId(budgetDto.categoryId(), user.getId()).orElse(null);
        if (category == null) throw new EntityNotFoundException("Category not found");

        Optional<BudgetAlert> existing = budgetAlertRepository.findByUserIdAndExpenseCategoryId(user.getId(), category.getId());

        BudgetAlert alert = existing.orElseGet(BudgetAlert::new);
        alert.setUser(user);
        alert.setExpenseCategory(category);
        alert.setBudgetLimit(budgetDto.budgetLimit());

        budgetAlertRepository.save(alert);
    }

    public List<BudgetAlertResponse> getUserAlerts(String authorization) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);
        return budgetAlertRepository.findByUser(user).stream()
                .map(budgetAlert -> new BudgetAlertResponse(budgetAlert.getId(), budgetAlert.getBudgetLimit(), budgetAlert.getExpenseCategory().getName()))
                .collect(Collectors.toList());
    }

//    public Optional<BudgetAlert> getUserAlertsByCategory(String authorization, Long categoryId) {
//        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);
//        return budgetAlertRepository.findByUserIdAndCategoryId(user.getId(), categoryId);
//    }
}
