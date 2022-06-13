package io.github.clemenscode.atrifydonuts.utils;

public class DuplicatedOrderException extends Exception {
    public DuplicatedOrderException() {
        super("Only one order per Client is possible at a time!");
    }
}
