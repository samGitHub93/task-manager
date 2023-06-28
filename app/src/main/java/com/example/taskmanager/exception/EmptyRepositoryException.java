package com.example.taskmanager.exception;

public class EmptyRepositoryException extends Exception {
    public EmptyRepositoryException(){
        super("Repository is empty!");
    }

    public EmptyRepositoryException(String message){
        super(message);
    }
}
