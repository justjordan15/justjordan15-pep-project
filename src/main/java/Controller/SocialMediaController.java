package Controller;

import DAO.UserDAO;
import Model.Account;
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

    // Constructor for dependency
    public SocialMediaController(){
        this.userService = new UserService(new UserDAO()); 
    }



    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::handleReigister);

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


}