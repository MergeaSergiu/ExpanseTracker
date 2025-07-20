package expense.tracker.dto;

import java.time.LocalDate;

public record TopExpenses(String name, String category, Double amount, LocalDate expenseDate) {
}
