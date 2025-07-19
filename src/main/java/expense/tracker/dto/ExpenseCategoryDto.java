package expense.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public record ExpenseCategoryDto (
        @NotBlank
        String name,

        @NotBlank
        String description
){}
