package sk.tuke.gamestudio.services;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Player;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Scanner;

@Service
@Transactional
public class UserAuthenticationServiceJPA implements IUserAuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(UserAuthenticationServiceJPA.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String authenticateUser() {
        logger.info("Authenticating user");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("Do you want to Log In, Register, Change Password, or Change Username? (login/register/change/change-username)");
                String choice = scanner.nextLine().trim().toLowerCase();

                switch (choice) {
                    case "login":
                        return handleLogin(scanner);
                    case "register":
                        return handleRegistration(scanner);
                    case "change":
                        return handlePasswordChange(scanner);
                    case "change-username":
                        return handleChangeUsername(scanner);
                    default:
                        System.out.println("Invalid choice. Please choose 'login', 'register', 'change', or 'change-username'.");
                        break;
                }
            } catch (AuthenticationException e) {
                logger.error("Authentication error: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage() + ". Please try again.");
            } catch (Exception e) {
                logger.error("System error during authentication process", e);
                System.out.println("System error: " + e.getMessage() + ". Please contact support.");
            }
        }
    }

    private String handleLogin(Scanner scanner) throws AuthenticationException {
        System.out.println("Enter your username:");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }

        Player player = entityManager.find(Player.class, username);
        if (player == null) {
            throw new AuthenticationException("Username not found.");
        }

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            throw new AuthenticationException("Password cannot be empty.");
        }

        if (!BCrypt.checkpw(password, player.getPassword())) {
            throw new AuthenticationException("Invalid password.");
        }

        logger.info("User authenticated successfully: {}", username);
        System.out.println("User authenticated successfully. Welcome back, " + username + "!");
        return username;
    }

    private String handleRegistration(Scanner scanner) throws AuthenticationException {
        System.out.println("Choose a username:");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }

        if (entityManager.find(Player.class, username) != null) {
            throw new AuthenticationException("Username already exists.");
        }

        System.out.println("Set a password:");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            throw new AuthenticationException("Password cannot be empty.");
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        Player newPlayer = new Player(username, passwordHash);
        entityManager.persist(newPlayer);

        logger.info("New user registered successfully: {}", username);
        System.out.println("Registration successful. You can now log in, " + username + ".");
        return username;
    }

    private String handlePasswordChange(Scanner scanner) throws AuthenticationException {
        System.out.println("Enter your username:");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }

        Player player = entityManager.find(Player.class, username);
        if (player == null) {
            throw new AuthenticationException("Username not found.");
        }

        System.out.println("Enter your current password:");
        String currentPassword = scanner.nextLine();

        if (currentPassword.isEmpty()) {
            throw new AuthenticationException("Current password cannot be empty.");
        }

        if (!BCrypt.checkpw(currentPassword, player.getPassword())) {
            throw new AuthenticationException("Invalid current password.");
        }

        System.out.println("Enter your new password:");
        String newPassword = scanner.nextLine();

        if (newPassword.isEmpty()) {
            throw new AuthenticationException("New password cannot be empty.");
        }

        if (currentPassword.equals(newPassword)) {
            throw new AuthenticationException("New password cannot be the same as the current password.");
        }

        System.out.println("Confirm your new password:");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            throw new AuthenticationException("New password and confirmation password do not match.");
        }

        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        player.setPassword(newPasswordHash);
        entityManager.merge(player);

        logger.info("Password for user {} changed successfully.", username);
        System.out.println("Password changed successfully for " + username + ".");

        return username;
    }
    @Override
    public Player logInUser(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be null or empty.");
        }

        Player player = entityManager.find(Player.class, username);
        if (player == null) {
            throw new AuthenticationException("User not found with username: " + username);
        }

        if (!BCrypt.checkpw(password, player.getPassword())) {
            throw new AuthenticationException("Invalid password for user: " + username);
        }

        logger.info("User {} logged in successfully.", username);
        return player;
    }

    @Override
    public Player registerUser(String username, String password) throws AuthenticationException {
        if (entityManager.find(Player.class, username) != null) {
            throw new AuthenticationException("Username already exists.");
        }
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        Player newPlayer = new Player(username, passwordHash);
        entityManager.persist(newPlayer);
        logger.info("User {} registered successfully.", username);
        return newPlayer;
    }


    @Override
    public void changePassword(String username, String oldPassword, String newPassword) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be null or empty.");
        }

        Player player = entityManager.find(Player.class, username);
        if (player == null) {
            throw new AuthenticationException("User not found with username: " + username);
        }

        // Check if the old password is correct
        if (!BCrypt.checkpw(oldPassword, player.getPassword())) {
            throw new AuthenticationException("Invalid current password.");
        }

        // Check if the new password is empty
        if (newPassword.isEmpty()) {
            throw new AuthenticationException("New password cannot be empty.");
        }

        // Ensure the new password is different from the old password
        if (oldPassword.equals(newPassword)) {
            throw new AuthenticationException("New password cannot be the same as the current password.");
        }

        // Update the password
        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        player.setPassword(newPasswordHash);
        entityManager.merge(player);

        logger.info("Password changed successfully for user: {}", username);
    }
    @Override
    public String changeUsername(String oldUsername, String password, String newUsername) throws AuthenticationException {
        if (oldUsername == null || oldUsername.trim().isEmpty() || newUsername == null || newUsername.trim().isEmpty()) {
            throw new AuthenticationException("Usernames cannot be null or empty.");
        }

        if (oldUsername.equals(newUsername)) {
            throw new AuthenticationException("New username cannot be the same as the current username.");
        }

        Player oldPlayer = entityManager.find(Player.class, oldUsername);
        if (oldPlayer == null) {
            throw new AuthenticationException("User not found with username: " + oldUsername);
        }

        if (!BCrypt.checkpw(password, oldPlayer.getPassword())) {
            throw new AuthenticationException("Invalid password for user: " + oldUsername);
        }

        if (entityManager.find(Player.class, newUsername) != null) {
            throw new AuthenticationException("Username already exists: " + newUsername);
        }

        Player newPlayer = new Player(newUsername, oldPlayer.getPassword());

        entityManager.persist(newPlayer);
        entityManager.remove(oldPlayer); // Remove the old Player entity

        logger.info("Username changed successfully from {} to {}", oldUsername, newUsername);
        return newUsername;
    }

    private String handleChangeUsername(Scanner scanner) throws AuthenticationException {
        System.out.println("Enter your current username:");
        String oldUsername = scanner.nextLine().trim();

        System.out.println("Enter your password:");
        String password = scanner.nextLine().trim();

        System.out.println("Enter your new username:");
        String newUsername = scanner.nextLine().trim();

        return changeUsername(oldUsername, password, newUsername);
    }

}
