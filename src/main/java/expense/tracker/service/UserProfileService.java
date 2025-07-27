package expense.tracker.service;

import expense.tracker.dto.DashboardInfo;
import expense.tracker.dto.UserProfilePage;

public interface UserProfileService {

    UserProfilePage getUserProfilePage(String authHeader);

    DashboardInfo dashboardInformation(String authHeader);

}
