-- Borra la tabla si existe
-- DROP TABLE IF EXISTS FUNKOS;

-- Crear la tabla FUNKOS
CREATE TABLE IF NOT EXISTS FUNKOS(
    id INT PRIMARY KEY AUTO_INCREMENT,
    cod UUID DEFAULT RANDOM_UUID() NOT NULL,
    myid LONG NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    modelo VARCHAR(20) CHECK(modelo IN ('MARVEL','DISNEY','ANIME','OTROS')),
    precio DOUBLE NOT NULL,
    fecha_lanzamiento DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL
);
