package Service;

import DAO.UserDAO;
import Model.Account;

public class UserService {

    private final UserDAO userDAO;

    // Constructor to inject the UserDAO
    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }


    /**
     * Validates and registers a new account
     * @param userName the username for the account
     * @param password the password for the account
     * @return the created Account object
     * @throws IllegalArgumentException if validation fails
     */

     public Account registerUser(String userName, String password){

        if(userName == null || userName.trim().isEmpty()){
            throw new IllegalArgumentException("Username cannot be empty");
        }

        // validate password
        if(password == null || password.length() < 4){
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }

        if (userDAO.checkExistingUser(userName)){
            throw new IllegalArgumentException("Username already exists");
        }

        return userDAO.registerUser(userName, password);
     }


    
}
