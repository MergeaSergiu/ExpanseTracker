package expense.tracker.dto;

public record LoginResponse(String accessToken, String refreshToken, String role) { }
