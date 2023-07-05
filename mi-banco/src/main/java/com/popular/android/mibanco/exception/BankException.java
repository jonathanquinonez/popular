package com.popular.android.mibanco.exception;

/**
 * Exception thrown on a generic bank error.
 */
public class BankException extends Exception {

    private boolean backOnConfirm;

    private String title = null;

    /**
     * An unique serial version UID.
     */
    private static final long serialVersionUID = 3496643661040723984L;

    /**
     * Instantiates a new BankException.
     */
    public BankException() {
        super();
    }

    /**
     * Instantiates a new BankException.
     * 
     * @param message the exception message
     */
    public BankException(final String message) {
        super(message);
    }

    public BankException(final String message, final String title) {
        this(message);
        this.title = title;
    }

    public BankException(final String message, final boolean backOnUserAction) {
        super(message);
        this.backOnConfirm = backOnUserAction;
    }

    public BankException(final String message, String title, final boolean backOnUserAction) {
        super(message);
        this.backOnConfirm = backOnUserAction;
        this.title = title;
    }

    public boolean isBackOnConfirm() {
        return this.backOnConfirm;
    }

    public String getTitle() {
        return this.title;
    }

}
