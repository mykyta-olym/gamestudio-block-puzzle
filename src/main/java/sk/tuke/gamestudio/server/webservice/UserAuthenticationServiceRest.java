package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.gamestudio.entity.Player;
import sk.tuke.gamestudio.services.UserAuthenticationServiceJPA;

@RestController
@RequestMapping("/auth")
public class UserAuthenticationServiceRest {

    private final UserAuthenticationServiceJPA authService;

    @Autowired
    public UserAuthenticationServiceRest(UserAuthenticationServiceJPA authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        if (request.getUsername() == null || request.getPassword() == null ||
            request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Username and password must not be empty\"}");
        }

        try {
            Player player = authService.logInUser(request.getUsername(), request.getPassword());
            if (player != null) {
                // Sending a JSON response with the username
                return ResponseEntity.ok("{\"username\":\"" + player.getUsername() + "\"}");
            } else {
                // Sending a JSON response for authentication failure
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"Authentication failed.\"}");
            }
        } catch (Exception e) {
            // Sending a JSON response for internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"An error occurred: " + e.getMessage() + "\"}");
        }
    }



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (request.getUsername() == null || request.getPassword() == null ||
            request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password must not be empty");
        }

        try {
            Player player = authService.registerUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("Registration successful. Welcome, " + player.getUsername() + "!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request.getUsername() == null || request.getOldPassword() == null || request.getNewPassword() == null ||
            request.getUsername().trim().isEmpty() || request.getOldPassword().trim().isEmpty() || request.getNewPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, old password, and new password must not be empty");
        }

        try {
            authService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password change failed: " + e.getMessage());
        }
    }

    @PostMapping("/change-username")
    public ResponseEntity<String> changeUsername(@RequestBody ChangeUsernameRequest request) {
        if (request.getUsername() == null || request.getPassword() == null || request.getNewUsername() == null ||
            request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty() || request.getNewUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, password, and new username must not be empty");
        }

        try {
            String newUsername = authService.changeUsername(request.getUsername(), request.getPassword(), request.getNewUsername());
            return ResponseEntity.ok("Username changed successfully. New username: " + newUsername + ".");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username change failed: " + e.getMessage());
        }
    }

    static class ChangeUsernameRequest {
        private String username;
        private String password;
        private String newUsername;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }
        public String getNewUsername() {
            return newUsername;
        }

    }
    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

    }

    static class ChangePasswordRequest extends AuthRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

    }
}
