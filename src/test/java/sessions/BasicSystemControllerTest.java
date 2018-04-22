package sessions;

import categories.Category;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.annotation.DependsOn;
import transactions.Transaction;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.DOUBLE;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BasicSystemControllerTest {
    public static final String API_URL = "/api/v0";

    @Test
    public void basicSystemTest() throws Exception {
        newSession();
        newTransaction();
        getTransaction();
        getTransactions();
        newCategory();
        getCategory();
        getCategories();
        assignCategory();
    }

    //@Test
    private void newSession() throws Exception {
        when().get(API_URL + "/sessions").then().statusCode(200).body("sessionID", equalTo(1));
        when().get(API_URL + "/sessions").then().statusCode(200).body("sessionID", equalTo(2));
    }

    //@Test
    private void newTransaction() throws Exception {
        Transaction testTransaction = new Transaction();
        testTransaction.setAmount(500.00);
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
                contentType("application/json").
                body(testTransaction).
        when().
                post(API_URL + "/{sessionId}/transactions", 1).
        then().
                statusCode(201).
                body(
                        "amount", equalTo(500.00),
                        "id", equalTo(3)
                );

        testTransaction.setAmount(250.00);
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
                contentType("application/json").
                body(testTransaction).
        when().
                post(API_URL + "/{sessionId}/transactions", 1).
        then().
                statusCode(201).
                body(
                        "amount", equalTo(250.00),
                        "id", equalTo(4)
                );

        testTransaction.setAmount(750.00);
        testTransaction.setId(2);
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
                contentType("application/json").
                body(testTransaction).
        when().
                post(API_URL + "/{sessionId}/transactions", 1).
        then().
                statusCode(201).
                body(
                    "amount", equalTo(750.00),
                    "id", equalTo(5)
                );
    }

    //@Test
    private void getTransaction() throws Exception {
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
        when().
                get(API_URL + "/{sessionId}/transactions/{transactionId}",1,3).
        then().
                body(
                        "id", equalTo(3),
                        "amount", equalTo(500.00)
                );
    }

    //@Test
    private void getTransactions() throws Exception {
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
        when().
                get(API_URL + "/{sessionId}/transactions/", 1).
        then().
                body("3.amount", equalTo(500.00), "4.amount", equalTo(250.00));
    }

    //@Test
    private void newCategory() throws Exception {
        Category testCategory = new Category();
        testCategory.setName("testCategory");
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
                contentType("application/json").
                body(testCategory).
        when().
                post(API_URL + "/{sessionId}/categories", 1).
        then().
                statusCode(201).
                body(
                        "name", equalTo("testCategory"),
                        "id", equalTo(6)
                )
        ;
        testCategory.setId(3);
        testCategory.setName("anotherCategory");
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
                contentType("application/json").
                body(testCategory).
        when().
                post(API_URL + "/{sessionId}/categories", 1).
        then().
                statusCode(201).
                body(
                        "name", equalTo("anotherCategory"),
                        "id", equalTo(7)
                )
        ;
    }

    //@Test
    private void getCategory() throws Exception {
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
        when().
                get(API_URL+"/{sessionId}/categories/{categoryId}",1,6).
        then().
                statusCode(200).body(
                        "name", equalTo("testCategory"),
                "id", equalTo(6)
        );
    }

    //@Test
    private void assignCategory() throws Exception {
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
                contentType("application/json").
                param("categoryId",6).
        when().
                patch(API_URL + "/{sessionId}/transactions/{transactionId}", 1, 4).
        then().
                statusCode(200).
                body(
                        "amount", equalTo(250.00),
                        "category.name", equalTo("testCategory")
                );
    }

    //@Test
    private void getCategories() throws Exception {
        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE))).
                contentType("application/json").
        when().
                get(API_URL + "/{sessionId}/categories",1).
        then().
                body("6.name", equalTo("testCategory"), "7.name", equalTo("anotherCategory"));
    }

}