package org.example.repository.curd;

import org.example.model.Funko;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CrudRepository<T,ID,EX extends Exception> {
    //CRUD
    CompletableFuture<Funko> save(T t) throws SQLException,EX;
    CompletableFuture<Funko> update(T t) throws SQLException ,EX;
    CompletableFuture<Optional<Funko>> findById(Long id) throws SQLException;
    CompletableFuture<ArrayList<Funko>> findAll() throws SQLException;
    CompletableFuture<Boolean> deleteById(Long id) throws SQLException;
    CompletableFuture<Void> deleteAll() throws SQLException;


}
