package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.List;
import java.util.Arrays;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Enclosed.class)
public class OrderTest {

    @RunWith(Parameterized.class)
    public static class TestMakeOrderWithColorSelect {

        private final String firstName;
        private final String lastName;
        private final String address;
        private final String metroStation;
        private final String phone;
        private final int rentTime;
        private final String deliveryDate;
        private final String comment;
        private final List<String> color;

        public TestMakeOrderWithColorSelect(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.metroStation = metroStation;
            this.phone = phone;
            this.rentTime = rentTime;
            this.deliveryDate = deliveryDate;
            this.comment = comment;
            this.color = color;
        }

        @Parameterized.Parameters
        public static Object[][] getOrderData() {
            return new Object[][]{
                    {"Misha", "Petrov", "Chasovaya str, 15", "26", "9001111110", 3, "2024-08-14", "I'm here", Arrays.asList("BLACK", "GRAY")},
                    {"Masha", "Sidorova", "Chasovaya str, 22", "26", "9001111117", 1, "2024-08-15", "I'm here", List.of("GRAY")},
            };
        }

        @Before
        public void init() {
            RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        }

        @Test
        @DisplayName("Create order with two colors and one color selected and check Status and Track assigned and List order by Track")
        public void TestCreateOrderWithColorsAndCheckStatusAndTrack() {
            Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
            Response response = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(order)
                    .when()
                    .post("/api/v1/orders");
            response.then().statusCode(201);
            JsonPath body = response.jsonPath();
            int track = body.get("track");
            Assert.assertNotEquals(track, 0);
            Response response2 = given()
                    .header("Content-type", "application/json")
                    .when()
                    .get("/api/v1/orders/track?t=" + track);
            response2.then().assertThat().body("order.track", equalTo(track))
                    .and()
                    .statusCode(200);
        }

    }

    @RunWith(Parameterized.class)
    public static class TestMakeOrderWithoutColorSelect {

        private final String firstName;
        private final String lastName;
        private final String address;
        private final String metroStation;
        private final String phone;
        private final int rentTime;
        private final String deliveryDate;
        private final String comment;

        public TestMakeOrderWithoutColorSelect(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.metroStation = metroStation;
            this.phone = phone;
            this.rentTime = rentTime;
            this.deliveryDate = deliveryDate;
            this.comment = comment;
        }

        @Parameterized.Parameters
        public static Object[][] getOrderData() {
            return new Object[][]{
                    {"Serega", "Ivanov", "Chasovaya str, 10", "26", "9001111112", 2, "2024-08-14", "I'm here"},
            };
        }

        @Before
        public void init() {
            RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        }

        @Test
        @DisplayName("Create order without color selected and check Status and Track assigned and List order by Track")
        public void TestCreateOrderWithoutColorAndCheckStatusAndTrack() {
            Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment);
            Response response = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(order)
                    .when()
                    .post("/api/v1/orders");
            response.then().statusCode(201);
            JsonPath body = response.jsonPath();
            int track = body.get("track");
            Assert.assertNotEquals(track, 0);
            Response response2 = given()
                    .header("Content-type", "application/json")
                    .when()
                    .get("/api/v1/orders/track?t=" + track);
            response2.then().assertThat().body("order.track", equalTo(track))
                    .and()
                    .statusCode(200);
        }
    }
}
