package org.example.service.funko;

import org.example.model.Funko;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FunkoService {
    List<Funko> findAll() throws SQLException, ExecutionException, InterruptedException;
    List<Funko> findbyNombre(String nombre) throws SQLException, ExecutionException, InterruptedException;
    Optional<Funko> findById(long id) throws SQLException, ExecutionException, InterruptedException;

    Funko save(Funko funko) throws SQLException, ExecutionException, InterruptedException;

    Funko update(Funko funko) throws SQLException,  ExecutionException, InterruptedException;

    boolean deleteById(long id) throws SQLException, ExecutionException, InterruptedException;

    void deleteAll() throws SQLException, ExecutionException, InterruptedException;
    CompletableFuture<Void> importar() throws IOException,SQLException;
    void exportar(String ruta);
}

