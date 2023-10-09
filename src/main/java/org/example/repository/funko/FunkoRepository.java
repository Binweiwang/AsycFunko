package org.example.repository.funko;

import org.apache.ibatis.jdbc.SQL;
import org.example.exception.FunkoException;
import org.example.model.Funko;
import org.example.repository.curd.CrudRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FunkoRepository extends CrudRepository<Funko, Long, FunkoException> {
    // Metodos propios de la entidad
    CompletableFuture<ArrayList<Funko>> findByNombre(String nombre) throws SQLException,FunkoException;
    CompletableFuture<List<Funko>> findByModelo(String modelo)throws SQLException,FunkoException;
}
