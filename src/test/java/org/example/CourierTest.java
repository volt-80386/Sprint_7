package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

public class CourierTest {

    static final String URL = "https://qa-scooter.praktikum-services.ru";

    @Before
    public void init() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Add new courier and check response and status code")
    public void testCreateNewCourier() {
        CourierTestStep createCourier = new CourierTestStep();
        CourierTestStep checkBooleanResponse = new CourierTestStep();
        CourierTestStep checkStatusCode = new CourierTestStep();
        CourierTestStep deleteCourier = new CourierTestStep();
        Response create = createCourier.createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse.checkBooleanResponse(create, "ok", true);
        checkStatusCode.checkStatusCode(create, 201);
        deleteCourier.deleteCourier();
    }

    @Test
    @DisplayName("Add new courier with same login and check status code")
    public void testCreateNewCourierWithSameLogin() {
        CourierTestStep createCourier = new CourierTestStep();
        CourierTestStep checkBooleanResponse = new CourierTestStep();
        CourierTestStep checkStatusCode = new CourierTestStep();
        CourierTestStep deleteCourier = new CourierTestStep();
        Response create = createCourier.createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse.checkBooleanResponse(create, "ok", true);
        checkStatusCode.checkStatusCode(create, 201);
        Response second_create = createCourier.createCourier("helldiver", "12345678", "Kolya");
        checkStatusCode.checkStatusCode(second_create, 409);
        deleteCourier.deleteCourier();
    }

    @Test
    @DisplayName("Add new courier without password and check response and status code")
    public void testCreateNewCourierWithoutRequiredFields() {
        CourierTestStep createCourier = new CourierTestStep();
        CourierTestStep checkTextResponse = new CourierTestStep();
        CourierTestStep checkStatusCode = new CourierTestStep();
        Response create = createCourier.createCourier("helldiver", "", "Kolya");
        checkTextResponse.checkTextResponse(create, "message", "Недостаточно данных для создания учетной записи");
        checkStatusCode.checkStatusCode(create, 400);
    }

    @Test
    @DisplayName("Login with right credentials and check status code")
    public void testCourierGoodLogin () {
        CourierTestStep createCourier = new CourierTestStep();
        CourierTestStep checkBooleanResponse = new CourierTestStep();
        CourierTestStep checkStatusCode = new CourierTestStep();
        CourierTestStep loginCourier = new CourierTestStep();
        CourierTestStep checkIdResponse = new CourierTestStep();
        CourierTestStep deleteCourier = new CourierTestStep();
        Response create = createCourier.createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse.checkBooleanResponse(create, "ok", true);
        checkStatusCode.checkStatusCode(create, 201);
        Response login = loginCourier.loginCourier("helldiver", "12345678");
        checkIdResponse.checkIdResponse(login, "id");
        deleteCourier.deleteCourier();
    }

    @Test
    @DisplayName("Login with bad password and check response and status code")
    public void testCourierLoginWithBadPassword () {
        CourierTestStep createCourier = new CourierTestStep();
        CourierTestStep checkBooleanResponse = new CourierTestStep();
        CourierTestStep checkStatusCode = new CourierTestStep();
        CourierTestStep loginCourier = new CourierTestStep();
        CourierTestStep checkTextResponse = new CourierTestStep();
        CourierTestStep deleteCourier = new CourierTestStep();
        Response create = createCourier.createCourier("helldiver", "12345678", "Kolya");
        checkBooleanResponse.checkBooleanResponse(create, "ok", true);
        checkStatusCode.checkStatusCode(create, 201);
        Response login = loginCourier.loginCourier("helldiver", "123456");
        checkTextResponse.checkTextResponse(login, "message", "Учетная запись не найдена");
        checkStatusCode.checkStatusCode(login, 404);
        deleteCourier.deleteCourier();
    }
}
