package com.example.taskmanager.exception;

public class NotValidTaskException extends Exception{
    public NotValidTaskException(){
        super("Not valid task.");
    }
    public NotValidTaskException(String message){
        super(message);
    }
}
