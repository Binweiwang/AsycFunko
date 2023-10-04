-- Borra la tabla si existe
DROP TABLE IF EXISTS FUNKOS;

-- Crear la tabla FUNKOS
CREATE TABLE IF NOT EXISTS FUNKOS(
    id INT PRIMARY KEY AUTO_INCREMENT,
    cod UUID DEFAULT RANDOM_UUID() NOT NULL,
    myid LONG,
    nombre VARCHAR(255),
    modelo VARCHAR(20) CHECK(modelo IN ('MARVEL','DISNEY','ANIME','OTROS')),
    precio DOUBLE,
    fecha_lanzamiento DATE,
    created_at DATE DEFAULT CURRENT_TIMESTAMP(),
    updated_at DATE DEFAULT CURRENT_TIMESTAMP()
)