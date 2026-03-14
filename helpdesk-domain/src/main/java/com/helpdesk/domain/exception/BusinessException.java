package com.helpdesk.domain.exception;

/*
    Sistem işleyişine aykırı bir durumda fırlatır
    Örnek olarak CLOSED ticket a yorum eklemeye çalışılırsa 400 bad request yer.
 */

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
