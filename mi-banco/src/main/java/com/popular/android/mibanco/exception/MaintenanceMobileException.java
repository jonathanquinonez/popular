package com.popular.android.mibanco.exception;

/**
 * Exception thrown when mobile banking is undergoing maintenance.
 */
public class MaintenanceMobileException extends BankException {

    /**
     * An unique serial version UID.
     */
    private static final long serialVersionUID = 6906346517615592424L;

    /**
     * Instantiates a new MaintenanceMobileException.
     * 
     * @param message the exception message
     */
    public MaintenanceMobileException(final String message) {
        super(message);
    }
}
