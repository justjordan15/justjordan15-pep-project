package Exceptions;

/*
 * Custome exception for unauthorized access, such as invalid login credentials
 */

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message){
        super(message);
    }    
}
