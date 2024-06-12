package br.com.fiap.postech.goodbuy.item.repository;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import br.com.fiap.postech.goodbuy.item.helper.ItemHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRepositoryTest {
    @Mock
    private ItemRepository itemRepository;

    AutoCloseable openMocks;
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCadastrarItem() {
        // Arrange
        var item = ItemHelper.getItem(false);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        // Act
        var savedItem = itemRepository.save(item);
        // Assert
        assertThat(savedItem).isNotNull().isEqualTo(item);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void devePermitirBuscarItem() {
        // Arrange
        var item = ItemHelper.getItem(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        // Act
        var itemOpcional = itemRepository.findById(item.getId());
        // Assert
        assertThat(itemOpcional).isNotNull().containsSame(item);
        itemOpcional.ifPresent(
                itemRecebido -> {
                    assertThat(itemRecebido).isInstanceOf(Item.class).isNotNull();
                    assertThat(itemRecebido.getId()).isEqualTo(item.getId());
                    assertThat(itemRecebido.getNome()).isEqualTo(item.getNome());
                }
        );
        verify(itemRepository, times(1)).findById(item.getId());
    }
    @Test
    void devePermitirRemoverItem() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(itemRepository).deleteById(id);
        //Act
        itemRepository.deleteById(id);
        //Assert
        verify(itemRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarItems() {
        // Arrange
        var item1 = ItemHelper.getItem(true);
        var item2 = ItemHelper.getItem(true);
        var listaItems = Arrays.asList(
                item1,
                item2
        );
        when(itemRepository.findAll()).thenReturn(listaItems);
        // Act
        var itemsListados = itemRepository.findAll();
        assertThat(itemsListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(item1, item2);
        verify(itemRepository, times(1)).findAll();
    }
}