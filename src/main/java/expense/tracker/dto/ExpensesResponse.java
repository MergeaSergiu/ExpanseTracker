package expense.tracker.dto;


import java.util.List;

public record ExpensesResponse(
        String category,
        Long categoryId,
        Double totalAmount,
        List<ExpenseDataResponse> expenseList

) {
}
