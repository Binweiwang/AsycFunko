package org.example.repository.funko;

import org.example.model.Funko;
import org.example.service.DatabaseManager;
import org.example.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FunkoRepositoryImp implements FunkoRepository {
    private static FunkoRepositoryImp instance;
    private final Logger logger= LoggerFactory.getLogger(FunkoRepositoryImp.class);
    private final DatabaseManager db;

    public FunkoRepositoryImp(DatabaseManager dm) {
        this.db = dm;
    }
    public static FunkoRepositoryImp getInstance(DatabaseManager db){
        if(instance == null){
            instance = new FunkoRepositoryImp(db);
        }
        return instance;
    }

    @Override
    public CompletableFuture<Funko> save(Funko funko) throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            String query = "INSERT INTO FUNKOS (cod, myId, nombre, modelo, precio, fecha_lanzamiento) VALUES(?,?,?,?,?,?)";
            try(var connection = db.getConnection();
                var stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            ){
                logger.debug("Guardando Funko: " + funko);
                stmt.setObject(1, funko.getCod());
                stmt.setLong(2, funko.getMyId());
                stmt.setString(3,funko.getNombre());
                stmt.setString(4,funko.getModelo());
                stmt.setDouble(5,funko.getPrecio());
                stmt.setObject(6,funko.getFechaLanzamiento());
                var res = stmt.executeUpdate();
                if(res > 0){
                    ResultSet rs = stmt.getGeneratedKeys();
                    while(rs.next()){
                        funko.setId(rs.getLong(1));
                    }
                    rs.close();
                }else {
                    logger.debug("Funko no guardado");
                }
            } catch (SQLException e) {
                logger.debug("Error al guardar el funko " + e.getMessage());
            }
            return funko;
        });
    }

    @Override
    public CompletableFuture<Funko> update(Funko funko) throws SQLException {

        return null;
    }

    @Override
    public CompletableFuture<Funko> findById(Integer integer) throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<Funko>> findAll(){
        return CompletableFuture.supplyAsync(()->{
            var lista = new ArrayList<Funko>();
            String query = "SELECT * FROM FUNKOS";
            try(var connetion = db.getConnection();
                var stmt = connetion.prepareStatement(query)){
                logger.debug("Obteniendo todos los funkos");
                var rs = stmt.executeQuery();
                while(rs.next()){
                    Funko funko = Funko.builder()
                            .id(rs.getLong("id"))
                            .cod(rs.getObject("cod", UUID.class))
                            .myId(rs.getLong("myid"))
                            .nombre(rs.getString("nombre"))
                            .modelo(rs.getString("modelo"))
                            .precio(rs.getDouble("precio"))
                            .fechaLanzamiento((LocalDate) rs.getObject("fecha_lanzamiento"))
                            .credated_at((LocalDateTime) rs.getObject("created_at"))
                            .upcrated_at((LocalDateTime) rs.getObject("updated_at"))
                            .build();
                    lista.add(funko);
                }
            } catch (SQLException e) {
                logger.error("Error al buscar todos los alumnos", e);
                throw new RuntimeException(e);
            }
            return lista;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(Integer integer) throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteAll() throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<Funko>> findByNombre(String nombre) throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<Funko>> csvToFunko() throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            var listFunkos = new ArrayList<Funko>();
            String rutaCSV = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "funkos.csv";
            try(BufferedReader bf = new BufferedReader(new FileReader(rutaCSV))) {
                String linea;
                bf.readLine();
                while((linea = bf.readLine()) != null){
                    String [] campos = bf.readLine().split(",");
                    listFunkos.add(new Funko(UUID.fromString(campos[0].substring(1,35)), IdGenerator.getInstance().getMyid(), campos[1],campos[2],Double.parseDouble(campos[3]),  LocalDate.parse(campos[4])));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return listFunkos;
        });
    }
}
