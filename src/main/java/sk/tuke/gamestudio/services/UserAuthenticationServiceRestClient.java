package sk.tuke.gamestudio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Player;

@Service
public class UserAuthenticationServiceRestClient implements IUserAuthenticationService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/auth";

    @Autowired
    public UserAuthenticationServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String authenticateUser() {
        throw new UnsupportedOperationException("Direct authentication via REST client not supported. Sry but only login or register methods.");
    }

    @Override
    public Player logInUser(String username, String password) {
        String url = baseUrl + "/login";
        return restTemplate.postForObject(url, new AuthRequest(username, password), Player.class);
    }

    @Override
    public Player registerUser(String username, String password) {
        String url = baseUrl + "/register";
        return restTemplate.postForObject(url, new AuthRequest(username, password), Player.class);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        String url = baseUrl + "/change-password";
        restTemplate.postForObject(url, new ChangePasswordRequest(username, oldPassword, newPassword), Void.class);
    }
    @Override
    public String changeUsername(String username, String password, String newUsername) {
        String url = baseUrl + "/change-username";
        restTemplate.postForObject(url, new ChangeUsernameRequest(username, password, newUsername), Void.class);
        return newUsername;
    }

    static class ChangeUsernameRequest {
        private String username;
        private String password;
        private String newUsername;

        public ChangeUsernameRequest(String username, String password, String newUsername) {
            this.username = username;
            this.password = password;
            this.newUsername = newUsername;
        }
    }
    static class AuthRequest {
        private String username;
        private String password;

        public AuthRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    static class ChangePasswordRequest {
        private String username;
        private String oldPassword;
        private String newPassword;

        public ChangePasswordRequest(String username, String oldPassword, String newPassword) {
            this.username = username;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }
    }
}
