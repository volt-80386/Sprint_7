package org.example;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTestStep {

    static final String API_CREATE_ORDER = "/api/v1/orders";
    static final String API_CHECK_TRACK = "/api/v1/orders/track?t=";

    @Step("Create order")
    public Response createOrder(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(API_CREATE_ORDER);
    }

    @Step("Check status code and track")
    public int checkStatusCode(Response response, int status_code) {
        response.then().statusCode(status_code);
        JsonPath body = response.jsonPath();
        int track = body.get("track");
        Assert.assertNotEquals(track, 0);
        return track;
    }

    @Step("Show order by track")
    public void showOrder(int track) {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get(API_CHECK_TRACK + track);
        response.then().assertThat().body("order.track", equalTo(track))
                .and()
                .statusCode(200);
    }

    @Step("Show orders")
    public void showOrders() {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get(API_CREATE_ORDER + "?nearestStation=[\"26\"]");
        response.then().assertThat().body("orders.id", notNullValue())
                .and()
                .assertThat().body("orders.track", notNullValue())
                .and()
                .statusCode(200);
    }
}
