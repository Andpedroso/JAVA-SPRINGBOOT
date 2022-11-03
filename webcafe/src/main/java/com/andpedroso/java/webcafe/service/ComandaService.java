package com.andpedroso.java.webcafe.service;

import com.andpedroso.java.webcafe.model.Comanda;
import com.andpedroso.java.webcafe.repo.ComandaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ComandaService {
    @Autowired
    private ComandaRepo repo;
    @Transactional(rollbackOn = ChangeSetPersister.NotFoundException.class)
    public Comanda salvar(Comanda obj){
        return repo.save(obj);
    }
    @Transactional(rollbackOn = NoSuchElementException.class)
    public boolean remover(Long id){
        try {
            var comanda = repo.findById(id).orElseThrow();
            repo.delete(comanda);
            return true;
        } catch (NoSuchElementException ex){
            return false;
        }
    }
    public Optional<Comanda> localizar(int mesa){
        return repo.findByMesa(mesa);
    }
    public Optional<Comanda> localizar(Long idComanda){
        return repo.findById(idComanda);
    }
    public Page<Comanda> listar(Integer pageNo, Integer pageSize, String sortBy){
        var paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return repo.findAll(paging);
    }
}
