package org.example;


import org.example.model.Funko;
import org.example.repository.funko.FunkoRepositoryImp;
import org.example.repository.funko.FunkoRepository;
import org.example.service.DatabaseManager;
import org.example.util.CsvCreater;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;


public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException, ExecutionException {
        var csvCreater = new CsvCreater();
        FunkoRepository funkoRepository = FunkoRepositoryImp.getInstance(DatabaseManager.getInstance());
        CompletableFuture<ArrayList<Funko>> arrayListCompletableFuture = csvCreater.csvToFunkos();
//        arrayListCompletableFuture.thenAccept(funkos -> {
//            for (Funko funko : funkos) {
//                try {
//                    funkoRepository.save(funko);
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
sleep(1000);

    }
}