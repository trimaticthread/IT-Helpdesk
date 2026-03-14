package com.helpdesk.domain.exception;

/*
    Kullancı yetkisi olmayan bir işlem yapmaya çalışırsa sen napıyon diye fırlatır.
    Mesela customer rolünde biri başkasının ticketini silmeye çalışırsa tak diye 403 forbidden yer.
 */

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
