package org.example.util;

import org.example.model.Funko;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CsvCreater {

    public CompletableFuture<ArrayList<Funko>> csvToFunkos() {
        return CompletableFuture.supplyAsync(() -> {
            var listFunkos = new ArrayList<Funko>();
            // No se encuentra la ruta con la class ClassLoader
            String rutaCSV ="D:\\Proyecto\\AsycFunko\\AsycFunko\\data\\funkos.csv";
            try(BufferedReader bf = new BufferedReader(new FileReader(rutaCSV))) {
                String linea;
                bf.readLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                while((linea = bf.readLine()) != null){
                    String [] campos = bf.readLine().split(",");
                    listFunkos.add(new Funko(UUID.fromString(campos[0].substring(1,35)),campos[1],campos[2],Double.parseDouble(campos[3]), formatter.format(LocalDate.parse(campos[4]))));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return listFunkos;
        });
    }
}
