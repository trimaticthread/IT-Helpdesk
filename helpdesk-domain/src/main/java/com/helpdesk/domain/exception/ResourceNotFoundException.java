package com.helpdesk.domain.exception;

/*
  Aranan kaynak veritabanında yoksa firlatir.
  Olmayan bir ticket ın id sini sorgularsan tak diye kafana 404 fırlatıyo ondan yok diye.
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String resourceName, Long id){
        super(resourceName + " bulunamadi. ID: " + id);
    }
}
