package org.example.service.funko;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.Funko;
import org.example.repository.funko.FunkoRepository;
import org.example.service.cacheService.FunkoCache;
import org.example.service.cacheService.FunkoCacheImp;
import org.example.util.IdGenerator;
import org.example.util.LocalDateTimeTypeAdapter;
import org.example.util.LocalDateTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FunkoServiceImp implements FunkoService {
    private static FunkoServiceImp instance;
    private final Logger logger= LoggerFactory.getLogger(FunkoServiceImp.class);
    private FunkoRepository funkoRepository;
    private FunkoCache cache;
    private static final int CACHE_SIZE = 10;

    private FunkoServiceImp(FunkoRepository funkoRepository){
        this.funkoRepository = funkoRepository;
        this.cache = new FunkoCacheImp(CACHE_SIZE);
    }
    public static FunkoServiceImp getInstance(FunkoRepository funkoRepository){
        if (instance == null){
            instance = new FunkoServiceImp(funkoRepository);
        }
        return instance;
    }

    @Override
    public List<Funko> findAll() throws SQLException, ExecutionException, InterruptedException {
        return funkoRepository.findAll().get();
    }

    @Override
    public List<Funko> findbyNombre(String nombre) throws SQLException, ExecutionException, InterruptedException {
        return funkoRepository.findByNombre(nombre).get();
    }

    @Override
    public Optional<Funko> findById(long id) throws SQLException, ExecutionException, InterruptedException {
        return funkoRepository.findById(id).get();
    }

    @Override
    public Funko save(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        funko = funkoRepository.save(funko).get();
        cache.put(funko.getId(),funko);
        return funko;


    }

    @Override
    public Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        funko =funkoRepository.update(funko).get();
        cache.put(funko.getId(),funko);
        return funko;
    }

    @Override
    public boolean deleteById(long id) throws SQLException, ExecutionException, InterruptedException {
        var deleted = funkoRepository.deleteById(id).get();
        if (deleted){
            cache.remove(id);
        }
        return deleted;
    }

    @Override
    public void deleteAll() throws SQLException, ExecutionException, InterruptedException {
        funkoRepository.deleteAll().get();
        cache.clear();
    }


    @Override
    public CompletableFuture<Void> importar() {
        return CompletableFuture.supplyAsync(()->{
            String rutaFileCSV = Paths.get("").toAbsolutePath().toString()+ File.separator + "data" + File.separator + "funkos.csv";
            try (BufferedReader br = new BufferedReader(new FileReader(rutaFileCSV))){
                br.readLine();
                String lines;
                while((lines = br.readLine()) != null){
                    String[] line = lines.split(",");
                    Funko funko = Funko.builder()
                            .cod(UUID.fromString(line[0].substring(1,35)))
                            .myId(IdGenerator.getInstance().getMyid())
                            .nombre(line[1])
                            .modelo(line[2])
                            .precio(Double.parseDouble(line[3]))
                            .fechaLanzamiento(LocalDate.parse(line[4]))
                            .build();
                    logger.debug("Importando funko: " + funko);
                    funkoRepository.save(funko);
                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    @Override
    public void exportar(String ruta) {
//        return CompletableFuture.runAsync(()->{
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,new LocalDateTypeAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).create();
            try {
                ArrayList<Funko> funkos = funkoRepository.findAll().get();
                String json = gson.toJson(funkos);
                Files.writeString(Path.of(ruta),json);
            } catch (InterruptedException | ExecutionException | SQLException | IOException e) {
                throw new RuntimeException(e);
            }
//        });
    }

}
