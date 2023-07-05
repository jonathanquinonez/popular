package com.popular.android.mibanco.exception;

/**
 * Exception thrown when banking remote access is undergoing maintenance.
 */
public class MaintenancePersonalException extends BankException {

    /**
     * An unique serial version UID.
     */
    private static final long serialVersionUID = 3496643661040723984L;

    /**
     * Instantiates a new MaintenancePersonalException.
     * 
     * @param message the exception message
     */
    public MaintenancePersonalException(final String message) {
        super(message);
    }
}
