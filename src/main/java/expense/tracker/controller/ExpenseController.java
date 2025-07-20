package expense.tracker.controller;


import expense.tracker.dto.ExpenseCategoryResponse;
import expense.tracker.dto.ExpenseDto;
import expense.tracker.dto.ExpensesResponse;
import expense.tracker.entity.Expense;
import expense.tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping()
    public ResponseEntity<String> addExpense(
            @RequestBody @Valid ExpenseDto expenseDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        expenseService.createExpense(expenseDto, authHeader);
        return ResponseEntity.ok("Expense created");
    }

    @GetMapping
    public ResponseEntity<List<ExpensesResponse>> getAllExpenses(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        List<ExpensesResponse> expenseCategoryResponses = expenseService.getAllExpense(authHeader);
        return ResponseEntity.ok(expenseCategoryResponses);
    }
}



