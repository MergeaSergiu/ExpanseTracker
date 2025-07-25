package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;

import expense.tracker.dto.ExpenseDataResponse;
import expense.tracker.dto.ExpenseDto;
import expense.tracker.dto.ExpensesResponse;
import expense.tracker.dto.TopExpenses;
import expense.tracker.entity.Expense;
import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.User;
import expense.tracker.repository.ExpenseCategoryRepository;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.service.ExpenseService;
import expense.tracker.service.S3Service;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UtilsMethod utilsMethod;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final S3Service s3Service;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UtilsMethod utilsMethod, ExpenseCategoryRepository expenseCategoryRepository, S3Service s3Service) {
        this.expenseRepository = expenseRepository;
        this.utilsMethod = utilsMethod;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void createExpense(ExpenseDto expenseDto,  String authHeader) {

        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);

        ExpenseCategory expenseCategory = expenseCategoryRepository.findByIdAndUserId(expenseDto.categoryId(), user.getId()).orElse(null);
        if(expenseCategory == null) throw new EntityNotFoundException("This category does not exist");

        Expense expense = expenseRepository.findByUserIdAndName(user.getId(), expenseDto.name());
        if (expense != null) throw new EntityExistsException("Expense already exists");

        //public Expense(String name, Double amount, LocalDate date, ExpenseCategory expenseCategory, String documentURL, User user) {
        Expense newExpense = new Expense(expenseDto.name(), expenseDto.amount(), expenseDto.expenseDate(), expenseCategory,"document", user);

         expenseRepository.save(newExpense);
    }

    @Override
    public List<ExpensesResponse> getAllExpense(String authHeader) {

        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);

        List<Expense> allUserExpenses = expenseRepository.findByUser(user);

        Map<String, List<Expense>> grouped = allUserExpenses.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseCategory().getName()));


        return grouped.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();

                    List<Expense> expenses = entry.getValue();
                    Long categoryId = expenses.getFirst().getExpenseCategory().getId();

                    List<ExpenseDataResponse> expenseList = expenses.stream()
                            .map(this::mapToExpenseDataResponse)
                            .toList();

                    return new ExpensesResponse(category,categoryId, expenseList);
                })
                .toList();
    }

    @Override
    public List<TopExpenses> getTop5expenses(String authHeader) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        Pageable fifthOnly = PageRequest.of(0, 5);
        return expenseRepository.findTopExpenses(user, fifthOnly)
                .stream()
                .map(expense -> new TopExpenses(
                        expense.getName(),
                        expense.getExpenseCategory().getName(),
                        expense.getAmount(),
                        expense.getDate()
                ))
                .toList();
    }

    @Override
    public void storeExpenseDocument(MultipartFile file, String authHeader, Long expenseId) throws IOException {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        String documentURL = s3Service.uploadFile(file);
        Expense expense = expenseRepository.findByUserIdAndId(user.getId(), expenseId);
        if(expense == null) throw new EntityNotFoundException("This category does not exist");
        expense.setDocumentURL(documentURL);
        expenseRepository.save(expense);
    }

    @Override
    public List<ExpensesResponse> getExpensesByCategory(String authHeader, Long expenseCategoryId) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseCategoryId(user.getId(), expenseCategoryId);
        List<ExpenseDataResponse> expenseList = expenses.stream()
                .map(this::mapToExpenseDataResponse)
                .toList();
        String categoryName = expenses.isEmpty() ? "Unknown Category" : expenses.getFirst().getExpenseCategory().getName();
        Long categoryId = expenses.getFirst().getExpenseCategory().getId();

        return List.of(new ExpensesResponse(categoryName, categoryId, expenseList));
    }

    private ExpenseDataResponse mapToExpenseDataResponse(Expense expense) {
        return new ExpenseDataResponse(expense.getName(), expense.getAmount(), expense.getDate());
    }
}
