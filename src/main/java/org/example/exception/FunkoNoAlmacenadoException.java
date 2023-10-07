package org.example.exception;

public abstract class FunkoNoAlmacenadoException extends Exception{
    public FunkoNoAlmacenadoException(String mensaje){
        super(mensaje);
    }
}
