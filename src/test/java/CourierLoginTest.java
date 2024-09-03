import courier.Courier;
import courier.CourierClient;
import courier.CourierCredentials;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CourierLoginTest {
    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = Courier.getRandom();
        courierClient.postCreateToCourier(courier);
    }
    @Test
    @DisplayName("Проверка входа с валидными данными")
    public void checkCourierCanLoginWithValidCredentialsResponse200() {
        CourierCredentials courierCredentialsCorrect = CourierCredentials.from(courier);
        Response response = courierClient.postToCourierLogin(courierCredentialsCorrect);
        courierClient.compareLoginResponseAndBodyIdNotNull(response);
    }
    @Test
    @DisplayName("Проверка входа с пустыми логином и паролем")
    public void checkLoginCourierWithInvalidCredentialsResponse400() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials("", "");
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody400Message(response);
    }
    @Test
    @DisplayName("Проверка входа с пустым логином")
    public void checkLoginCourierWitEmptyLoginResponse400() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials("", courier.getPassword());
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody400Message(response);
    }
    @Test
    @DisplayName("Проверка входа с пустым паролем")
    public void checkLoginCourierWitEmptyPasswordResponse400() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials(courier.getLogin(), "");
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody400Message(response);
    }
    @Test
    @DisplayName("Проверка входа с не валидным логином")
    public void checkLoginCourierIncorrectLoginNameResponse404() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials("Marzipan", courier.getPassword());
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody404Message(response);
    }
    @Test
    @DisplayName("Проверка входа с не валидным паролем")
    public void checkLoginCourierIncorrectPasswordResponse404() {
        CourierCredentials courierCredentialsIncorrect = new CourierCredentials(courier.getLogin(), "123456");
        Response response = courierClient.postToCourierLogin(courierCredentialsIncorrect);
        courierClient.compareLoginResponseCodeAndBody404Message(response);
    }
    @After
    public void tearDown() {
        CourierCredentials courierCredentialsCorrect = CourierCredentials.from(courier);
        int courierId = courierClient.postToCourierLogin(courierCredentialsCorrect)
                .then().extract().path("id");
        Response response = courierClient.deleteCourier(courierId);
        courierClient.compareDeleteResponseCodeAndBodyOk(response);
    }
}
