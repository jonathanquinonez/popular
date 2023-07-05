package com.popular.android.mibanco.exception;

/**
 * Exception thrown when access to a users's account has been blocked.
 */
public class AccessBlockedException extends BankException {

    /**
     * An unique serial version UID.
     */
    private static final long serialVersionUID = -2561764954346868455L;

    private boolean exitToLogin;
    /**
     * Instantiates a new AccessBlockedException.
     * 
     * @param message the exception message
     */
    public AccessBlockedException(final String message) {
        super(message);
    }

    public AccessBlockedException(final String message, final boolean exitToLogin) {
        super(message);
        this.exitToLogin = exitToLogin;
    }

    public boolean isExitToLogin() {
        return exitToLogin;
    }
}
