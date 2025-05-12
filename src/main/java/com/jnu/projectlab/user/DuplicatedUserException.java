// 새로운 파일: DuplicatedUserException.java
package com.jnu.projectlab.user;

public class DuplicatedUserException extends RuntimeException {
    public DuplicatedUserException(String message) {
        super(message);
    }
}