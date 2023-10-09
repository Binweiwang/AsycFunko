package org.example.exception;

public abstract class FunkoNoAlmacenadoException extends FunkoException{
// Constructor
    public FunkoNoAlmacenadoException(String mensaje){
        super(mensaje);
    }
}
