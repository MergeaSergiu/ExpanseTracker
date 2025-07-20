package expense.tracker.dto;

import java.time.LocalDate;

public record ExpenseDataResponse(
        String name,
        double amount,
        LocalDate expenseDate
) { }
