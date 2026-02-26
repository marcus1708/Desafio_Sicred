package br.com.automacao;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class Desafio_SicredTest {
    private static String Token,Token_Inv="1234",Token_Vazio="";

    /* GET/test - Buscar o status da aplicação */  
    @Test    @Order(1)
    public void Buscar_Status_Aplic() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
        .when()
            .get("/test")
        .then()
            .statusCode(200)
            .body("status", is("ok"),"method", is("GET"));
    }

    /* GET/test - Buscar usuário para autenticação */
    @Test    @Order(2)
    public void Buscar_Usuario_Aplic() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("users[0].firstName", is("Emily"))
            .body("users[0].lastName", is("Johnson"));
    }

    /* POST /auth/login - Criação de token para Autenticação */
    @Test    @Order(3)
    public void Login_Aplic() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        File body = new File("src/test/resources/json/auth.json");
        Token=given()
            .header("Content-Type", "application/json")
            .body(body)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .path("accessToken");
    }
    
    /* GET /auth/products - Buscar produtos com autenticação */   
    @Test    @Order(4)
    public void Busca_Produtos_Auth() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + Token)
        .when()
            .get("/auth/products")
        .then()
            .statusCode(200);
    }

    /* POST /products/add - Criação de produto */   
    @Test    @Order(5)
    public void Criar_Produto() {
        baseURI = "https://dummyjson.com";
        File body = new File("src/test/resources/json/produto.json");
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
            .body(body)
        .when()
            .post("/products/add")
        .then()
            .statusCode(201)
            .body("title", is("Perfume Oil"),"category", is("fragrances"));
    }

    /* GET /products - Buscar todos os produtos */  
    @Test    @Order(6)
    public void Listar_Produtos() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
        .when()
            .get("/products")
        .then()
            .statusCode(200)
            .body("products[0].title", is("Essence Mascara Lash Princess"),
                  "products[0].category", is("beauty"));
    }

    /* GET/products{id} - Buscar apenas um produto por id */  
    @Test    @Order(7)
    public void Listar_Produtos_ID() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
        .when()
            .get("/products/1")
        .then()
            .statusCode(200)
            .body("title", is("Essence Mascara Lash Princess"),
                  "category", is("beauty"));
    }

    /*  CENARIOS NEGATIVOS  */

    /* POST /auth/login - Usuario inválido */
    @Test    @Order(8)
    public void Login_Aplic_UserInvalid() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        File bodyFile = new File("src/test/resources/json/auth.json");
        JsonPath jsonPath = new JsonPath(bodyFile);

         String username = jsonPath.getString("teste");
         String password = jsonPath.getString("password");

         Map<String, String> body = new HashMap<>();
         body.put("username", username);
         body.put("password", password);
        Token=given()
            .header("Content-Type", "application/json")
            .body(body)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400)
            .extract()
            .path("accessToken");
    }
    
    /* POST /auth/login - Senha inválida */
    @Test    @Order(9)
    public void Login_Aplic_PassInvalid() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        File bodyFile = new File("src/test/resources/json/auth.json");
        JsonPath jsonPath = new JsonPath(bodyFile);

         String username = jsonPath.getString("username");
         String password = jsonPath.getString("teste");

         Map<String, String> body = new HashMap<>();
         body.put("username", username);
         body.put("password", password);
        Token=given()
            .header("Content-Type", "application/json")
            .body(body)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400)
            .extract()
            .path("accessToken");
    }
    
    /* POST /auth/login - token inválido */
    @Test    @Order(10)
    public void Auth_Erro() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + Token_Inv)
        .when()
            .get("/auth/products")
        .then()
            .statusCode(401);
    }

    /* POST /auth/login - token inexistente */
    @Test    @Order(11)
    public void Auth_Vazia() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + Token_Vazio)
        .when()
            .get("/auth/products")
        .then()
            .statusCode(401);
    }
   
    /* GET/products{id} - Buscar apenas um produto por id */  
    @Test    @Order(12)
    public void Listar_Produtos_IDinv() {
        baseURI = "https://dummyjson.com";
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
        given()
            .header("Content-Type", "application/json")
        .when()
            .get("/products/999")
        .then()
            .statusCode(404);
    }

}


























