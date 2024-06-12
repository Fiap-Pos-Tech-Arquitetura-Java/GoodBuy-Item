package br.com.fiap.postech.goodbuy.item.service;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import br.com.fiap.postech.goodbuy.item.helper.ItemHelper;
import br.com.fiap.postech.goodbuy.item.repository.ItemRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;


    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        itemService = new ItemServiceImpl(itemRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarItem {
        @Test
        void devePermitirCadastrarItem() {
            // Arrange
            var item = ItemHelper.getItem(false);
            when(itemRepository.save(any(Item.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var itemSalvo = itemService.save(item);
            // Assert
            assertThat(itemSalvo)
                    .isInstanceOf(Item.class)
                    .isNotNull();
            assertThat(itemSalvo.getNome()).isEqualTo(item.getNome());
            assertThat(itemSalvo.getId()).isNotNull();
            verify(itemRepository, times(1)).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_informandoId() {
            // Arrange
            var item = ItemHelper.getItem(true);
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("o id do item é gerado pelo sistema.");
            // Assert
            verify(itemRepository, never()).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_nomeNulo() {
            // Arrange
            var item = ItemHelper.getItem(false);
            item.setNome(null);
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nome do item deve ser informado.");
            // Assert
            verify(itemRepository, never()).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_NomeExistente() {
            // Arrange
            var item = ItemHelper.getItem(false);
            when(itemRepository.findByNome(item.getNome())).thenReturn(Optional.of(item));
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Já existe um item cadastrado com esse nome.");
            // Assert
            verify(itemRepository, times(1)).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_precoNegativo() {
            // Arrange
            var item = ItemHelper.getItem(false);
            item.setPreco(-40.34);
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Preço deve ser maior que zero.");
            // Assert
            verify(itemRepository, never()).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_descricaoNula() {
            // Arrange
            var item = ItemHelper.getItem(false);
            item.setDescricao(null);
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Descricao do item deve ser informada.");
            // Assert
            verify(itemRepository, never()).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_categoriaNula() {
            // Arrange
            var item = ItemHelper.getItem(false);
            item.setCategoria(null);
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Categoria do item deve ser informada.");
            // Assert
            verify(itemRepository, never()).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_urlImagemNula() {
            // Arrange
            var item = ItemHelper.getItem(false);
            item.setUrlImagem(null);
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Url da imagem do item deve ser informada.");
            // Assert
            verify(itemRepository, never()).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_quantidadeNegativa() {
            // Arrange
            var item = ItemHelper.getItem(false);
            item.setQuantidade(-321L);
            // Act
            assertThatThrownBy(() -> itemService.save(item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantidade deve ser maior que um.");
            // Assert
            verify(itemRepository, never()).findByNome(anyString());
            verify(itemRepository, never()).save(any(Item.class));
        }
    }

    @Nested
    class BuscarItem {
        @Test
        void devePermitirBuscarItemPorId() {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            // Act
            var itemObtido = itemService.findById(item.getId());
            // Assert
            assertThat(itemObtido).isEqualTo(item);
            verify(itemRepository, times(1)).findById(any(UUID.class));
        }

/*        @Test
        void devePermitirBuscarItemPorLogin() {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemRepository.findByLogin(item.getLogin())).thenReturn(Optional.of(item));
            // Act
            var itemObtido = itemService.findByLogin(item.getLogin());
            // Assert
            assertThat(itemObtido).isEqualTo(item);
            verify(itemRepository, times(1)).findByLogin(anyString());
        }*/

        @Test
        void deveGerarExcecao_QuandoBuscarItemPorId_idNaoExiste() {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());
            UUID uuid = item.getId();
            // Act
            assertThatThrownBy(() -> itemService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Item não encontrado com o ID: " + item.getId());
            // Assert
            verify(itemRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosItem() {
            // Arrange
            Item criteriosDeBusca = ItemHelper.getItem(false);
            Page<Item> items = new PageImpl<>(Arrays.asList(
                    ItemHelper.getItem(true),
                    ItemHelper.getItem(true),
                    ItemHelper.getItem(true)
            ));
            when(itemRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(items);
            // Act
            var itemsObtidos = itemService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(itemsObtidos).hasSize(3);
            assertThat(itemsObtidos.getContent()).asList().allSatisfy(
                    item -> {
                        assertThat(item)
                                .isNotNull()
                                .isInstanceOf(Item.class);
                    }
            );
            verify(itemRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarItem {
        @Test
        void devePermitirAlterarItem() {
            // Arrange
            var item = ItemHelper.getItem(true);
            var itemReferencia = new Item(item.getNome(), item.getPreco(), item.getDescricao(), item.getCategoria(), item.getUrlImagem(), item.getQuantidade());
            var novoItem = new Item(
                    RandomStringUtils.random(20, true, true),
                    12.01D,
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    123L
            );
            novoItem.setId(item.getId());
            when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            when(itemRepository.save(any(Item.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var itemSalvo = itemService.update(item.getId(), novoItem);
            // Assert
            assertThat(itemSalvo)
                    .isInstanceOf(Item.class)
                    .isNotNull();
            assertThat(itemSalvo.getNome()).isEqualTo(novoItem.getNome());
            assertThat(itemSalvo.getNome()).isNotEqualTo(itemReferencia.getNome());

            assertThat(itemSalvo.getPreco()).isEqualTo(novoItem.getPreco());
            assertThat(itemSalvo.getPreco()).isNotEqualTo(itemReferencia.getPreco());

            assertThat(itemSalvo.getDescricao()).isEqualTo(novoItem.getDescricao());
            assertThat(itemSalvo.getDescricao()).isNotEqualTo(itemReferencia.getDescricao());

            assertThat(itemSalvo.getCategoria()).isEqualTo(novoItem.getCategoria());
            assertThat(itemSalvo.getCategoria()).isNotEqualTo(itemReferencia.getCategoria());

            assertThat(itemSalvo.getUrlImagem()).isEqualTo(novoItem.getUrlImagem());
            assertThat(itemSalvo.getUrlImagem()).isNotEqualTo(itemReferencia.getUrlImagem());

            assertThat(itemSalvo.getQuantidade()).isEqualTo(novoItem.getQuantidade());
            assertThat(itemSalvo.getQuantidade()).isNotEqualTo(itemReferencia.getQuantidade());

            verify(itemRepository, times(1)).findById(any(UUID.class));
            verify(itemRepository, times(1)).save(any(Item.class));
        }

        @Test
        void devePermitirAlterarItem_semBody() {
            // Arrange
            var item = ItemHelper.getItem(true);
            var itemReferencia = new Item(item.getNome(), item.getPreco(), item.getDescricao(), item.getCategoria(), item.getUrlImagem(), item.getQuantidade());
            var novoItem = new Item(null, null, null, null, null, null);

            novoItem.setId(item.getId());
            when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            when(itemRepository.save(any(Item.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var itemSalvo = itemService.update(item.getId(), novoItem);
            // Assert
            assertThat(itemSalvo)
                    .isInstanceOf(Item.class)
                    .isNotNull();
            assertThat(itemSalvo.getNome()).isEqualTo(itemReferencia.getNome());

            verify(itemRepository, times(1)).findById(any(UUID.class));
            verify(itemRepository, times(1)).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarItemPorId_idNaoExiste() {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());
            UUID uuid = item.getId();
            // Act && Assert
            assertThatThrownBy(() -> itemService.update(uuid, item))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Item não encontrado com o ID: " + item.getId());
            verify(itemRepository, times(1)).findById(any(UUID.class));
            verify(itemRepository, never()).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarItemPorId_alterandoId() {
            // Arrange
            var item = ItemHelper.getItem(true);
            var itemParam = ItemHelper.getItem(true);
            when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            UUID uuid = item.getId();
            // Act && Assert
            assertThatThrownBy(() -> itemService.update(uuid, itemParam))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o id de um item.");
            verify(itemRepository, times(1)).findById(any(UUID.class));
            verify(itemRepository, never()).save(any(Item.class));
        }
    }

    @Nested
    class RemoverItem {
        @Test
        void devePermitirRemoverItem() {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            doNothing().when(itemRepository).deleteById(item.getId());
            // Act
            itemService.delete(item.getId());
            // Assert
            verify(itemRepository, times(1)).findById(any(UUID.class));
            verify(itemRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandRemoverItemPorId_idNaoExiste() {
            // Arrange
            var item = ItemHelper.getItem(true);
            doNothing().when(itemRepository).deleteById(item.getId());
            UUID uuid = item.getId();
            // Act && Assert
            assertThatThrownBy(() -> itemService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Item não encontrado com o ID: " + item.getId());
            verify(itemRepository, times(1)).findById(any(UUID.class));
            verify(itemRepository, never()).deleteById(any(UUID.class));
        }
    }
}
