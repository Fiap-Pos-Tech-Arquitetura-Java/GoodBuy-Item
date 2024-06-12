package br.com.fiap.postech.goodbuy.item.service;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import br.com.fiap.postech.goodbuy.item.repository.ItemRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item save(Item item) {
        validaNome(item);
        if (item.getId() != null) {
            throw new IllegalArgumentException("o id do item é gerado pelo sistema.");
        }
        validaPreco(item);
        validaDescricao(item);
        validaCategoria(item);
        validaUrlImagem(item);
        validaQuantidade(item);
        if (itemRepository.findByNome(item.getNome()).isPresent()) {
            throw new IllegalArgumentException("Já existe um item cadastrado com esse nome.");
        }
        item.setId(UUID.randomUUID());
        return itemRepository.save(item);
    }

    @Override
    public Page<Item> findAll(Pageable pageable, Item item) {
        Example<Item> itemExample = Example.of(item);
        return itemRepository.findAll(itemExample, pageable);
    }

    @Override
    public Item findById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado com o ID: " + id));
    }

    @Override
    public Item update(UUID id, Item itemParam) {
        Item item = findById(id);
        if (itemParam.getId() != null && !item.getId().equals(itemParam.getId())) {
            throw new IllegalArgumentException("Não é possível alterar o id de um item.");
        }
        if (StringUtils.isNotEmpty(itemParam.getNome())) {
            item.setNome(itemParam.getNome());
        }
        if (itemParam.getPreco() != null) {
            validaPreco(itemParam);
            item.setPreco(itemParam.getPreco());
        }
        if (StringUtils.isNotEmpty(itemParam.getDescricao())) {
            item.setDescricao(itemParam.getDescricao());
        }
        if (StringUtils.isNotEmpty(itemParam.getCategoria())) {
            item.setCategoria(itemParam.getCategoria());
        }
        if (StringUtils.isNotEmpty(itemParam.getUrlImagem())) {
            validaUrlImagem(itemParam);
            item.setUrlImagem(itemParam.getUrlImagem());
        }
        if (itemParam.getQuantidade() != null) {
            validaQuantidade(itemParam);
            item.setQuantidade(itemParam.getQuantidade());
        }
        item = itemRepository.save(item);
        return item;
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        itemRepository.deleteById(id);
    }

    private static void validaPreco(Item item) {
        if (item.getPreco().compareTo(0d) < 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
    }

    private static void validaQuantidade(Item item) {
        if (item.getQuantidade() < 1) {
            throw new IllegalArgumentException("Quantidade deve ser maior que um.");
        }
    }

    private static void validaNome(Item item) {
        if (StringUtils.isEmpty(item.getNome())) {
            throw new IllegalArgumentException("Nome do item deve ser informado.");
        }
    }

    private void validaDescricao(Item item) {
        if (StringUtils.isEmpty(item.getDescricao())) {
            throw new IllegalArgumentException("Descricao do item deve ser informada.");
        }
    }

    private void validaCategoria(Item item) {
        if (StringUtils.isEmpty(item.getCategoria())) {
            throw new IllegalArgumentException("Categoria do item deve ser informada.");
        }
    }

    private void validaUrlImagem(Item item) {
        if (StringUtils.isEmpty(item.getUrlImagem())) {
            throw new IllegalArgumentException("Url da imagem do item deve ser informada.");
        }
    }
}
