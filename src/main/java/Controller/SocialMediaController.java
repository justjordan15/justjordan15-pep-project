package Controller;

import java.util.List;


import DAO.MessageDAO;
import DAO.UserDAO;
import Exceptions.UnauthorizedException;
import Model.Account;
import Model.Message;
import Service.MessageService;
import Service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    UserService userService;
    MessageService messageService;

    // Constructor for dependency
    public SocialMediaController(){
        this.userService = new UserService(new UserDAO()); 
        this.messageService = new MessageService(new MessageDAO());
    }



    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::handleReigister);
        app.post("/login", this::handleLogin);
        app.post("/messages", this::handleCreateMessage);
        app.get("/messages", this::handleGetAllMessages);
        app.get("messages/{message_id}", this::handleGetMessageById);
        app.delete("/messages/{message_id}", this::handleDeleteMessageById);
        app.patch("/messages/{message_id}", this::handleUpdateMessageText);
        app.get("/accounts/{account_id}/messages", this::hadleGetMessagesByUserId);

        return app;
    }




    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void handleReigister(Context ctx) {
        try {
            // Parse JSON body into account
            Account requestAccount = ctx.bodyAsClass(Account.class);

            // Register the user with service layer
            Account createdAccount = userService.registerUser(requestAccount.getUsername(), requestAccount.getPassword());

            ctx.status(200).json(createdAccount);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("");
        } catch(Exception e){
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }


    private void handleLogin(Context ctx){

        try {
            // Pase JSON body into account
            Account requestAccount = ctx.bodyAsClass(Account.class);
            
            // Validate the login using the service layer
            Account loggedIAccount = userService.login(requestAccount.getUsername(), requestAccount.getPassword());

            ctx.status(200).json(loggedIAccount);
        } catch (UnauthorizedException e){
            ctx.status(401).result("");
        } catch(IllegalArgumentException e){
            ctx.status(400).result(e.getMessage());
        } catch(Exception e){
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }



    /**
     * Handles the POST /message endpoint
     * @param ctx The Javalin Contect object that manages the HTTP rquest and response
     * 
     */
    private void handleCreateMessage(Context ctx){
        try {
            Message requestMessage = ctx.bodyAsClass(Message.class);

            // Validate and Create
            Message createdMessage = messageService.createMessage(requestMessage);

            ctx.status(200).json(createdMessage);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("");
        } catch (Exception e){
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }


    /**
     * Handles GET /messages endpoint
     * @param ctx The Javalin Contect object that manages the HTTP request and response. 
     */
    private void handleGetAllMessages(Context ctx){
        try {
            List<Message> allMessages = messageService.getAllMessages();

            ctx.status(200).json(allMessages);
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }

    }


    private void handleGetMessageById(Context ctx){
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));

            Message message = messageService.getMessageById(messageId);

            if(message == null){
                ctx.status(200).json("");
            } else{
                ctx.status(200).json(message);
            }

        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getLocalizedMessage());
        }
    }


    /**
     * Handles the DELETE /messages/{message_id} endpoint.
     * 
     * @param ctx The Javalin Context object that manages the HTTP request and response.
     */
    private void handleDeleteMessageById(Context ctx){
        try {
            
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));

            Message deletedMessage = messageService.deleteMessageByID(messageId);

            if(deletedMessage != null){
                ctx.status(200).json(deletedMessage);
            } else{
                ctx.status(200).result("");
            }
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }


    /**
     * Handles the PATCH /messages/{message_id} endpoint
     * @param ctx The Javalin Context object that manages the HTTP request and response
     */
    private void handleUpdateMessageText(Context ctx){
        try {
            
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));

            Message requestBody = ctx.bodyAsClass(Message.class);
            String newMessageText = requestBody.getMessage_text();

            Message updatedMessage = messageService.updateMessageTextById(messageId, newMessageText);

            ctx.status(200).json(updatedMessage);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }

    }



    /**
     * Handles the GET /accounts/{account_id}/messages endpoint
     * 
     * @param ctx The Javalin Context object that manages the HTTP request and response
     */
    private void hadleGetMessagesByUserId(Context ctx){

        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));

            List<Message> userMessages = messageService.getMessagesByUserId(accountId);

            ctx.status(200).json(userMessages);
        } catch (NumberFormatException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }

    }


}