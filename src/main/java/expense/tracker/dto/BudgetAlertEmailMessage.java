package expense.tracker.dto;

import java.io.Serializable;

public record BudgetAlertEmailMessage (
        String username,
        String category,
        Double budgetLimit,
        double currentAmount,
        String to
) implements Serializable{
}
