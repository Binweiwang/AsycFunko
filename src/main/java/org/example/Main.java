package org.example;


import org.example.model.Funko;
import org.example.util.CsvCreater;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        var csvCreater = new CsvCreater();
        CompletableFuture<ArrayList<Funko>> arrayListCompletableFuture = csvCreater.csvToFunkos();
        arrayListCompletableFuture.thenAccept(funkos -> {
            int count = 0;
            for (Funko funko : funkos) {
                System.out.println(funko);
                count++;
            }
            System.out.println(count);
        });

        sleep(10000);

    }
}