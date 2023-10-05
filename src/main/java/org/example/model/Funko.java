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
    private long id;
    private UUID cod;
    private long myId;
    private String nombre;
    private String modelo;
    private double precio;
    private LocalDate fechaLanzamiento;
    private LocalDateTime credated_at;
    private LocalDateTime upcrated_at;

    public Funko(UUID cod, long myId, String nombre, String modelo, double precio, LocalDate fechaLanzamiento) {
        this.cod = cod;
        this.myId = myId;
        this.nombre = nombre;
        this.modelo = modelo;
        this.precio = precio;
        this.fechaLanzamiento = fechaLanzamiento;
        this.credated_at = LocalDateTime.now();
        this.upcrated_at = LocalDateTime.now();
    }

    public Funko(long id, UUID cod, long myId, String nombre, String modelo, double precio, LocalDate fechaLanzamiento, LocalDateTime credated_at, LocalDateTime upcrated_at) {
        this.id = id;
        this.cod = cod;
        this.myId = myId;
        this.nombre = nombre;
        this.modelo = modelo;
        this.precio = precio;
        this.fechaLanzamiento = fechaLanzamiento;
        this.credated_at = credated_at;
        this.upcrated_at = upcrated_at;
    }
}
