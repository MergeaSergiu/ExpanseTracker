package expense.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public record BudgetAlertDto(
        @NotBlank
        Long categoryId,

        @NotBlank
        Double budgetLimit
) { }
