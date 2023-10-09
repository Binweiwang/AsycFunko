package org.example.service.funko;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.exception.FunkoException;
import org.example.exception.FunkoNoAlmacenadoException;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FunkoServiceImp implements FunkoService {
    private static FunkoServiceImp instance;
    private final Logger logger= LoggerFactory.getLogger(FunkoServiceImp.class);
    private final FunkoRepository funkoRepository;
    private final FunkoCache cache;
    private static final int CACHE_SIZE = 10;

    // Constructor privado
    private FunkoServiceImp(FunkoRepository funkoRepository){
        this.funkoRepository = funkoRepository;
        this.cache = new FunkoCacheImp(CACHE_SIZE);
    }
    // metodo singleton que develve una instancia de la clase
    public static FunkoServiceImp getInstance(FunkoRepository funkoRepository){
        if (instance == null){
            instance = new FunkoServiceImp(funkoRepository);
        }
        return instance;
    }

    /**
     * Metodo que carga las propiedades de la base de datos
     * @return un array de funkos
     * @throws Exception
     */
    @Override
    public List<Funko> findAll() throws Exception {
        return funkoRepository.findAll().get();
    }

    /**
     * Metodo que busca un funko por nombre
     * @param nombre nombre del funko
     * @return un array de funkos
     * @throws SQLException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws FunkoException
     */
    @Override
    public List<Funko> findbyNombre(String nombre) throws SQLException, ExecutionException, InterruptedException, FunkoException {
        return funkoRepository.findByNombre(nombre).get();
    }

    /**
     * Metodo que busca un funko por id
     * @param id
     * @return un funko
     * @throws Exception
     */
    @Override
    public Optional<Funko> findById(long id) throws Exception {
        return funkoRepository.findById(id).get();
    }

    /**
     * Metodo que guarda un funko
     * @param funko
     * @return un funko
     * @throws SQLException
     * @throws FunkoNoAlmacenadoException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws FunkoException
     */
    @Override
    public Funko save(Funko funko) throws SQLException, FunkoNoAlmacenadoException, ExecutionException, InterruptedException, FunkoException {
        funko = funkoRepository.save(funko).get();
        cache.put(funko.getId(),funko);
        return funko;


    }

    /**
     * Metodo que actualiza un funko
     * @param funko
     * @return un funko
     * @throws SQLException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws FunkoException
     */
    @Override
    public Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException, FunkoException {
        funko =funkoRepository.update(funko).get();
        cache.put(funko.getId(),funko);
        return funko;
    }

    /**
     * Metodo que elimina un funko por id
     * @param id
     * @return un booleano
     * @throws SQLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public boolean deleteById(long id) throws SQLException, ExecutionException, InterruptedException {
        var deleted = funkoRepository.deleteById(id).get();
        if (deleted){
            cache.remove(id);
        }
        return deleted;
    }

    /**
     * Metodo que elimina todos los funkos
     * @throws SQLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public void deleteAll() throws SQLException, ExecutionException, InterruptedException {
        funkoRepository.deleteAll().get();
        cache.clear();
    }

    /**
     * Metodo que importa los funkos
     * @return un completable future
     */
    @Override
    public CompletableFuture<Void> importar() {
        return CompletableFuture.runAsync(()->{
            String rutaFileCSV = Paths.get("").toAbsolutePath().toString()+ File.separator + "data" + File.separator + "funkos.csv";
            try (BufferedReader br = new BufferedReader(new FileReader(rutaFileCSV))){
                br.readLine();
                String lines;
                while((lines = br.readLine()) != null){
                    String[] line = lines.split(",");
                    Funko funko = getFunko(line);
                    logger.debug("Importando funko: " + funko);
                    funkoRepository.save(funko);
                }
            } catch (IOException | SQLException | FunkoException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Metodo que delvelve un Funko
     * @param line los datos de Funko
     * @return un Funko
     */
    private static Funko getFunko(String[] line) {
        return Funko.builder()
                .cod(UUID.fromString(line[0].substring(1,35)))
                .myId(IdGenerator.getInstance().getMyid())
                .nombre(line[1])
                .modelo(line[2])
                .precio(Double.parseDouble(line[3]))
                .fechaLanzamiento(LocalDate.parse(line[4]))
                .build();
    }

    /**
     * Metodo que exporta los funkos
     * @param ruta ruta donde se exportan los funkos
     * @return un completable future
     */
    @Override
    public CompletableFuture<Void> exportar(String ruta) {
        return CompletableFuture.runAsync(()->{
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,new LocalDateTypeAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).create();
            try {
                ArrayList<Funko> funkos = funkoRepository.findAll().get();
                String json = gson.toJson(funkos);
                Files.writeString(Path.of(ruta),json);
            } catch (InterruptedException | ExecutionException | SQLException | IOException e) {
                logger.debug("Error de exportar archivo a JSON " + e.getMessage());
            }
        });
    }

    /**
     * Metodo que devuelve el funko mas caro
     * @return funko mas caro
     */
    @Override
    public CompletableFuture<Funko> funkoMasCaro() {
        return CompletableFuture.supplyAsync(()->
        {
            Funko funko;
            try {
                funko = funkoRepository.findAll().get().stream().max(Comparator.comparing(Funko::getPrecio)).get();

            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return funko;
        });
    }

    /**
     * Metodo que devuelve la media de los funkos
     * @return media de los funkos
     */
    @Override
    public CompletableFuture<Double> mediaFunko() {
        return CompletableFuture.supplyAsync(()->{
            double media;
            try {
                media = funkoRepository.findAll().get().stream().mapToDouble(Funko::getPrecio).average().getAsDouble();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return media;
        });
    }

    /**
     * Metodo que devuelve un map con los funkos agrupados por modelos
     * @return un map con los funkos agrupados por modelos
     */
    @Override
    public CompletableFuture<Map<String, List<Funko>>> agrupadoPorModelos() {
        return CompletableFuture.supplyAsync(()-> {
           var map = new HashMap<String, List<Funko>>();
            try {
                funkoRepository.findAll().get().forEach(funko -> {
                    try {
                        map.put(funko.getModelo(), funkoRepository.findByModelo(funko.getModelo()).join());
                    } catch (SQLException | FunkoException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return map;
        });
    }

    /**
     * Metodo que devuelve un map con el numero de funkos por modelos
     * @return un map con el numero de funkos por modelos
     */
    @Override
    public CompletableFuture<Map<String, Integer>> numeroFunkoPorModelos() {
        return CompletableFuture.supplyAsync(()->{
            var map = new HashMap<String, Integer>();
            try {
                for (Funko funko : funkoRepository.findAll().get()) {
                    try {
                        map.put(funko.getModelo(), (int) funkoRepository.findByModelo(funko.getModelo()).join().stream().count());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (FunkoException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (InterruptedException | ExecutionException | SQLException e) {
                throw new RuntimeException(e);
            }
            return map;
        });
    }

    /**
     * Metodo que devuelve los funkos lanzados en 2023
     * @return un array de funkos
     */
    @Override
    public CompletableFuture<List<Funko>> funkoLanzados2023() {
        return CompletableFuture.supplyAsync(()->{
            var funkos2023 = new ArrayList<Funko>();
            try {
                funkoRepository.findAll().get().stream().filter(funko -> funko.getFechaLanzamiento().getYear() == 2023).forEach(funkos2023::add);
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return funkos2023;
        });
    }

    /**
     * Metodo que devuelve el numero de funkos de stitch
     * @return un numero de funkos
     */
    @Override
    public CompletableFuture<Integer> numeroFunkosStitch() {
        return CompletableFuture.supplyAsync(()->{
            int numeroFunkosStitch = 0;
            try {
                numeroFunkosStitch = (int) funkoRepository.findAll().get().stream().filter(funko -> funko.getNombre().contains("Stitch")).count();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return numeroFunkosStitch;
        });
    }

    /**
     * Metodo que devuelve los funkos de stitch
     * @return un array de funkos
     */
    @Override
    public CompletableFuture<List<Funko>> funkosStitch() {
        return CompletableFuture.supplyAsync(()->{
            var funkosStitch= new ArrayList<Funko>();
            try {
                funkoRepository.findAll().get().stream().filter(funko -> funko.getNombre().contains("Stitch")).forEach(funkosStitch::add);
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return funkosStitch;
        });
    }

}
