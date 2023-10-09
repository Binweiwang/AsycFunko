package services.funko;

import org.example.exception.FunkoException;
import org.example.model.Funko;
import org.example.repository.funko.FunkoRepository;
import org.example.service.funko.FunkoServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class funkosServicesTest {
    @Mock
    FunkoRepository funkoRepository;

     @InjectMocks
    FunkoServiceImp funkoServiceImp;

@Test
void kunkosStitch() throws SQLException {
    var funkos = of(
            getFunko(1L,1L, "test-1"),
            getFunko(2L,2L, "test-2"),
            getFunko(3L,3L, "test-3")
    );
    when(funkoRepository.findAll()).thenReturn(CompletableFuture.completedFuture(new ArrayList<>(funkos)));
    var result = funkoServiceImp.funkosStitch().join();
    assertEquals(result.size(), 0);
}
@Test
void numeroFunkoStitch() throws SQLException, FunkoException, ExecutionException, InterruptedException {
    ArrayList<Funko> funkos = new ArrayList<>();
    funkos.add(getFunko(1L,1L, "test-1"));
    funkos.add(getFunko(2L,2L, "test-2"));
    funkos.add(getFunko(3L,3L, "test-3"));
    when(funkoRepository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));
    var result = funkoServiceImp.numeroFunkosStitch().get();
    assertEquals(result,0 );
}
@Test
void funkoLanzados2023() throws SQLException, ExecutionException, InterruptedException {
ArrayList<Funko> funkos = new ArrayList<>();
    funkos.add(getFunko(1L,1L, "test-1"));
    funkos.add(getFunko(2L,2L, "test-2"));
    funkos.add(getFunko(3L,3L, "test-3"));
    when(funkoRepository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));
    var result = funkoServiceImp.funkoLanzados2023().get();
    assertEquals(result.size(), 3);

}
@Test
void numeroFunkoPorModelos() throws SQLException, ExecutionException, InterruptedException, FunkoException {
    ArrayList<Funko> funkos = new ArrayList<>();
    funkos.add(getFunko(1L,1L, "test-1"));
    funkos.add(getFunko(2L,2L, "test-2"));
    funkos.add(getFunko(3L,3L, "test-3"));
    when(funkoRepository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));
    when(funkoRepository.findByModelo("DISNEY")).thenReturn(CompletableFuture.completedFuture(funkos));
    var result = funkoServiceImp.numeroFunkoPorModelos().get();
    assertEquals(result.size(), 1);
    assertEquals(result.get("DISNEY"), 3);
}
@Test
void agrupadoPorModelos() throws SQLException, FunkoException, ExecutionException, InterruptedException {
    ArrayList<Funko> funkos = new ArrayList<>();
    funkos.add(getFunko(1L,1L, "test-1"));
    funkos.add(getFunko(2L,2L, "test-2"));
    funkos.add(getFunko(3L,3L, "test-3"));
    when(funkoRepository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));
    when(funkoRepository.findByModelo("DISNEY")).thenReturn(CompletableFuture.completedFuture(funkos)
    );
    var result = funkoServiceImp.agrupadoPorModelos().get();
    assertEquals(result.size(), 1);
    assertEquals(result.get("DISNEY").size(), 3);


}


@Test
void mediaFunko() throws SQLException, ExecutionException, InterruptedException {
    ArrayList<Funko> funkos = new ArrayList<>();
    funkos.add(getFunko(1L,1L, "test-1", 9.5));
    funkos.add(getFunko(2L,2L, "test-2", 8.5));
    funkos.add(getFunko(3L,3L, "test-3", 10.5));
    when(funkoRepository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

    double media = funkoServiceImp.mediaFunko().get();
    assertEquals(media, 9.5);

}
@Test
void funkoMasCaro() throws ExecutionException, InterruptedException, FunkoException, SQLException {
    ArrayList<Funko> funkos = new ArrayList<>();
    funkos.add(getFunko(1L,1L, "test-1", 9.5));
    funkos.add(getFunko(2L,2L, "test-2", 8.5));
    funkos.add(getFunko(3L,3L, "test-3", 10.5));

    when(funkoRepository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));
    var result = funkoServiceImp.funkoMasCaro().get();
    assertEquals(result.getNombre(), "test-3");
    assertEquals(result.getPrecio(), 10.5);
}

    @Test
    void save() throws FunkoException, SQLException, ExecutionException, InterruptedException {
        var funko = getFunko(1L,1L, "test-1");
        when(funkoRepository.save(funko)).thenReturn(CompletableFuture.completedFuture(funko));

        var result = funkoServiceImp.save(funko);

        assertAll("Guardar Funko",
                ()-> assertEquals(result.getNombre(),"test-1"),
                ()->assertEquals(result.getPrecio(),9.5)
        );
    }
    private static Funko getFunko(long id, long myId, String nombre) {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .myId(myId)
                .nombre(nombre)
                .id(id)
                .modelo("DISNEY")
                .precio(9.5)
                .fechaLanzamiento(LocalDate.now())
                .credated_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        return funko;
    }
    private static Funko getFunko(long id, long myId,String nombre, double precio) {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .myId(myId)
                .nombre(nombre)
                .id(id)
                .modelo("DISNEY")
                .precio(precio)
                .fechaLanzamiento(LocalDate.now())
                .credated_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        return funko;
    }
}
