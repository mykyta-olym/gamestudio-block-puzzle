package sk.tuke.gamestudio.services;

import sk.tuke.gamestudio.database.DatabaseException;
import sk.tuke.gamestudio.entity.Player;

public interface IUserAuthenticationService {
    String authenticateUser() throws DatabaseException;
    public Player logInUser(String username, String password) throws AuthenticationException;
    public Player registerUser(String username, String password) throws AuthenticationException;

    public void changePassword(String username, String oldPassword, String newPassword) throws AuthenticationException;
    public String changeUsername(String oldUsername, String password, String newUsername) throws AuthenticationException;
}
