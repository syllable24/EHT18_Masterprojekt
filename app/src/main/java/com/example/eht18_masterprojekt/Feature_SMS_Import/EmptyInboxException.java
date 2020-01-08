package com.example.eht18_masterprojekt.Feature_SMS_Import;

public class EmptyInboxException extends Exception {
    EmptyInboxException(){
        super();
    }
    EmptyInboxException(String message){
        super(message);
    }
}
