package expense.tracker.service;

import expense.tracker.dto.ExpenseDto;
import expense.tracker.dto.ExpenseUpdateDto;
import expense.tracker.dto.ExpensesResponse;
import expense.tracker.dto.TopExpenses;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ExpenseService {
    void createExpense(@Valid ExpenseDto expenseDto, String authHeader);

    List<TopExpenses> getTop5expenses(String authHeader);

    void storeExpenseDocument(MultipartFile file, String authHeader, Long expenseId) throws IOException;

    List<ExpensesResponse> getExpensesByCategory(String authHeader, Long expenseCategoryId);

    void deleteExpense(String authHeader, Long expenseId);

    URL downloadExpenseDocument(String authHeader, String fileKey) throws IOException;

    void updateExpense( Long expenseId, ExpenseUpdateDto expenseUpdateDto);
}
