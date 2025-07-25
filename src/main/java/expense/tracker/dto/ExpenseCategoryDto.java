package expense.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public record ExpenseCategoryDto (

        @NotBlank(message = "Please add a name for the category")
        String name,

        @NotBlank(message = "Please add a description for this category")
        String description
){}
