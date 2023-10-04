package org.example.model;

import java.time.LocalDate;
import java.util.UUID;

public record Funko(UUID id, String nombre, String modelo, double precio, String fechaLanzamiento
) {
}
