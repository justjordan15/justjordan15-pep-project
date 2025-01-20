package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {

    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }

    /**
     * Validates and creates a new message. 
     * @param message The Message object containing the message text and posted_by user ID
     * @return The created Message object with its auto-generated message_id. 
     * @throws IllegalArgumentException If validation fails. 
     */

     public Message createMessage(Message message){
        // Validate message_text
        if(message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()){
            throw new IllegalArgumentException("Message text cannot be blank");
        }

        if(message.getMessage_text().length() > 255){
            throw new IllegalArgumentException("Message text cannot exceed 255 characters.");
        }

        int postedBy = message.getPosted_by();

        if(!messageDAO.doesUserExist(postedBy)){
            throw new IllegalArgumentException("The user does not exist");
        }
        if(message.getTime_posted_epoch() <= 0){
            throw new IllegalArgumentException("Invalid timestamp");
        }

        return messageDAO.createMessage(message);
     }

     /**
      * Retrives all messages from the database.
      *
      * @return A list of Message objects representing all messages in the database
      */

     public List<Message> getAllMessages(){
        return messageDAO.getAllMessages();
     }

     /**
      * Retrives a message by its ID.
      *
      * @param messageId The ID of the message to retrive
      * @return A Message object representing the message, or null if no such message exists.
      */
     public Message getMessageById(int messageId){
        return messageDAO.getMessageById(messageId);
     }


     /**
      * Deletes a message by its ID.
      *
      * @param messageId The ID of the message to delete.
      * @return The deleted Message object if it existed, or null if no such message existed.
      */
     public Message deleteMessageByID(int messageId){
        return messageDAO.deleteMessageById(messageId);
     }


     /**
      * Updates the message_text of a message by its ID.
      *
      * @param messageId The ID of the message to update.
      * @param newMessageText The new text for the message.
      * @return The updated Message object if successful
      * @throws IllegalArgumentException If the validation fails or the message does not exist.
      */

     public Message updateMessageTextById(int messageId, String newMessageText){

        if(newMessageText == null || newMessageText.trim().isEmpty()){
            throw new IllegalArgumentException("Message text cannot be blank");
        }
        
        if(newMessageText.length() > 255){
            throw new IllegalArgumentException("Message text cannot exceed 255 characters.");
        }

        Message updatedMessage = messageDAO.updMessageTextByID(messageId, newMessageText);

        if(updatedMessage == null){
            throw new IllegalArgumentException("Message with ID " + " does not excist");
        }
        
        return updatedMessage;
     }

     /**
      * Retrieves all messages written by a specific user.
      *
      * @param accountId The ID of the user whose messages need to be retrieved.
      * @return A list of Message objects written by the user, or an empty list if no messages exist.
      */
     public List<Message> getMessagesByUserId(int accountId){
        return messageDAO.getMessagesByUserId(accountId);
     }
    
}
