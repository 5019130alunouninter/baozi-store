package com.baozi.store.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baozi.store.model.Cliente;
import com.baozi.store.model.Pedido;
import com.baozi.store.model.Produto;
import com.baozi.store.repository.ClienteRepository;
import com.baozi.store.repository.PedidoRepository;
import com.baozi.store.repository.ProdutoRepository;

@RestController
@RequestMapping({ "/pedidos" })
public class PedidoController {

	private PedidoRepository repository;
	private ClienteRepository clienteRepository;
	private ProdutoRepository produtoRepository;

	PedidoController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
			ProdutoRepository produtoRepository) {
		this.repository = pedidoRepository;
		this.clienteRepository = clienteRepository;
		this.produtoRepository = produtoRepository;
	}

	@GetMapping
	public List<?> findAll() {
		return repository.findAll();
	}

	@GetMapping(path = { "/{id}" })
	public ResponseEntity<?> findById(@PathVariable long id) {
		return repository.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Pedido pedido) {

		if (pedido.getCliente() == null || pedido.getCliente().getId() == null || pedido.getProdutos() == null
				|| pedido.getProdutos().isEmpty()) {
			return ResponseEntity.badRequest().body("Cliente e produtos são obrigatórios.");
		}

		var cliente = clienteRepository.findById(pedido.getCliente().getId());

		if (cliente.isEmpty()) {
			return ResponseEntity.badRequest().body("Cliente não encontrado.");
		}

		List<Produto> produtos = new ArrayList<>();

		for (Produto produto : pedido.getProdutos()) {

			if (produto == null || produto.getId() == null) {
				return ResponseEntity.badRequest().body("Todos os produtos devem possuir um ID.");
			}

			var encontrado = produtoRepository.findById(produto.getId());

			if (encontrado.isEmpty()) {
				return ResponseEntity.badRequest().body("Produto não encontrado: " + produto.getId());
			}

			produtos.add(encontrado.get());
		}

		pedido.setId(null);
		pedido.setCliente(cliente.get());
		pedido.setProdutos(produtos);

		return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(pedido));
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Pedido pedido) {

		return repository.findById(id).map(record -> {

			if (pedido.getCliente() == null || pedido.getCliente().getId() == null || pedido.getProdutos() == null
					|| pedido.getProdutos().isEmpty()) {
				return ResponseEntity.badRequest().body("Cliente e produtos são obrigatórios.");
			}

			var cliente = clienteRepository.findById(pedido.getCliente().getId());

			if (cliente.isEmpty()) {
				return ResponseEntity.badRequest().body("Cliente não encontrado.");
			}

			List<Produto> produtos = new ArrayList<>();

			for (Produto produto : pedido.getProdutos()) {

				if (produto == null || produto.getId() == null) {
					return ResponseEntity.badRequest().body("Todos os produtos devem possuir um ID.");
				}

				var encontrado = produtoRepository.findById(produto.getId());

				if (encontrado.isEmpty()) {
					return ResponseEntity.badRequest().body("Produto não encontrado: " + produto.getId());
				}

				produtos.add(encontrado.get());
			}

			record.setCliente(cliente.get());
			record.setProdutos(produtos);

			Pedido updated = repository.save(record);

			return ResponseEntity.ok().body(updated);

		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping(path = { "/{id}" })
	public ResponseEntity<?> delete(@PathVariable long id) {
		return repository.findById(id).map(record -> {
			repository.deleteById(id);
			return ResponseEntity.noContent().build();
		}).orElse(ResponseEntity.notFound().build());
	}

}