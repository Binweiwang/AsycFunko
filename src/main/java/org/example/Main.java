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
        System.out.println("Importando datos..." + funkoService.importar().get());
        System.out.println("Exportando datos..." + funkoService.exportar("funkos.json").get());
        System.out.println("Funko mas caro: " + funkoService.funkoMasCaro().get());
        System.out.println("Media de precio de los funkos: " + funkoService.mediaFunko().get());
        System.out.println("Funkos agrupados por modelos: " + funkoService.agrupadoPorModelos().get());
        System.out.println("Numero de funkos por modelo: " + funkoService.numeroFunkoPorModelos().get());
        System.out.println("Funkos lanzados en 2023: " + funkoService.funkoLanzados2023().get());
        System.out.println("Numero de funkos de Stitch: " + funkoService.numeroFunkosStitch().get());
    }
    }