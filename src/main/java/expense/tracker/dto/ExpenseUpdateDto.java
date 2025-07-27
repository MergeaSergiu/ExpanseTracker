package expense.tracker.dto;

import java.time.LocalDate;

public record ExpenseUpdateDto(
        String name,
        Double amount,
        LocalDate localDate
) {
}
