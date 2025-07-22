package expense.tracker.dto;

import java.io.Serializable;

public record EmailRequest (
    String to
) implements Serializable {}
