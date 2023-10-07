package org.example;


import org.example.model.Funko;
import org.example.repository.funko.FunkoRepositoryImp;
import org.example.service.DatabaseManager;
import org.example.service.funko.FunkoService;
import org.example.service.funko.FunkoServiceImp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException, ExecutionException, IOException {

        FunkoService funkoService = FunkoServiceImp.getInstance(FunkoRepositoryImp.getInstance(DatabaseManager.getInstance()));

        funkoService.funkosStitch().get().forEach(System.out::println);
    }
    }