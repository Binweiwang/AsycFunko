package org.example.service;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
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

    private void initDatabas(Connection conn) {
        logger.debug("Cargando scriptt de la base de datos");
        try{
            excuteScript(conn,initScript,initTables);
        } catch (Exception e) {
           logger.debug("No ha podido abrir el fichero de script " + e.getMessage());
        }
    }

    private void excuteScript(Connection conn, String initScript, boolean initTables) throws FileNotFoundException {
        var sr = new ScriptRunner(conn);
        var scirptPath = ClassLoader.getSystemResource(initScript).getFile();
        logger.debug("Ejecutando script de SQL " + scirptPath);
        var bf = new BufferedReader(new FileReader(scirptPath));
        sr.setLogWriter(initTables ? new PrintWriter(System.out) : null);
        sr.runScript(bf);
    }



    private synchronized void loadPropierties() {
        logger.debug("Cargando fichero de configuracion de la base de datos");
        try{
            var file = ClassLoader.getSystemResource("database.properties").getFile();
            var prop = new Properties();
            prop.load(new FileReader(file));
            databaseUrl = prop.getProperty("database.url", "jdbc:h2:mem:FUNKKOS");
            initTables = Boolean.parseBoolean(prop.getProperty("database.initTables","false"));
            initScript = prop.getProperty("database.initScript","init.sql");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized  DatabaseManager getInstance(){
        if(instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
      return dataSource.getConnection();
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }
}
