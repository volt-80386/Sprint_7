package org.example;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierTestStep {

    static final String API_CREATE_COURIER = "/api/v1/courier/";
    static final String API_LOGIN_COURIER = "/api/v1/courier/login/";

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
