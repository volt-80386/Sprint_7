package org.example;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import io.qameta.allure.junit4.DisplayName;

public class CourierTest {

    @Before
    public void init() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    Courier courier = new Courier("helldiver", "12345678", "Kolya");

    @Test
    @DisplayName("Add new courier and check response and status code")
    public void TestCreateNewCourier() {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Add new courier with same login and check status code")
    public void TestCreateNewCourierWithSameLogin() {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().statusCode(409);
    }

    @Test
    @DisplayName("Add new courier without password and check response and status code")
    public void TestCreateNewCourierWithoutRequiredFields() {
        BadCourier courier = new BadCourier("helldiver", "Kolya");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Login with right credentials and check status code")
    public void TestCourierGoodLogin () {
        Courier courierLogin = new Courier("helldiver", "12345678");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courierLogin)
                .post("/api/v1/courier/login");
        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Login with bad password and check response and status code")
    public void TestCourierLoginWithBadPassword () {
        Courier courierBadPass = new Courier("helldiver", "123456");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courierBadPass)
                .post("/api/v1/courier/login");
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @AfterClass
    public static void deleteCourier() {
        Courier courierLogin = new Courier("helldiver", "12345678");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLogin)
                .when()
                .post("/api/v1/courier/login");
        JsonPath body = response.jsonPath();
        int id = body.get("id");
        Response delete = given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + id);
        delete.then().statusCode(200);
    }
}
