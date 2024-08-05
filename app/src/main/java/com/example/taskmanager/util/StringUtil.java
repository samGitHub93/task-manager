package com.example.taskmanager.util;

public class StringUtil {
    public static String capFirstCharacter(String string){
        string = string.toLowerCase();
        String[] stringSplit = string.split(" ");
        StringBuilder newString = new StringBuilder();
        for(String s : stringSplit){
            s = s.substring(0,1).toUpperCase() + s.substring(1);
            newString.append(" ").append(s);
        }
        newString = new StringBuilder(newString.toString().trim());
        return newString.toString();
    }
}
