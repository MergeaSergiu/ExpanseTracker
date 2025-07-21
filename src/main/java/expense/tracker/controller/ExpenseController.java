package expense.tracker.controller;


import expense.tracker.dto.ExpenseCategoryResponse;
import expense.tracker.dto.ExpenseDto;
import expense.tracker.dto.ExpensesResponse;
import expense.tracker.dto.TopExpenses;
import expense.tracker.service.ExpenseService;
import expense.tracker.service.S3Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService, S3Service s3Service) {
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

    @GetMapping("/five")
    public ResponseEntity<List<TopExpenses>> getTop5expense(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return ResponseEntity.ok(expenseService.getTop5expenses(authHeader));
    }

    @PutMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                             @RequestParam Long expenseId) throws IOException {
        expenseService.storeExpenseDocument(file, authHeader, expenseId);
        return ResponseEntity.ok("Document registered to your expense " + file);
    }
}



