package org.example.service;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager implements AutoCloseable{
    private static DatabaseManager instance = null;
    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final HikariDataSource dataSource;
    private String databaseUrl;
    private boolean initTables;
    private String initScript;

    /**
     * Constructor privado
     */
    private DatabaseManager(){
        logger.debug("Iniciando la configuracion basica de base de datos");
        loadPropierties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        dataSource = new HikariDataSource(config);
        try(Connection conn = dataSource.getConnection()){
            if (initTables){
                initDatabas(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo que inicializa la base de datos
     * @param conn conexion a la base de datos
     */
    private void initDatabas(Connection conn) {
        logger.debug("Cargando scriptt de la base de datos");
        try{
            excuteScript(conn,initScript,initTables);
        } catch (Exception e) {
           logger.debug("No ha podido abrir el fichero de script " + e.getMessage());
        }
    }

    /**
     * Metodo que ejecuta el script de la base de datos
     * @param conn conexion a la base de datos
     * @param initScript script de la base de datos
     * @param initTables booleano que indica si se inicializa la base de datos
     * @throws FileNotFoundException excepcion de fichero no encontrado
     */
    private void excuteScript(Connection conn, String initScript, boolean initTables) throws FileNotFoundException {
        var sr = new ScriptRunner(conn);
        var scirptPath = ClassLoader.getSystemResource(initScript).getFile();
        logger.debug("Ejecutando script de SQL " + scirptPath);
        var bf = new BufferedReader(new FileReader(scirptPath));
        sr.setLogWriter(initTables ? new PrintWriter(System.out) : null);
        sr.runScript(bf);
    }


    /**
     * Metodo que carga las propiedades de la base de datos
     */
    private synchronized void loadPropierties() {
        logger.debug("Cargando fichero de configuracion de la base de datos");
        try{
            var pathFile = Paths.get("").toAbsolutePath().toString() + File.separator + "src" + File.separator  + File.separator + "main" + File.separator + "resources" + File.separator + "database.properties";
            var prop = new Properties();
            prop.load(new FileReader(pathFile));
            databaseUrl = prop.getProperty("database.url", "jdbc:h2:mem:FUNKKOS");
            initTables = Boolean.parseBoolean(prop.getProperty("database.initTables","false"));
            initScript = prop.getProperty("database.initScript","init.sql");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo singleton que devuelve la instancia de la base de datos
     * @return
     */
    public static synchronized  DatabaseManager getInstance(){
        if(instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Metodo que devuelve la conexion a la base de datos
     * @return conexion a la base de datos
     * @throws SQLException excepcion de SQL
     */
    public synchronized Connection getConnection() throws SQLException {
      return dataSource.getConnection();
    }

    /**
     * Metodo que cierra la conexion a la base de datos
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        dataSource.close();
    }
}
