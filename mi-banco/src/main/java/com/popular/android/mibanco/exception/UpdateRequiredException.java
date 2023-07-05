package com.popular.android.mibanco.exception;

/**
 * Exception thrown when mobile application version is outdated and have to be updated in order to get access to the banking interface.
 */
public class UpdateRequiredException extends BankException {

    /**
     * An unique serial version UID.
     */
    private static final long serialVersionUID = -6372613417211136783L;

    /**
     * Instantiates a new update required exception.
     * 
     * @param message the exception message
     */
    public UpdateRequiredException(final String message) {
        super(message);
    }
}
