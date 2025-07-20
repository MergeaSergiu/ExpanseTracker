package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;

import expense.tracker.dto.ExpenseDataResponse;
import expense.tracker.dto.ExpenseDto;
import expense.tracker.dto.ExpensesResponse;
import expense.tracker.entity.Expense;
import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.User;
import expense.tracker.repository.ExpenseCategoryRepository;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.service.ExpenseService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UtilsMethod utilsMethod;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UtilsMethod utilsMethod, ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseRepository = expenseRepository;
        this.utilsMethod = utilsMethod;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    @Override
    public void createExpense(ExpenseDto expenseDto,  String authHeader) {

        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);

        ExpenseCategory expenseCategory = expenseCategoryRepository.findByIdAndUserId(expenseDto.categoryId(), user.getId()).orElse(null);
        if(expenseCategory == null) throw new EntityNotFoundException("This category does not exist");

        Expense expense = expenseRepository.findByUserIdAndName(user.getId(), expenseDto.name());
        if (expense != null) throw new EntityExistsException("Expense already exists");

        //public Expense(String name, Double amount, LocalDate date, ExpenseCategory expenseCategory, String documentURL, User user) {
        Expense newExpense = new Expense(expenseDto.name(), expenseDto.amount(), expenseDto.expenseDate(), expenseCategory,"document", user);

         expenseRepository.save(newExpense);
    }

    @Override
    public List<ExpensesResponse> getAllExpense(String authHeader) {

        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);

        List<Expense> allUserExpenses = expenseRepository.findByUser(user);

        Map<String, List<Expense>> grouped = allUserExpenses.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory().getName()));


        return grouped.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    List<Expense> expenses = entry.getValue();

                    List<ExpenseDataResponse> expenseList = expenses.stream()
                            .map(this::mapToExpenseDataResponse)
                            .toList();

                    return new ExpensesResponse(category, expenseList);
                })
                .toList();
    }

    private ExpenseDataResponse mapToExpenseDataResponse(Expense expense) {
        return new ExpenseDataResponse(expense.getName(), expense.getAmount(), expense.getDate());

    }
}
