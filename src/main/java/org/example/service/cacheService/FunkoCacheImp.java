package org.example.service.cacheService;

import org.example.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunkoCacheImp implements FunkoCache {
    private Map<Long,Funko> cache;
    private int maxSize;
    private final ScheduledExecutorService cleaner;
    private Logger logger = LoggerFactory.getLogger(FunkoCacheImp.class);

    public FunkoCacheImp(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<Long,Funko>(maxSize,0.75F,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long,Funko> eldest){return size() > maxSize;}
        };

        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear,1,1, TimeUnit.MINUTES);

    }


    @Override
    public void put(Long key, Funko value) {
        logger.debug("Añadiendo Funko a cache con id: " + key + " y valor: " + value);
        cache.put(key,value);
    }

    @Override
    public Funko get(Long key) {
        logger.debug("Obteneidno Funko desde cache con id: " + key);
        return cache.get(key);
    }

    @Override
    public void remove(Long key) {
        logger.debug("Borrando Funko desde cache con id: " + key);
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.entrySet().removeIf(funko ->{
            boolean shouldRemove = funko.getValue().getUpdated_at().plusMinutes(1).isBefore(LocalDateTime.now());
            if (shouldRemove){
                logger.debug("Eliminando por la cadudcida funko de cache con id: " + funko.getKey());
            }
            return shouldRemove;
        });
    }

    @Override
    public void shutdown() {
        cleaner.shutdown();
    }
}
