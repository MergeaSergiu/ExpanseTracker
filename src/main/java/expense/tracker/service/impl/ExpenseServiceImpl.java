package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;

import expense.tracker.dto.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ExpensesResponse> getExpensesByCategory(String authHeader, Long expenseCategoryId) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseCategoryId(user.getId(), expenseCategoryId);
        List<ExpenseDataResponse> expenseList = expenses.stream()
                .map(this::mapToExpenseDataResponse)
                .toList();

        double totalSumOfCategory = expenseList.stream()
                .mapToDouble(ExpenseDataResponse::amount)
                .sum();
        String categoryName = expenses.isEmpty() ? "Unknown Category" : expenses.getFirst().getExpenseCategory().getName();
        Long categoryId = expenses.getFirst().getExpenseCategory().getId();

        return List.of(new ExpensesResponse(categoryName, categoryId, totalSumOfCategory, expenseList));
    }

    @Override
    @Transactional
    public void deleteExpense(String authHeader, Long expenseId) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        Expense expense = expenseRepository.findByUserIdAndId(user.getId(), expenseId);
        if(expense == null) throw new EntityNotFoundException("This expense does not exist");
        expenseRepository.delete(expense);
    }

    @Override
    public URL downloadExpenseDocument(String authHeader, String fileKey) throws IOException {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        Expense expense = expenseRepository.findByUserIdAndDocumentURL(user.getId(), fileKey);
        if(expense == null) throw new EntityNotFoundException("This expense does not exist");
        return s3Service.download(expense.getDocumentURL());
    }

    @Override
    public void updateExpense(Long expenseId, ExpenseUpdateDto expenseUpdateDto) {
        Expense expense = expenseRepository.findById(expenseId).orElse(null);
        if(expense == null) throw new EntityNotFoundException("This expense does not exist");
        expense.setName(expenseUpdateDto.name());
        expense.setAmount(expenseUpdateDto.amount());
        expense.setDate(expenseUpdateDto.localDate());
        expenseRepository.save(expense);
    }

    private ExpenseDataResponse mapToExpenseDataResponse(Expense expense) {
        return new ExpenseDataResponse(expense.getName(), expense.getAmount(), expense.getDate());
    }
}
