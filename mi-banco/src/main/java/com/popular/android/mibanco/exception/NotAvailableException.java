package com.popular.android.mibanco.exception;

/**
 * Exception thrown when a user has to login to the full site before using mobile banking.
 */
public class NotAvailableException extends BankException {

    /**
     * An unique serial version UID.
     */
    private static final long serialVersionUID = 4285814825062611L;

    /**
     * Instantiates a new NotAvailableException.
     * 
     * @param message the exception message
     */
    public NotAvailableException(String message) {
        super(message);
    }
}
