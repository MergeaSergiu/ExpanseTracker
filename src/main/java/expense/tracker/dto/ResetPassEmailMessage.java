package expense.tracker.dto;

import java.io.Serializable;

public record ResetPassEmailMessage(
        String username,
        String code
) implements Serializable {
}
