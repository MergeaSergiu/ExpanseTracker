package expense.tracker.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ExpenseDto(

        @NotBlank(message = "Each expense must have a name")
        String name,

        Double amount,

        LocalDate expenseDate,

        Long categoryId
) { }
