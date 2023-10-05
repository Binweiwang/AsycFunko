package org.example.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {
    private static IdGenerator instance = null;
    private final Lock lock = new ReentrantLock(true);
    private long myid = 0;

    private IdGenerator() {
    }
    public synchronized static IdGenerator getInstance(){
        if(instance == null){
            return new IdGenerator();
        }
        return instance;
    }

    public long getMyid(){
        lock.lock();
        try{
            return this.myid;
        }finally {
            lock.unlock();
        }
    }

    public long setMyid(){
        lock.lock();
        try{
           return this.myid++;
        }finally {
            lock.unlock();
        }
    }
}
