
package com.ninetwozero.assignment4.misc;

public class DatabaseException extends Exception {

    // Serialization UID
    private static final long serialVersionUID = -5996544211777333875L;

    // Constructs matching "The Parent"
    public DatabaseException() {
        super();
    }

    public DatabaseException(String detailMessage) {
        super(detailMessage);
    }

    public DatabaseException(Throwable throwable) {
        super(throwable);
    }

    public DatabaseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
