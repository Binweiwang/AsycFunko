package org.example.service;


import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager implements AutoCloseable{
    private static DatabaseManager instance = null;
    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;
    private boolean initTables;
    private String initScript;
    private Connection connection;


    private DatabaseManager(){
        logger.debug("Iniciando la configuracion basica de base de datos");
        loadPropierties();
        try{
            openConnection();
            if (initTables){
                initDatabas();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDatabas() {
        logger.debug("Cargando scriptt de la base de datos");
        try{
            excuteScript(initScript,initTables);
        } catch (Exception e) {
           logger.debug("No ha podido abrir el fichero de script " + e.getMessage());
        }
    }

    private void excuteScript(String initScript, boolean initTables) throws FileNotFoundException {
        var sr = new ScriptRunner(connection);
        var scirptPath = ClassLoader.getSystemResource(initScript).getFile();
        logger.debug("Ejecutando script de SQL " + initScript);
        var bf = new BufferedReader(new FileReader(scirptPath));
        sr.setLogWriter(initTables ? new PrintWriter(System.out) : null);
        sr.runScript(bf);
    }

    private void openConnection() throws SQLException {
        logger.debug("Abriendo conexion con la base de datos");
        try {
            connection = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
           logger.debug("No ha podido abrir la conexcion " + e.getMessage());
        }
    }

    private void loadPropierties() {
        logger.debug("Cargando fichero de configuracion de la base de datos");
        try{
            var file = ClassLoader.getSystemResource("database.properties").getFile();
            var prop = new Properties();
            prop.load(new FileReader(file));
            databaseUrl = prop.getProperty("database.url", "jdbc:h2:mem:FUNKKOS");
            databaseUser = prop.getProperty("database.user","sa");
            databasePassword = prop.getProperty("database.password", "");
            initTables = Boolean.parseBoolean(prop.getProperty("database.initTables","false"));
            initScript = prop.getProperty("database.initScript","init.sql");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DatabaseManager getInstance(){
        if(instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()){
            try{
                openConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    private void closeConnection(){
        logger.debug("Cerrando conexcion con la base de datos");
        try{
            connection.close();
        } catch (SQLException e) {
          logger.debug("No ha podido cerrar la conexcion " + e.getMessage());
        }
    }
    @Override
    public void close() throws Exception {
       closeConnection();
    }
}
