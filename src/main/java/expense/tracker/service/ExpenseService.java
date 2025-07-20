package expense.tracker.service;

import expense.tracker.dto.ExpenseDto;
import expense.tracker.dto.ExpensesResponse;
import expense.tracker.dto.TopExpenses;
import jakarta.validation.Valid;

import java.util.List;

public interface ExpenseService {
    void createExpense(@Valid ExpenseDto expenseDto, String authHeader);

    List<ExpensesResponse> getAllExpense(String authHeader);

    List<TopExpenses> getTop5expenses(String authHeader);

}
