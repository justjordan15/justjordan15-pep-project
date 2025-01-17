package DAO;
import java.sql.*;

import Model.Account;
import Util.ConnectionUtil;


public class UserDAO {


    /**
     * Registers a new user in the database by inserting their username and password
     * 
     * @param userName The username of the new user (must be unique)
     * @param password The password of the new user.
     * @return An Account object representing the newly created user, including the auto-generated ID
     * @throws RuntimeException if there is a database-related error or the account ID cannot be obtained
     */
    public Account registerUser(String userName, String password){

        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";

        try (Connection connect = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, password);

                preparedStatement.executeUpdate();


                try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        int id = generatedKeys.getInt(1); //Auto-generated ID
                        return new Account(id, userName, password);
                    } else{
                        throw new SQLException("no account ID Obtained");
                    }
                }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error Registering User");
        }
    }

    /**
     * Check if a username already exist in the database
     * @param userName the username to check
     * @return true if the username exist, else flase 
     */
    public boolean checkExistingUser(String userName){

        String sql = "SELECT 1 FROM account WHERE username = ?";

        try(Connection connect = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connect.prepareStatement(sql)){

            preparedStatement.setString(1, userName);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                return resultSet.next(); 
            } 

        }catch (SQLException e) {
            throw new RuntimeException("Error checking for existing user");
        }
    }


    public Account validateLogin(String userName, String password){

        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";

        try (Connection connect = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connect.prepareStatement(sql)){

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    int id = resultSet.getInt("account_id");
                    String username = resultSet.getString("username");
                    String dbPassword = resultSet.getString("password");

                    return new Account(id, username, dbPassword);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error validating login: " + e.getMessage());
        }

        return null;
    }
}


    

