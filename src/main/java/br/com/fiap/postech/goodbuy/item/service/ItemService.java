package br.com.fiap.postech.goodbuy.item.service;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ItemService {
    Item save(Item item);

    Page<Item> findAll(Pageable pageable, Item item);

    Item findById(UUID id);

    Item update(UUID id, Item item);

    void delete(UUID id);
}
