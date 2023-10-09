# Proyecto Java AsycFunko

***
Es un proyecto simple en Java que utiliza H2 como base de datos. A contnuación, se describen los pasos para configura y
ejecutar el proyecto.

## Requisitos

***

- Java 8 o superior
- Gradle

# Configuración

***

1. **Configuración de las dependencias**

Agraga las siguientes dependencias al archivo `build.gradle`:

```kotlin
plugins {
    java
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Añadimos las dependencias de las librerías JDBC que vayamos a usar
    // SQLite
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
    // H2, solo usa una
    implementation("com.h2database:h2:2.2.224")
    // Ibatis lo usaremos para leer los scripts SQL desde archivos
    implementation("org.mybatis:mybatis:3.5.13")
    // Lombook para generar código
    implementation("org.projectlombok:lombok:1.18.26")
}
```

2. **Configuración de la base de datos**

Este proyecto ha utilizado "H2" como base de datos, podemos crear un archivo en la carpeta "resources" llamado '
database.properties' para las configuraciones de la base de datos.

```properties
database.url=jdbc:h1:./funkos
database.driver=org.h1.Driver
database.user=sa
database.password=
database.initTables=true
database.initScript=init.sql
```

3. **Crear la clase DataBaseManager**

La clase `DatabaseManger` nos permite manejar la conexión a la base de datos y la inicializaición de las tablas, es una
clase `singleton` y tiene un metodo `getInstance()` que devuelve una instancia de la base de datos.

```java
    private DatabaseManager(){
        logger.debug("Iniciando la configuracion basica de base de datos");
        loadPropierties();

        HikariConfig config=new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        dataSource=new HikariDataSource(config);
        try(Connection conn=dataSource.getConnection()){
        if(initTables){
        initDatabas(conn);
        }
        }catch(SQLException e){
        throw new RuntimeException(e);
        }
        }

/**
 * Metodo que inicializa la base de datos
 * @param conn conexion a la base de datos
 */
private void initDatabas(Connection conn){
        logger.debug("Cargando scriptt de la base de datos");
        try{
        excuteScript(conn,initScript,initTables);
        }catch(Exception e){
        logger.debug("No ha podido abrir el fichero de script "+e.getMessage());
        }
        }

/**
 * Metodo que ejecuta el script de la base de datos
 * @param conn conexion a la base de datos
 * @param initScript script de la base de datos
 * @param initTables booleano que indica si se inicializa la base de datos
 * @throws FileNotFoundException excepcion de fichero no encontrado
 */
private void excuteScript(Connection conn,String initScript,boolean initTables)throws FileNotFoundException{
        var sr=new ScriptRunner(conn);
        var scirptPath=ClassLoader.getSystemResource(initScript).getFile();
        logger.debug("Ejecutando script de SQL "+scirptPath);
        var bf=new BufferedReader(new FileReader(scirptPath));
        sr.setLogWriter(initTables?new PrintWriter(System.out):null);
        sr.runScript(bf);
        }


/**
 * Metodo que carga las propiedades de la base de datos
 */
private synchronized void loadPropierties(){
        logger.debug("Cargando fichero de configuracion de la base de datos");
        try{
        var file=ClassLoader.getSystemResource("database.properties").getFile();
        var prop=new Properties();
        prop.load(new FileReader(file));
        databaseUrl=prop.getProperty("database.url","jdbc:h2:mem:FUNKKOS");
        initTables=Boolean.parseBoolean(prop.getProperty("database.initTables","false"));
        initScript=prop.getProperty("database.initScript","init.sql");
        }catch(Exception e){
        throw new RuntimeException(e);
        }
        }

/**
 * Metodo singleton que devuelve la instancia de la base de datos
 * @return
 */
public static synchronized  DatabaseManager getInstance(){
        if(instance==null){
        instance=new DatabaseManager();
        }
        return instance;
        }

/**
 * Metodo que devuelve la conexion a la base de datos
 * @return conexion a la base de datos
 * @throws SQLException excepcion de SQL
 */
public synchronized Connection getConnection()throws SQLException{
        return dataSource.getConnection();
        }

/**
 * Metodo que cierra la conexion a la base de datos
 * @throws Exception
 */
@Override
public void close()throws Exception{
        dataSource.close();
        }
```

4. **Crear la clase Funko**

Crea una clase `Funko` con los siguientes atributos:

```java
   public class Funko {
    // Atributos
    private long id;
    private UUID cod;
    private long myId;
    private String nombre;
    private String modelo;
    private double precio;
    private LocalDate fechaLanzamiento;
    private final LocalDateTime credated_at;
    private LocalDateTime updated_at;

    // Constructores
    // Getters y Setters 
}
```

5. **Crear la clase Repository**

creamos la clase `FunkoRepository` que implementa la interfaz `CrudRepository` y sobreescribe los metodos de la interfaz.
```java
   public interface CrudRepository<T,ID,EX extends Exception> {
    //CRUD
    CompletableFuture<Funko> save(T t) throws SQLException,EX;
    CompletableFuture<Funko> update(T t) throws SQLException ,EX;
    CompletableFuture<Optional<Funko>> findById(Long id) throws SQLException;
    CompletableFuture<ArrayList<Funko>> findAll() throws SQLException;
    CompletableFuture<Boolean> deleteById(Long id) throws SQLException;
    CompletableFuture<Void> deleteAll() throws SQLException;}
```
```java
    public interface FunkoRepository extends CrudRepository<Funko, Long, FunkoException> {
    // Metodos propios de la entidad
    CompletableFuture<ArrayList<Funko>> findByNombre(String nombre) throws SQLException,FunkoException;
     CompletableFuture<List<Funko>> findByModelo(String modelo)throws SQLException,FunkoException;}
```
```java
public class FunkoRepositoryImp implements FunkoRepository {
    private static FunkoRepositoryImp instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImp.class);
    private final DatabaseManager db;
    // Constructor privado
    public FunkoRepositoryImp(DatabaseManager dm) {
        this.db = dm;
    }

    // metodo Singleton que devuelve una instancia de la clase
    public static FunkoRepositoryImp getInstance(DatabaseManager db) {
        if (instance == null) {
            instance = new FunkoRepositoryImp(db);
        }
        return instance;
    }
    // Sobreesecribimos los metodos de la interfaz...
```
6. **Crear la clase Service**

Creamos la clase `FunkoService` y sobreescribe los metodos de la interfaz.
```java
public interface FunkoService {
    //CRUD
    List<Funko> findAll() throws Exception;
    List<Funko> findbyNombre(String nombre) throws SQLException, ExecutionException, InterruptedException, FunkoException;
    Optional<Funko> findById(long id) throws Exception;

    Funko save(Funko funko) throws SQLException, FunkoNoAlmacenadoException, ExecutionException, InterruptedException, FunkoException;

    Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException, FunkoException;

    boolean deleteById(long id) throws SQLException, ExecutionException, InterruptedException;

    void deleteAll() throws SQLException, ExecutionException, InterruptedException;
    CompletableFuture<Void> importar();
    CompletableFuture<Void> exportar(String ruta);
    CompletableFuture<Funko> funkoMasCaro();
    CompletableFuture<Double> mediaFunko();
    CompletableFuture<Map<String, List<Funko>>> agrupadoPorModelos();
    CompletableFuture<Map<String,Integer>> numeroFunkoPorModelos();
    CompletableFuture<List<Funko>> funkoLanzados2023();
    CompletableFuture<Integer> numeroFunkosStitch();
    CompletableFuture<List<Funko>> funkosStitch();
}

```
```java
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
    // Sobreesecribimos los metodos de la interfaz...
```
7. **Ejecución**

Ejecutamos el proyecto y comprobamos que funciona correctamente.
```java
package org.example;


import org.example.model.Funko;
import org.example.repository.funko.FunkoRepositoryImp;
import org.example.service.DatabaseManager;
import org.example.service.funko.FunkoService;
import org.example.service.funko.FunkoServiceImp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException, ExecutionException, IOException {

        FunkoService funkoService = FunkoServiceImp.getInstance(FunkoRepositoryImp.getInstance(DatabaseManager.getInstance()));
        System.out.println("Importando datos..." + funkoService.importar().get());
        System.out.println("Exportando datos..." + funkoService.exportar("funkos.json").get());
        System.out.println("Funko mas caro: " + funkoService.funkoMasCaro().get());
        System.out.println("Media de precio de los funkos: " + funkoService.mediaFunko().get());
        System.out.println("Funkos agrupados por modelos: " + funkoService.agrupadoPorModelos().get());
        System.out.println("Numero de funkos por modelo: " + funkoService.numeroFunkoPorModelos().get());
        System.out.println("Funkos lanzados en 2023: " + funkoService.funkoLanzados2023().get());
        System.out.println("Numero de funkos de Stitch: " + funkoService.numeroFunkosStitch().get());
    }
    }
```