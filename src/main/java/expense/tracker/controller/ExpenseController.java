package expense.tracker.controller;


import expense.tracker.dto.ExpenseDto;
import expense.tracker.dto.ExpenseUpdateDto;
import expense.tracker.dto.ExpensesResponse;
import expense.tracker.dto.TopExpenses;
import expense.tracker.service.ExpenseService;
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

    @PostMapping("/download")
    public ResponseEntity<String> getDownloadUrl(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                 @RequestParam String fileKey) throws IOException {
        String url = expenseService.downloadExpenseDocument(authHeader, fileKey).toString();
        return ResponseEntity.ok(url);
    }

    @GetMapping("/byCategory")
    public ResponseEntity<List<ExpensesResponse>> getExpensesByCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestParam Long expenseCategoryId) {
       List<ExpensesResponse> expensesResponses = expenseService.getExpensesByCategory(authHeader, expenseCategoryId);
       return  ResponseEntity.ok(expensesResponses);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteExpense(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestParam Long expenseId) {
        expenseService.deleteExpense(authHeader, expenseId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateExpense(@RequestParam Long expenseId,
                                              @RequestBody ExpenseUpdateDto expenseUpdateDto){
        expenseService.updateExpense(expenseId, expenseUpdateDto);
        return ResponseEntity.noContent().build();
    }
}



