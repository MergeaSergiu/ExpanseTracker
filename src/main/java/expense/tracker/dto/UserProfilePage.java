package expense.tracker.dto;

public record UserProfilePage(
        String firstName,
        String lastName,
        String username,
        Double totalExpense
) { }
