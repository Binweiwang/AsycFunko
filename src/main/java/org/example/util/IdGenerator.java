package org.example.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {
    // propiedades de la clase
    private static IdGenerator instance = null;
    private final Lock lock = new ReentrantLock(true);
    private long myid = 0;

    // constructor privado
    private IdGenerator() {
    }
    // metodo de singleton
    public synchronized static IdGenerator getInstance(){
        if(instance == null){
            instance = new IdGenerator();
        }
        return instance;
    }

    /**
     * Metodo que devuelve el siguiente id
     * @return id siguiente
     */
    public long getMyid(){
        lock.lock();
        try{
            return this.myid++;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Metodo que devuelve el id actual
     * @return id actual
     */
    public long setMyid(){
        lock.lock();
        try{
           return this.myid;
        }finally {
            lock.unlock();
        }
    }
}
