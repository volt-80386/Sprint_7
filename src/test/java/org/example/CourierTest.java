package org.example;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import io.qameta.allure.junit4.DisplayName;

public class CourierTest {

    static final String URL = "https://qa-scooter.praktikum-services.ru";
    static final String API_CREATE_COURIER = "/api/v1/courier/";
    static final String API_LOGIN_COURIER = "/api/v1/courier/login/";

    @Before
    public void init() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Add new courier and check response and status code")
    public void testCreateNewCourier() {
        Response create = createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse(create, "ok", true);
        checkStatusCode(create, 201);
        deleteCourier();
    }

    @Test
    @DisplayName("Add new courier with same login and check status code")
    public void testCreateNewCourierWithSameLogin() {
        Response create = createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse(create, "ok", true);
        checkStatusCode(create, 201);
        Response second_create = createCourier("helldiver", "12345678", "Kolya");
        checkStatusCode(second_create, 409);
        deleteCourier();
    }

    @Test
    @DisplayName("Add new courier without password and check response and status code")
    public void testCreateNewCourierWithoutRequiredFields() {
        Response create = createCourier("helldiver", "", "Kolya");
        checkTextResponse(create, "message", "Недостаточно данных для создания учетной записи");
        checkStatusCode(create, 400);
    }

    @Test
    @DisplayName("Login with right credentials and check status code")
    public void testCourierGoodLogin () {
        Response create = createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse(create, "ok", true);
        checkStatusCode(create, 201);
        Response login = loginCourier("helldiver", "12345678");
        checkIdResponse(login, "id");
        deleteCourier();
    }

    @Test
    @DisplayName("Login with bad password and check response and status code")
    public void testCourierLoginWithBadPassword () {
        Response create = createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse(create, "ok", true);
        checkStatusCode(create, 201);
        Response login = loginCourier("helldiver", "123456");
        checkTextResponse(login, "message", "Учетная запись не найдена");
        checkStatusCode(login, 404);
        deleteCourier();
    }

    @Step("POST credentials to create courier")
    public Response createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(API_CREATE_COURIER);
    }

    @Step("Check boolean response")
    public void checkBooleanResponse(Response response, String json_field, boolean expected_value) {
        response.then().assertThat().body(json_field, equalTo(expected_value));
    }

    @Step("Check text response")
    public void checkTextResponse(Response response, String json_field, String expected_value) {
        response.then().assertThat().body(json_field, equalTo(expected_value));
    }

    @Step("Check id")
    public void checkIdResponse(Response response, String json_field) {
        response.then().assertThat().body(json_field, notNullValue());
    }

    @Step("Check status code")
    public void checkStatusCode(Response response, int status_code) {
        response.then().statusCode(status_code);
    }

    @Step("POST login courier")
    public Response loginCourier(String login, String password) {
        Courier courier = new Courier(login, password);
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(API_LOGIN_COURIER);
    }

    @Step("DELETE courier")
    public void deleteCourier() {
        Response response = loginCourier("helldiver", "12345678");
        JsonPath body = response.jsonPath();
        int id = body.get("id");
        Response delete = given()
                .header("Content-type", "application/json")
                .delete(API_CREATE_COURIER + id);
        delete.then().statusCode(200);
    }
}
