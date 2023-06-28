package com.example.taskmanager.exception;

public class PullException extends Exception{
    public PullException(){
        super("Unable to pull.");
    }

    public PullException(String message){
        super(message);
    }
}
