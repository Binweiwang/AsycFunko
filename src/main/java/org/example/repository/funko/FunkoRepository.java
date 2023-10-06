package org.example.repository.funko;

import org.example.model.Funko;
import org.example.repository.curd.CrudRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface FunkoRepository extends CrudRepository<Funko, Integer> {
    CompletableFuture<ArrayList<Funko>> findByNombre(String nombre) throws SQLException;
    CompletableFuture<ArrayList<Funko>> csvToFunko() throws SQLException;
}