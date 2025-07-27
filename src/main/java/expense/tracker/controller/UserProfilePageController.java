package expense.tracker.controller;

import expense.tracker.dto.DashboardInfo;
import expense.tracker.dto.UserProfilePage;
import expense.tracker.service.UserProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class UserProfilePageController {

    private final UserProfileService userProfileService;

    public UserProfilePageController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfilePage> getUserProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return ResponseEntity.ok(userProfileService.getUserProfilePage(authHeader));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardInfo> dashboardInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return ResponseEntity.ok(userProfileService.dashboardInformation(authHeader));
    }

}
