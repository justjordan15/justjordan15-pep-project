package DAO;

import java.sql.*;



import Model.Message;
import Util.ConnectionUtil;


public class MessageDAO {

    
    /**
     * Inserts a new message into the database
     * 
     * @param message The Message object containing the message text and posted_by user ID
     * @return The newly created Message object with its aut-generated message_id.
     * @throws IllegalArgumentException If the user (posted_by) does not exist.
     */

     public Message createMessage(Message message){

        String sql = "INSERT INTO message (message_text, posted_by, time_posted_epoch) VALUES (?, ?, ?)";

        try (Connection connect = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

                preparedStatement.setString(1, message.getMessage_text());
                preparedStatement.setInt(2, message.getPosted_by());
                preparedStatement.setLong(3, message.getTime_posted_epoch());

                preparedStatement.executeUpdate();

                // Retrieve the auto-generated message_id
                try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        int messageId = generatedKeys.getInt(1);
                     
                        return new Message(messageId, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
                    } else{
                        throw new SQLException("Failed to obtain message ID");
                    }
                }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error creating message: " + e.getMessage());
        }
     }


     /**
      * Checks if a user with the given account_id exists in the database.
      *
      * @param accuntId The account_id to check. 
      * @return True if the user exists, false otherwis. 
      */

      public boolean doesUserExist(int accountId){
        
        String sql = "SELECT 1 FROM account WHERE account_id = ?";

        try (Connection connect = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connect.prepareStatement(sql)){

            preparedStatement.setInt(1, accountId);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return resultSet.next();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if user exist");
        }
    }
}
