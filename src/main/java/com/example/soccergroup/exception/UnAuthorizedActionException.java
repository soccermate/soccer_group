package com.example.soccergroup.exception;

public class UnAuthorizedActionException extends RuntimeException{

    public UnAuthorizedActionException(String msg)
    {
        super(msg);
    }
}
