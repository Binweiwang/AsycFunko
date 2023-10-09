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
    // Constructor privado
    public FunkoCacheImp(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<Long,Funko>(maxSize,0.75F,true){
            // Metodo que elimina el elemento mas antiguo
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long,Funko> eldest){return size() > maxSize;}
        };
        // Creacion del hilo que limpia la cache
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        // Limpieza de la cache cada minuto
        this.cleaner.scheduleAtFixedRate(this::clear,-1,1, TimeUnit.MINUTES);
    }

    /**
     * Metodo que añade un funko a la cache
     * @param key clave del funko
     * @param value funko a añadir
     */
    @Override
    public void put(Long key, Funko value) {
        logger.debug("Añadiendo Funko a cache con id: " + key + " y valor: " + value);
        cache.put(key,value);
    }

    /**
     * Metodo que obtiene un funko de la cache
     * @param key clave del funko
     * @return funko
     */
    @Override
    public Funko get(Long key) {
        logger.debug("Obteneidno Funko desde cache con id: " + key);
        return cache.get(key);
    }

    /**
     * Metodo que elimina un funko de la cache
     * @param key clave del funko
     */
    @Override
    public void remove(Long key) {
        logger.debug("Borrando Funko desde cache con id: " + key);
        cache.remove(key);
    }

    /**
     * Metodo que limpia la cache
     */
    @Override
    public void clear() {
        cache.entrySet().removeIf(funko ->{
            // Si el funko lleva mas de 2 minutos sin ser actualizado se elimina
            boolean shouldRemove = funko.getValue().getUpdated_at().plusMinutes(2).isBefore(LocalDateTime.now());
            if (shouldRemove){
                logger.debug("Eliminando por la cadudcida funko de cache con id: " + funko.getKey());
            }
            return shouldRemove;
        });
    }

    /**
     * Metodo que cierra o libera cache cuando ya no es necesaria
     */
    @Override
    public void shutdown() {
        cleaner.shutdown();
    }
}
