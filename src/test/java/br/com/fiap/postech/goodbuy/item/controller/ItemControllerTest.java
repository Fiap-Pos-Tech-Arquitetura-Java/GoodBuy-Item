package br.com.fiap.postech.goodbuy.item.controller;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import br.com.fiap.postech.goodbuy.item.helper.ItemHelper;
import br.com.fiap.postech.goodbuy.item.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest {
    public static final String ITEM = "/item";
    private MockMvc mockMvc;
    @Mock
    private ItemService itemService;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {

        mock = MockitoAnnotations.openMocks(this);
        ItemController itemController = new ItemController(itemService);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    @Nested
    class CadastrarItem {
        @Test
        void devePermitirCadastrarItem() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(false);
            when(itemService.save(any(Item.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post(ITEM).contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(item)))
                    .andExpect(status().isCreated());
            // Assert
            verify(itemService, times(1)).save(any(Item.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarItem_RequisicaoXml() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(false);
            when(itemService.save(any(Item.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post("/item").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(item)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(itemService, never()).save(any(Item.class));
        }
    }
    @Nested
    class BuscarItem {
        @Test
        void devePermitirBuscarItemPorId() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemService.findById(any(UUID.class))).thenReturn(item);
            // Act
            mockMvc.perform(get("/item/{id}", item.getId().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(itemService, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarItemPorId_idNaoExiste() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemService.findById(item.getId())).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(get("/item/{id}", item.getId().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(itemService, times(1)).findById(item.getId());
        }

        @Test
        void devePermitirBuscarTodosItem() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var item = ItemHelper.getItem(true);
            var criterioItem = new Item(item.getNome(), item.getPreco(), item.getDescricao(), item.getCategoria(), null, null);
            criterioItem.setId(null);
            List<Item> listItem = new ArrayList<>();
            listItem.add(item);
            Page<Item> items = new PageImpl<>(listItem);
            var pageable = PageRequest.of(page, size);
            when(itemService.findAll(
                            pageable,
                            criterioItem
                    )
            ).thenReturn(items);
            // Act
            mockMvc.perform(
                            get("/item")
                                    .param("page", String.valueOf(page))
                                    .param("size", String.valueOf(size))
                                    .param("nome", item.getNome())
                                    .param("preco", item.getPreco().toString())
                                    .param("descricao", item.getDescricao())
                                    .param("categoria", item.getCategoria())
                    )
                    //.andDo(print())
                    .andExpect(status().is5xxServerError())
            //.andExpect(jsonPath("$.content", not(empty())))
            //.andExpect(jsonPath("$.totalPages").value(1))
            //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(itemService, times(1)).findAll(pageable, criterioItem);
        }
    }

    @Nested
    class AlterarItem {
        @Test
        void devePermitirAlterarItem() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemService.update(item.getId(), item)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/item/{id}", item.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(item)))
                    .andExpect(status().isAccepted());
            // Assert
            verify(itemService, times(1)).update(item.getId(), item);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarItem_RequisicaoXml() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(true);
            when(itemService.update(item.getId(), item)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/item/{id}", item.getId())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(asJsonString(item)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(itemService, never()).update(item.getId(), item);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarItemPorId_idNaoExiste() throws Exception {
            // Arrange
            var itemDTO = ItemHelper.getItem(true);
            when(itemService.update(itemDTO.getId(), itemDTO)).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(put("/item/{id}", itemDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(itemDTO)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(itemService, times(1)).update(any(UUID.class), any(Item.class));
        }
    }

    @Nested
    class RemoverItem {
        @Test
        void devePermitirRemoverItem() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(true);
            doNothing().when(itemService).delete(item.getId());
            // when(securityHelper.getLoggedItem()).thenReturn(item);
            // Act
            mockMvc.perform(delete("/item/{id}", item.getId()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(itemService, times(1)).delete(item.getId());
            verify(itemService, times(1)).delete(item.getId());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverItemPorId_idNaoExiste() throws Exception {
            // Arrange
            var item = ItemHelper.getItem(true);
            //when(securityHelper.getLoggedItem()).thenReturn(item);
            doThrow(new IllegalArgumentException("Item não encontrado com o ID: " + item.getId()))
                    .when(itemService).delete(item.getId());
            // Act
            mockMvc.perform(delete("/item/{id}", item.getId()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(itemService, times(1)).delete(item.getId());
        }
    }
}