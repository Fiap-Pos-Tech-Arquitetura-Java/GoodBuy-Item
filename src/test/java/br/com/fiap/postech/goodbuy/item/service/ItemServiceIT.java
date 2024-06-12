package br.com.fiap.postech.goodbuy.item.service;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import br.com.fiap.postech.goodbuy.item.helper.ItemHelper;
import br.com.fiap.postech.goodbuy.item.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class ItemServiceIT {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceIT(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = itemRepository.count();
        assertThat(totalRegistros).isEqualTo(4);
    }

    @Test
    void devePermitirCadastrarItem() {
        // Arrange
        var item = ItemHelper.getItem(true);
        // Act
        var itemCadastrado = itemRepository.save(item);
        // Assert
        assertThat(itemCadastrado).isInstanceOf(Item.class).isNotNull();
        assertThat(itemCadastrado.getId()).isEqualTo(item.getId());
        assertThat(itemCadastrado.getNome()).isEqualTo(item.getNome());
    }
    @Test
    void devePermitirBuscarItem() {
        // Arrange
        var id = UUID.fromString("b04fa8fb-2de7-4589-9606-94e834acf310");
        var name = "Fullers London Pride";
        // Act
        var itemOpcional = itemRepository.findById(id);
        // Assert
        assertThat(itemOpcional).isPresent();
        itemOpcional.ifPresent(
                itemRecebido -> {
                    assertThat(itemRecebido).isInstanceOf(Item.class).isNotNull();
                    assertThat(itemRecebido.getId()).isEqualTo(id);
                    assertThat(itemRecebido.getNome()).isEqualTo(name);
                }
        );
    }
    @Test
    void devePermitirRemoverItem() {
        // Arrange
        var id = UUID.fromString("8855e7b2-77b6-448b-97f8-8a0b529f3976");
        // Act
        itemRepository.deleteById(id);
        // Assert
        var itemOpcional = itemRepository.findById(id);
        assertThat(itemOpcional).isEmpty();
    }
    @Test
    void devePermitirListarItems() {
        // Arrange
        // Act
        var itemsListados = itemRepository.findAll();
        // Assert
        assertThat(itemsListados).hasSize(4);
    }
}
