package org.example;


import org.example.model.Funko;
import org.example.repository.funko.FunkoRepositoryImp;
import org.example.service.DatabaseManager;
import org.example.service.funko.FunkoService;
import org.example.service.funko.FunkoServiceImp;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;


public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException, ExecutionException {
        FunkoService funkoService = FunkoServiceImp.getInstance(FunkoRepositoryImp.getInstance(DatabaseManager.getInstance()));
        System.out.println("Convierte CSV a Funkos");
        List<Funko> funkos = funkoService.csvToFunko();
        for (Funko funko : funkos) {
           funkoService.save(funko);
        }
        System.out.println("Obetiendo todos los funkos");
        List<Funko> all = funkoService.findAll();
        for (Funko funko : all) {
            System.out.println(funko);
        }
        System.out.println("Buscar Funko por nombre Loki Mischief");
        List<Funko> lokiMischief = funkoService.findbyNombre("Loki Mischief");
        System.out.println(lokiMischief);
    }
}