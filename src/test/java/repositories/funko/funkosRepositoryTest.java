package repositories.funko;

import org.example.exception.FunkoException;
import org.example.model.Funko;
import org.example.repository.funko.FunkoRepository;
import org.example.repository.funko.FunkoRepositoryImp;
import org.example.service.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class funkosRepositoryTest {
    private FunkoRepository funkoRepository;

    // Resetar los datos
    @BeforeEach
    void setUp() throws SQLException {
        funkoRepository = FunkoRepositoryImp.getInstance(DatabaseManager.getInstance());
        funkoRepository.deleteAll();
    }

    @AfterEach
    void tearDown() throws SQLException {
        funkoRepository.deleteAll();
    }

    @Test
    void saveFunko() throws FunkoException, SQLException, ExecutionException, InterruptedException {
            Funko funko = getFunko(1L, 1L);
        Funko savedFunko = funkoRepository.save(funko).get();
        assertAll(() -> assertNotNull(savedFunko),
                () -> assertNotNull(savedFunko.getId()),
                () -> assertEquals(funko.getNombre(), savedFunko.getNombre()),
                () -> assertEquals(funko.getModelo(), savedFunko.getModelo()),
                () -> assertEquals(funko.getPrecio(), savedFunko.getPrecio()),
                () -> assertNotNull(savedFunko.getCod()),
                () -> assertNotNull(savedFunko.getMyId()),
                () -> assertNotNull(savedFunko.getCredated_at()),
                () -> assertNotNull(savedFunko.getUpdated_at())
        );
    }

    @Test
    void deleteFunko() throws SQLException, FunkoException, ExecutionException, InterruptedException {
        Funko funko = getFunko(1L, 1L);
        Funko savedFunko = funkoRepository.save(funko).get();
        funkoRepository.deleteById(savedFunko.getId()).get();
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.getId()).get();
        assertFalse(foundFunko.isPresent());
    }

    @Test
    void deleteFunkoNoExiste() throws SQLException, ExecutionException, InterruptedException {
        boolean deleted = funkoRepository.deleteById(1L).get();
        assertFalse(deleted);
    }

    @Test
    void deleteAllFunkos() throws FunkoException, SQLException, ExecutionException, InterruptedException {
        Funko funko1 = getFunko(1L, 1);
        Funko funko2 = getFunko(2L, 2);
        Funko funko3 = getFunko(2L, 2);
        funkoRepository.save(funko1).get();
        funkoRepository.save(funko2).get();
        funkoRepository.save(funko3).get();
        funkoRepository.deleteAll();
        List<Funko> foundFunkos = funkoRepository.findAll().get();
        assertEquals(0, foundFunkos.size());
    }
    @Test
    void findById() throws FunkoException, SQLException, ExecutionException, InterruptedException {
        Funko funko = getFunko(5L,1L);
        Funko savedFunko = funkoRepository.save(funko).get();
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.getId()).get();
        assertAll(() -> assertTrue(foundFunko.isPresent())

        );
    }
    @Test
    void findByIdNoExiste() throws SQLException, ExecutionException, InterruptedException {
        Optional<Funko> foundFunko = funkoRepository.findById(1L).get();
        assertAll(() -> assertFalse(foundFunko.isPresent()));
    }

    @Test
    void findByNombre() throws FunkoException, SQLException, ExecutionException, InterruptedException {
        Funko funko1 = getFunko(1L,1L,"test-1");
        Funko funko2 = getFunko(2L,2L,"test-2");

        funkoRepository.save(funko1).get();
        funkoRepository.save(funko2).get();

        List<Funko> foundFunkos = funkoRepository.findByNombre("test").get();
        System.out.println(foundFunkos);
        assertAll(() -> assertNotNull(foundFunkos),
                () -> assertEquals(2, foundFunkos.size()),
                () -> assertEquals(foundFunkos.get(0).getNombre(), funko1.getNombre()),
                () -> assertEquals(foundFunkos.get(0).getPrecio(), funko1.getPrecio()),
                () -> assertEquals(foundFunkos.get(1).getNombre(), funko2.getNombre()),
                () -> assertEquals(foundFunkos.get(1).getPrecio(), funko2.getPrecio())
        );
    }
    private static Funko getFunko(long id, long myId) {
        Funko funko = Funko.builder()
                .cod(UUID.randomUUID())
                .myId(myId)
                .nombre("Test")
                .id(id)
                .modelo("DISNEY")
                .precio(9.5)
                .fechaLanzamiento(LocalDate.now())
                .credated_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        return funko;
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

}