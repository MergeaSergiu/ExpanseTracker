package expense.tracker.dto;


import java.util.List;

public record ExpensesResponse(
        String category,
        List<ExpenseDataResponse> expenseList
) {
}
