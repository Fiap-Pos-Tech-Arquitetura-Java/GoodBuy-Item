package br.com.fiap.postech.goodbuy.item.helper;

import br.com.fiap.postech.goodbuy.item.entity.Item;

import java.util.UUID;

public class ItemHelper {
    public static Item getItem(boolean geraId) {
        Item item = new Item(
                "VW Golf GTI MK7",
                1000000.01,
                "muito mais que divertido",
                "carros",
                "urlDeUmGolfGTI",
                1L
        );
        if (geraId) {
            item.setId(UUID.randomUUID());
        }
        return item;
    }
}
