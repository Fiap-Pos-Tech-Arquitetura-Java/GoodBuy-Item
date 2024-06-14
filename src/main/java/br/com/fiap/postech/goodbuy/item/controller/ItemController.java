package br.com.fiap.postech.goodbuy.item.controller;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import br.com.fiap.postech.goodbuy.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Service
@RequestMapping("/item")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "registra um item")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Item> save(@Valid @RequestBody Item itemDTO) {
        Item savedItemDTO = itemService.save(itemDTO);
        return new ResponseEntity<>(savedItemDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos os items")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<Item>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Double preco,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String categoria
    ) {
        Item item = new Item(nome, preco, descricao, categoria, null, null);
        item.setId(null);
        var pageable = PageRequest.of(page, size);
        var items = itemService.findAll(pageable, item);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @Operation(summary = "lista um item por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            Item item = itemService.findById(id);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "altera um item por seu id")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody Item itemDTO) {
        try {
            Item updatedItem = itemService.update(id, itemDTO);
            return new ResponseEntity<>(updatedItem, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um item por seu id")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            itemService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException
                exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
