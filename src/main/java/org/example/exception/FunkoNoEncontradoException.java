package org.example.exception;

public abstract class FunkoNoEncontradoException extends Exception {
    public FunkoNoEncontradoException(String mensaje){
        super(mensaje);
    }
}
