package expense.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public class JwtRefreshToken {

    @NotBlank
    private String refresh_JWT;

    public JwtRefreshToken(String refresh_JWT) {
        this.refresh_JWT = refresh_JWT;
    }

    public String getRefresh_JWT(){
        return refresh_JWT;
    }

    public void setRefresh_JWT(String refresh_JWT){
        this.refresh_JWT = refresh_JWT;
    }

    public JwtRefreshToken(){}
}
