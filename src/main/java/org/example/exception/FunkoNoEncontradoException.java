package org.example.exception;

public abstract class FunkoNoEncontradoException extends FunkoException{

    public FunkoNoEncontradoException(String mensaje){
        super(mensaje);
    }
}
