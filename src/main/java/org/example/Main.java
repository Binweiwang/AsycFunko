package org.example;


import org.example.model.Funko;
import org.example.repository.funko.FunkoRepository;
import org.example.repository.funko.FunkoRepositoryImp;
import org.example.service.DatabaseManager;
import org.example.service.funko.FunkoService;
import org.example.service.funko.FunkoServiceImp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;


public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException, ExecutionException, IOException {
        FunkoService funkoService = FunkoServiceImp.getInstance(FunkoRepositoryImp.getInstance(DatabaseManager.getInstance()));
//        funkoService.importar();
//        List<Funko> all = funkoService.findAll();
//        for (Funko funko : all) {
//            System.out.println(funko);
//        }
//        funkoService.exportar("D:\\Proyecto\\AsycFunko\\AsycFunko\\data\\funkos.json");

    }
}