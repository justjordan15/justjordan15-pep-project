package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


    /**
     * Retrives all messages from the database.
     * 
     * @return A list of Message objects representing all messages in the database.
     *         Returns an empty list if there are no messages.
     */
    public List<Message> getAllMessages(){

        String sql = "SELECT * FROM message";
        List<Message> messages = new ArrayList<>();

        try (Connection connect = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement(sql)){

                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    while (resultSet.next()) {
                        int messageId = resultSet.getInt("message_id");
                        String messageText = resultSet.getString("message_text");
                        int postedBy = resultSet.getInt("posted_by");
                        long timePosted = resultSet.getLong("time_posted_epoch");

                        Message message = new Message(messageId, postedBy, messageText, timePosted);

                        messages.add(message);
                        
                    }
                }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all messages: " + e.getMessage());
        }

        return messages;

    }



    /**
     * Retrives a message from the databse by its ID.
     * 
     * @param messageId The ID of the message to retrieve.
     * @return A Message object representing the message, or null if so such message exists.
     */
    public Message getMessageById(int messageId){

        String sql = "SELECT * FROM message WHERE message_id = ?";

        try(Connection connect = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connect.prepareStatement(sql)) {

                preparedStatement.setInt(1, messageId);

                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if (resultSet.next()) {
                        int id = resultSet.getInt("message_id");
                        String messageText = resultSet.getString("message_text");
                        int postedBy = resultSet.getInt("posted_by");
                        long timePosted = resultSet.getLong("time_posted_epoch");

                        return new Message(id, postedBy, messageText, timePosted);
                    }
                }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving message by ID: " + e.getMessage(), e);
        }

        // If no message is found
        return null;

    }


    /**
     * Deletes a message fro mthe database by its ID.
     * 
     * @param messageId The ID of the message to delete.
     * @return The deleted message object if it existed, or null if no such message existed.
     */
    public Message deleteMessageById(int messageId){

        String selectSQL = "SELECT * FROM message WHERE message_id = ?";
        String deleteSQL = "DELETE FROM message WHERE message_id = ?";

        try (Connection connect = ConnectionUtil.getConnection()){

            try(PreparedStatement selectStmt = connect.prepareStatement(selectSQL)){
                selectStmt.setInt(1, messageId);

                try(ResultSet resultSet = selectStmt.executeQuery()){
                    if(resultSet.next()){
                        int id = resultSet.getInt("message_id");
                        String messageText = resultSet.getString("message_text");
                        int postedBy = resultSet.getInt("posted_by");
                        long timePosted = resultSet.getLong("time_posted_epoch");

                        Message message = new Message(id, postedBy, messageText, timePosted);

                        try(PreparedStatement deleteStmt = connect.prepareStatement(deleteSQL)){
                            deleteStmt.setInt(1, messageId);
                            deleteStmt.executeUpdate();
                        }

                        // The deleted message
                        return message;
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting message by ID: " + e.getMessage());
        }

        // Return null if no message was found
        return null;
    }



    /**
     * Updates the message_text of a message identified by its ID.
     * 
     * @param messageID The ID of the message to update
     * @param newMessageText The new text for the message
     * @return Thee updated Message object if successful, or null if the message does not exist.
     * @throws IllegalArgumentException If the message does not exist or the update fails.
     */
    public Message updMessageTextByID(int messageID, String newMessageText){

        String selectSQL = "SELECT * FROM message WHERE message_id = ?";
        String updateSQL = "UPDATE message SET message_text = ? WHERE message_id = ?";

        try (Connection connect = ConnectionUtil.getConnection()){
            
            try(PreparedStatement selectStmt = connect.prepareStatement(selectSQL)){
                selectStmt.setInt(1, messageID);

                try(ResultSet resultSet = selectStmt.executeQuery()){
                    if(resultSet.next()){

                        try(PreparedStatement updateStmt = connect.prepareStatement(updateSQL)){
                            updateStmt.setString(1, newMessageText);
                            updateStmt.setInt(2, messageID);
                            int rowsUpdated = updateStmt.executeUpdate();
                      
                            if(rowsUpdated > 0){
                                int id = resultSet.getInt("message_id");
                                String messageText = newMessageText;
                                int postedBy = resultSet.getInt("posted_by");
                                long timePosted = resultSet.getLong("time_posted_epoch");
    
                                return new Message(id, postedBy, messageText, timePosted);
                            }
                        }    
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating message text: " + e.getMessage(), e);
        }

        // Return null if no message was found
        return null;
    }



    /**
     * Retrieves all messages written by a specific user from the database.
     * 
     * @param accountId The ID of the user whose messages need to be retrieved.
     * @return A list of Message objects written by the user, or an empty list if no messages exist.
     */
    public List<Message> getMessagesByUserId(int accountId){

        String sql = "SELECT * FROM message WHERE posted_by = ?";
        List<Message> messages = new ArrayList<>();

        try(Connection connect = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement =connect.prepareStatement(sql)){

                preparedStatement.setInt(1, accountId);

                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    while (resultSet.next()) {
                        int messagId = resultSet.getInt("message_id");
                        String messageText = resultSet.getString("message_text");
                        long timePostedEpoch = resultSet.getLong("time_posted_epoch");

                        Message message = new Message(messagId, accountId, messageText, timePostedEpoch);
                        messages.add(message);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving messages by user ID: " + e.getMessage());
            }
        
        return messages;
    }
}
