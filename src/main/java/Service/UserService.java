package Service;

import DAO.UserDAO;
import Exceptions.UnauthorizedException;
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





     /**
      * Validate the provided username and password to log in a user 
      *
      * Ensures the username and password are not blank, validates credentials
      * by querying the database through the UserDAO, and returns the corresponding
      * Account object if the credentials are valid
      *
      * @param userName The username provided by the user (cannot be null or blank).
      * @param password The password provided by the user (cannot be null or blank).
      * @return The account object containing the user's details if the login is successful.
      * @throws UnauthorizedException If the username and password do not match any account
      * in the database   
      */


     public Account login(String userName, String password){

        if(userName == null || userName.trim().isEmpty()){
            throw new IllegalArgumentException("Username cannot be blank");
        }

        if (password == null || password.trim().isEmpty()){
            throw new IllegalArgumentException("Password cannot be blank");
        }

        // Validate credentials
        Account account = userDAO.validateLogin(userName, password);

        if(account == null){
            throw new UnauthorizedException("Invalid username or password");
        }
        
        return account;
     }
}
