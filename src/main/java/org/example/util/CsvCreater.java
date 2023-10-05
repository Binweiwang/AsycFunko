package org.example.util;

import org.example.model.Funko;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CsvCreater {

    public CompletableFuture<ArrayList<Funko>> csvToFunkos() {
        return CompletableFuture.supplyAsync(() -> {
            var listFunkos = new ArrayList<Funko>();
            String rutaCSV = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "funkos.csv";
            try(BufferedReader bf = new BufferedReader(new FileReader(rutaCSV))) {
                String linea;
                bf.readLine();
                while((linea = bf.readLine()) != null){
                    String [] campos = bf.readLine().split(",");

                    listFunkos.add(new Funko(UUID.fromString(campos[0].substring(1,35)),IdGenerator.getInstance().setMyid(), campos[1],campos[2],Double.parseDouble(campos[3]),  LocalDate.parse(campos[4])));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return listFunkos;
        });
    }
}
