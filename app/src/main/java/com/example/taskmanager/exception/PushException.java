package com.example.taskmanager.exception;

public class PushException extends Exception {
    public PushException(){
        super("Unable to push.");
    }

    public PushException(String message) {
        super(message);
    }
}
