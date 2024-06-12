package br.com.fiap.postech.goodbuy.item.repository;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    Optional<Item> findByNome(String nome);
}
