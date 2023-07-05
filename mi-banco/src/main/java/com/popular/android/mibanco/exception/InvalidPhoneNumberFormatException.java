package com.popular.android.mibanco.exception;

/**
 * Exception class to implement invalid phone number formats exceptions
 */
public class InvalidPhoneNumberFormatException extends BankException {

    public InvalidPhoneNumberFormatException(final String message) {
        super(message);
    }
}
