package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;
import expense.tracker.dto.UserProfilePage;
import expense.tracker.entity.User;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.service.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final ExpenseRepository expenseRepository;
    private final UtilsMethod utilsMethod;

    public UserProfileServiceImpl(ExpenseRepository ExpenseRepository, UtilsMethod utilsMethod) {
        this.expenseRepository = ExpenseRepository;
        this.utilsMethod = utilsMethod;
    }

    @Override
    public UserProfilePage getUserProfilePage(String authHeader) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authHeader);
        if (user == null) throw new EntityNotFoundException("User does not exist");
        Double totalExpensesAmount = expenseRepository.getTotalExpensesByUser(user);
        return new UserProfilePage(user.getFirstName(), user.getLastName(), user.getUsername(), totalExpensesAmount);
    }
}
