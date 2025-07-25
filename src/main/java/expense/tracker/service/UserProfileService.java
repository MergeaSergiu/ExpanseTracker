package expense.tracker.service;

import expense.tracker.dto.UserProfilePage;

public interface UserProfileService {

    UserProfilePage getUserProfilePage(String authHeader);

}
