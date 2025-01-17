package Service;

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
    
}
