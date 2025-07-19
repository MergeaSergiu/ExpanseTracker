package expense.tracker.exception;

import java.security.Timestamp;
import java.time.LocalDateTime;

public class ErrorResponse {
    private String error;
    private LocalDateTime timestamp;
    private int httpStatus;

    public ErrorResponse(String error, LocalDateTime timestamp, int httpStatus) {
        this.error = error;
        this.timestamp = timestamp;
        this.httpStatus = httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
