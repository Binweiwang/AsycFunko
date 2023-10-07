package org.example.service.funko;

import org.example.exception.FunkoException;
import org.example.exception.FunkoNoAlmacenadoException;
import org.example.model.Funko;
import org.example.service.cacheService.FunkoCache;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FunkoService {
    //CRUD
    List<Funko> findAll() throws Exception;
    List<Funko> findbyNombre(String nombre) throws SQLException, ExecutionException, InterruptedException, FunkoException;
    Optional<Funko> findById(long id) throws Exception;

    Funko save(Funko funko) throws SQLException, FunkoNoAlmacenadoException, ExecutionException, InterruptedException, FunkoException;

    Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException, FunkoException;

    boolean deleteById(long id) throws SQLException, ExecutionException, InterruptedException;

    void deleteAll() throws SQLException, ExecutionException, InterruptedException;
    CompletableFuture<Void> importar();
    CompletableFuture<Void> exportar(String ruta);
    CompletableFuture<Funko> funkoMasCaro();
    CompletableFuture<Double> mediaFunko();
    CompletableFuture<Map<String, List<Funko>>> agrupadoPorModelos();
    CompletableFuture<Map<String,Integer>> numeroFunkoPorModelos();
    CompletableFuture<List<Funko>> funkoLanzados2023();
    CompletableFuture<Integer> numeroFunkosStitch();
    CompletableFuture<List<Funko>> funkosStitch();
}

