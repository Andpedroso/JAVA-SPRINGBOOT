package com.andpedroso.java.webcafe.repo;

import com.andpedroso.java.webcafe.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComandaRepo extends JpaRepository<Comanda, Long> {
    Optional<Comanda> findByMesa(int mesa);
}
