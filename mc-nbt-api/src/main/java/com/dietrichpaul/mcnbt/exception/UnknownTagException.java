package com.dietrichpaul.mcnbt.exception;

public class UnknownTagException extends RuntimeException {

    private final int id;

    public UnknownTagException(int id) {
        this("Unknown tag id: " + id, id);
    }

    public UnknownTagException(String message, int id) {
        super(message);
        this.id = id;
    }

    public UnknownTagException(String message, Throwable cause, int id) {
        super(message, cause);
        this.id = id;
    }

    public UnknownTagException(Throwable cause, int id) {
        super(cause);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
