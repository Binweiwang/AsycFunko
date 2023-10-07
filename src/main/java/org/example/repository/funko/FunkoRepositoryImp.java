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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FunkoRepositoryImp implements FunkoRepository {
    private static FunkoRepositoryImp instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImp.class);
    private final DatabaseManager db;

    public FunkoRepositoryImp(DatabaseManager dm) {
        this.db = dm;
    }

    public static FunkoRepositoryImp getInstance(DatabaseManager db) {
        if (instance == null) {
            instance = new FunkoRepositoryImp(db);
        }
        return instance;
    }

    @Override
    public CompletableFuture<Funko> save(Funko funko) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            String query = "INSERT INTO FUNKOS (cod, myId, nombre, modelo, precio, fecha_lanzamiento) VALUES(?,?,?,?,?,?)";
            try (var connection = db.getConnection();
                 var stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            ) {
                logger.debug("Guardando Funko: " + funko);
                stmt.setObject(1, funko.getCod());
                stmt.setLong(2, funko.getMyId());
                stmt.setString(3, funko.getNombre());
                stmt.setString(4, funko.getModelo());
                stmt.setDouble(5, funko.getPrecio());
                stmt.setObject(6, funko.getFechaLanzamiento());
                var res = stmt.executeUpdate();
                if (res > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    while (rs.next()) {
                        funko.setId(rs.getLong(1));
                    }
                    rs.close();
                } else {
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
        return CompletableFuture.supplyAsync(() -> {
            String query = "UPDATE FUNKOS SET nombre = ?, modelo = ?, precio = ?, updated_at = ? WHERE id = ?";
            try (var connection = db.getConnection();
                 var stmt = connection.prepareStatement(query)) {
                logger.debug("Actualizando funko: " + funko);
                funko.setUpdated_at(LocalDateTime.now());
                stmt.setString(1, funko.getNombre());
                stmt.setString(2, funko.getModelo());
                stmt.setDouble(3, funko.getPrecio());
                stmt.setObject(4, funko.getUpdated_at());
                stmt.setLong(5, funko.getId());
                var rs = stmt.executeUpdate();
                if (rs > 0) {
                    logger.debug("Funko actualizado");
                } else {
                    logger.debug("No ha podido encontrar funko con: " + funko.getId());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return funko;
        });
    }

    @Override
    public CompletableFuture<Optional<Funko>> findById(Long id) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Funko> funko = Optional.empty();
            String query = "SELECT * FROM FUNKOS WHERE id = ?";
            try (var connection = db.getConnection();
                 var stmt = connection.prepareStatement(query)) {
                logger.debug("Buscar funko por id");
                stmt.setLong(1, id);
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    funko = Optional.of(Funko.builder()
                                    .id(rs.getLong("id"))
                                    .nombre(rs.getString("nombre"))
                                    .cod(rs.getObject("cod", UUID.class))
                                    .myId(rs.getLong("myID"))
                                    .precio(rs.getDouble("precio"))
                                    .modelo(rs.getString("modelo"))
                                    .credated_at(rs.getObject("credated_at", LocalDateTime.class))
                                    .updated_at(rs.getObject("updated_at",LocalDateTime.class))
                                    .build()
                );
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return funko;
        });
    }

    @Override
    public CompletableFuture<ArrayList<Funko>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            var lista = new ArrayList<Funko>();
            String query = "SELECT * FROM FUNKOS";
            try (var connetion = db.getConnection();
                 var stmt = connetion.prepareStatement(query)) {
                logger.debug("Obteniendo todos los funkos");
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    Funko funko = Funko.builder()
                            .id(rs.getInt("id"))
                            .cod(rs.getObject("cod", UUID.class))
                            .myId(rs.getLong("myid"))
                            .nombre(rs.getString("nombre"))
                            .modelo(rs.getString("modelo"))
                            .precio(rs.getDouble("precio"))
                            .fechaLanzamiento(((java.sql.Date) rs.getObject("fecha_lanzamiento")).toLocalDate())
                            .credated_at(rs.getTimestamp("created_at").toLocalDateTime())
                            .updated_at(rs.getTimestamp("updated_at").toLocalDateTime())
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
    public CompletableFuture<Boolean> deleteById(Long integer) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            String query = "DELETE FROM FUNKOS WHERE id = ?";
            try (var connection = db.getConnection();
                 var stmt = connection.prepareStatement(query)) {
                logger.debug("Borrando funko por id");
                stmt.setLong(1, integer);
                var rs = stmt.executeUpdate();
                stmt.close();
                return rs > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteAll() throws SQLException {
        return CompletableFuture.runAsync(()->{
            String query = "DELETE FROM FUNKO";
            try(var connection = db.getConnection();
                var stmt = connection.prepareStatement(query)) {
                logger.debug("Borrando todos los Funkos");
                stmt.executeUpdate();
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<ArrayList<Funko>> findByNombre(String nombre) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            var lista = new ArrayList<Funko>();
            String query = "SELECT * FROM FUNKOS WHERE nombre LIKE ?";
            try (var connection = db.getConnection();
                 var stmt = connection.prepareStatement(query)) {
                logger.debug("Obteniendo todos los funko por nombre que contenga: " + nombre);
                stmt.setString(1, "%" + nombre + "%");
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    Funko funko = Funko.builder()
                            .id(rs.getInt("id"))
                            .cod(rs.getObject("cod", UUID.class))
                            .myId(rs.getLong("myid"))
                            .nombre(rs.getString("nombre"))
                            .modelo(rs.getString("modelo"))
                            .precio(rs.getDouble("precio"))
                            .fechaLanzamiento(((java.sql.Date) rs.getObject("fecha_lanzamiento")).toLocalDate())
                            .credated_at(rs.getTimestamp("created_at").toLocalDateTime())
                            .updated_at(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                    lista.add(funko);
                }
            } catch (SQLException e) {
                logger.debug("Error al buscar funko por nombre");
                throw new RuntimeException(e);
            }
            return lista;
        });
    }
}
