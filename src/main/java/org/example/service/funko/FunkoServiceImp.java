package org.example.service.funko;
import org.example.model.Funko;
import org.example.repository.funko.FunkoRepository;
import org.example.repository.funko.FunkoRepositoryImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FunkoServiceImp implements FunkoService {
    private static FunkoServiceImp instance;
    private final Logger logger= LoggerFactory.getLogger(FunkoServiceImp.class);
    private FunkoRepository funkoRepository;

    private FunkoServiceImp(FunkoRepository funkoRepository){
        this.funkoRepository = funkoRepository;
    }
    public static FunkoServiceImp getInstance(FunkoRepository funkoRepository){
        if (instance == null){
            instance = new FunkoServiceImp(funkoRepository);
        }
        return instance;
    }

    @Override
    public List<Funko> findAll() throws SQLException, ExecutionException, InterruptedException {
        return funkoRepository.findAll().get() ;
    }

    @Override
    public List<Funko> findbyNombre(String nombre) throws SQLException, ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public Optional<Funko> findById(long id) throws SQLException, ExecutionException, InterruptedException {
        return Optional.empty();
    }

    @Override
    public Funko save(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Guardando funko");
        return funkoRepository.save(funko).get();

    }

    @Override
    public Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public boolean deleteById(long id) throws SQLException, ExecutionException, InterruptedException {
        return false;
    }

    @Override
    public void deleteAll() throws SQLException, ExecutionException, InterruptedException {

    }

    @Override
    public List<Funko> csvToFunko() throws SQLException, ExecutionException, InterruptedException {
        return funkoRepository.csvToFunko().get();
    }
}
