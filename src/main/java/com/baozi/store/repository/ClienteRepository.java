package com.baozi.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.baozi.store.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
