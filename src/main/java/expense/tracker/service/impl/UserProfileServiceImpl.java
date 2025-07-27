package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;
import expense.tracker.dto.DashboardInfo;
import expense.tracker.dto.ExpenseCategoryResponse;
import expense.tracker.dto.ExpenseDataResponse;
import expense.tracker.dto.UserProfilePage;
import expense.tracker.entity.Expense;
import expense.tracker.entity.User;
import expense.tracker.repository.ExpenseCategoryRepository;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.service.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UtilsMethod utilsMethod;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public UserProfileServiceImpl(ExpenseRepository ExpenseRepository, UtilsMethod utilsMethod, ExpenseRepository expenseRepository, ExpenseCategoryRepository expenseCategoryRepository) {
        this.utilsMethod = utilsMethod;
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfilePage getUserProfilePage(String authHeader) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        if (user == null) throw new EntityNotFoundException("User does not exist");
        return new UserProfilePage(user.getFirstName(), user.getLastName(), user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardInfo dashboardInformation(String authHeader) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        if (user == null) throw new EntityNotFoundException("User does not exist");
        Double totalExpenses = expenseRepository.getTotalExpensesByUser(user);
        Integer totalCategories = expenseCategoryRepository.countByUserId(user.getId());
        List<Expense> expenses = expenseRepository.findTop3ByUserIdOrderByDateDesc(user.getId());
        List<ExpenseDataResponse> expenseDataResponseList = expenses.stream()
                .map(expense -> new ExpenseDataResponse(expense.getName(), expense.getAmount(), expense.getDate()))
                .toList();

        return new DashboardInfo(totalExpenses, totalCategories, expenseDataResponseList);

    }
}
