package br.com.fiap.postech.goodbuy.item.controller;

import br.com.fiap.postech.goodbuy.item.entity.Item;
import br.com.fiap.postech.goodbuy.item.helper.ItemHelper;
import br.com.fiap.postech.goodbuy.item.helper.UserHelper;
import br.com.fiap.postech.goodbuy.security.UserDetailsServiceImpl;
import br.com.fiap.postech.goodbuy.security.enums.UserRole;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class ItemControllerIT {

    public static final String ITEM = "/goodbuy/item";
    @LocalServerPort
    private int port;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarItem {
        @Test
        void devePermitirCadastrarItem() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = ItemHelper.getItem(false);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .body(item)
            .when()
                .post(ITEM)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/item.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_UserNaoAdministrativo() {
            var user = UserHelper.getUser(UserRole.USER);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = ItemHelper.getItem(false);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .body(item)
            .when()
                .post(ITEM)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarItem_RequisicaoXml() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            given()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .post(ITEM)
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarItem {
        @Test
        void devePermitirBuscarItemPorId_userAdmin() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var id = "b04fa8fb-2de7-4589-9606-94e834acf310";
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .get(ITEM + "/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/item.schema.json"));
        }

        @Test
        void devePermitirBuscarItemPorId() {
            var user = UserHelper.getUser(UserRole.USER);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var id = "b04fa8fb-2de7-4589-9606-94e834acf310";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                    .when()
                    .get(ITEM + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/item.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarItemPorId_idNaoExiste() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var id = ItemHelper.getItem(true).getId();
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .get(ITEM + "/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosItem_userAdmin() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                    .when()
                    .get(ITEM)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/item.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosItem() {
            var user = UserHelper.getUser(UserRole.USER);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                    .when()
                    .get(ITEM)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/item.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosItem_ComPaginacao_userAdmin() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            given()
                    .queryParam("page", "1")
                    .queryParam("size", "1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                    .when()
                    .get(ITEM)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/item.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosItem_ComPaginacao() {
            var user = UserHelper.getUser(UserRole.USER);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            given()
                    .queryParam("page", "1")
                    .queryParam("size", "1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                    .when()
                    .get(ITEM)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/item.page.schema.json"));
        }
    }

    @Nested
    class AlterarItem {
        @Test
        void devePermitirAlterarItem() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = new Item(
                    "kaiby.santos",
                    1234.34,
                    "52816804046",
                    "basdfa",
                    "124355675",
                    123545667895L
            );
            item.setId(UUID.fromString("e83807a3-31fc-4b56-988c-93eb36f13925"));
            given()
                .body(item)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .put(ITEM + "/{id}", item.getId())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/item.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarItem_UserNaoAdministrativo() {
            var user = UserHelper.getUser(UserRole.USER);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = new Item(
                    "kaiby.santos",
                    1234.34,
                    "52816804046",
                    "basdfa",
                    "124355675",
                    123545667895L
            );
            item.setId(UUID.fromString("e83807a3-31fc-4b56-988c-93eb36f13925"));
            given()
                    .body(item)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                    .when()
                    .put(ITEM + "/{id}", item.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarItem_RequisicaoXml() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = ItemHelper.getItem(true);
            given()
                .body(item)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .put(ITEM + "/{id}", item.getId())
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarItemPorId_idNaoExiste() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = ItemHelper.getItem(true);
            given()
                .body(item)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .put(ITEM + "/{id}", item.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Item não encontrado com o ID: " + item.getId()));
        }
    }

    @Nested
    class RemoverItem {
        @Test
        void devePermitirRemoverItem() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = new Item();
            item.setId(UUID.fromString("759ae7fa-2cd2-46ef-9c54-737a4d9d408d"));
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .delete(ITEM + "/{id}", item.getId())
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverItem_UserNaoAdministrativo() {
            var user = UserHelper.getUser(UserRole.USER);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = new Item();
            item.setId(UUID.fromString("759ae7fa-2cd2-46ef-9c54-737a4d9d408d"));
            given()
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                    .when()
                    .delete(ITEM + "/{id}", item.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverItemPorId_idNaoExiste() {
            var user = UserHelper.getUser(UserRole.ADMIN);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            var item = ItemHelper.getItem(true);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .delete(ITEM + "/{id}", item.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Item não encontrado com o ID: " + item.getId()));
        }

        /*@Test
        void deveGerarExcecao_QuandoRemoverItemPorId_roleItem() {
            var item = new Item(
                    "usuario.comum",
                    "Jose da Silva",
                    "'07379758063'",
                    null,
                    ItemRole.ITEM
            );
            item.setId(UUID.fromString("3929bac7-149a-443d-9a86-5afec529aaba"));
            given()
                    .header(HttpHeaders.AUTHORIZATION, ItemHelper.getToken(item))
                    .when()
                    .delete(ITEM + "/{id}", item.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(equalTo("item não tem perfil para executar essa operação"));
        }*/
    }
}
