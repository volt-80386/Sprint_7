package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.List;
import java.util.Arrays;

@RunWith(Parameterized.class)
public class OrderTest {
    static final String URL = "https://qa-scooter.praktikum-services.ru";
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final int rentTime;
    private final String deliveryDate;
    private final String comment;
    private final List<String> color;

    public OrderTest(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, List<String> color) {
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
                {"Vitya", "Samokatov", "Chasovaya str, 3", "26", "9001111119", 1, "2024-08-17", "I'm here", List.of("BLACK")},
                {"Serega", "Ivanov", "Chasovaya str, 10", "26", "9001111112", 2, "2024-08-14", "I'm here", List.of()},
        };
    }

    @Before
    public void init() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Create order with two colors, one color, no color selected and check Status and Track assigned and check order by Track")
    public void testCreateOrderAndCheckStatusAndTrack() {
        OrderTestStep createOrder = new OrderTestStep();
        OrderTestStep checkStatusCode = new OrderTestStep();
        OrderTestStep showOrder = new OrderTestStep();
        Response order = createOrder.createOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        int track = checkStatusCode.checkStatusCode(order, 201);
        showOrder.showOrder(track);
    }

    @Test
    @DisplayName("Show orders")
    public void testShowOrders() {
        OrderTestStep showOrders = new OrderTestStep();
        showOrders.showOrders();
    }
}
