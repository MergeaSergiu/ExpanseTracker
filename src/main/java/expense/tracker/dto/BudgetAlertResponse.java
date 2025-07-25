package expense.tracker.dto;

public record BudgetAlertResponse(
        Long budgetId,
        Double budgetLimit,
        String categoryName
) {
}
