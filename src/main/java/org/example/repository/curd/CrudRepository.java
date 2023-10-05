package org.example.repository.curd;

import org.example.model.Funko;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface CrudRepository<T,ID> {
    CompletableFuture<Funko> save(T t) throws SQLException;
    CompletableFuture<Funko> update(T t) throws SQLException;
    CompletableFuture<Funko> findById(ID id) throws SQLException;
    CompletableFuture<ArrayList<Funko>> findAll() throws SQLException;
    CompletableFuture<Boolean> deleteById(ID id) throws SQLException;
    CompletableFuture<Void> deleteAll() throws SQLException;
}
