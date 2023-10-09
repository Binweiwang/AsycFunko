package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
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

    // Constructor
    public Funko(UUID cod, long myId, String nombre, String modelo, double precio, LocalDate fechaLanzamiento) {
        this.cod = cod;
        this.myId = myId;
        this.nombre = nombre;
        this.modelo = modelo;
        this.precio = precio;
        this.fechaLanzamiento = fechaLanzamiento;
        this.credated_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }
    // Constructor
    public Funko(long id, UUID cod, long myId, String nombre, String modelo, double precio, LocalDate fechaLanzamiento, LocalDateTime credated_at, LocalDateTime updated_at) {
        this.id = id;
        this.cod = cod;
        this.myId = myId;
        this.nombre = nombre;
        this.modelo = modelo;
        this.precio = precio;
        this.fechaLanzamiento = fechaLanzamiento;
        this.credated_at = credated_at;
        this.updated_at = updated_at;
    }
}
