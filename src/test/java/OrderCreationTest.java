import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import orders.Order;
import orders.OrdersClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private final List<String> colorScooter;
    int track;
    OrdersClient orderClient;

    public OrderCreationTest(List<String> colorScooter) {
        this.colorScooter = colorScooter;
    }

    @Parameterized.Parameters
    public static Object[] getOrderCreation() {
        return new Object[][]{
                {List.of()},
                {List.of("BLACK", "GREY")},
                {List.of("GREY")},
                {List.of("BLACK")},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        orderClient = new OrdersClient();
    }

    @Test
    @DisplayName("Создать заказ с разным цветом скутера")
    public void orderCreateByScooterColor() {
        Order order = Order.createOrderWithColor(colorScooter);
        Response response = orderClient.sendPostCreateToOrders(order);
        orderClient.compareResponseCodeAndBodyAboutOrderCreation(response);
        track = response.then().extract().path("track");
        Response responseGet = orderClient.sendGetToTrackOrder(track);
        orderClient.compareResponse200(responseGet);
    }
}
