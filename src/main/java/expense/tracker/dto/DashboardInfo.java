package expense.tracker.dto;

import java.util.List;

public record DashboardInfo(
        Double totalAmountSpent,
        Integer categories,
        List<ExpenseDataResponse> last3Expenses
) {
}
