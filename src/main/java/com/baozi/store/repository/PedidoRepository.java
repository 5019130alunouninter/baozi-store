package com.baozi.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baozi.store.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
