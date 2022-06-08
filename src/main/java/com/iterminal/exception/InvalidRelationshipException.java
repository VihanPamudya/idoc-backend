package com.iterminal.exception;



public class InvalidRelationshipException extends CustomException {

    public InvalidRelationshipException() {
        super(INVALID_RELATIONSHIP, "Invalid Relationship.");
    }

    public InvalidRelationshipException(String message) {
        super(INVALID_RELATIONSHIP, message);
    }
}
