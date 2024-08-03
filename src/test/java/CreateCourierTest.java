import courier.Courier;
import courier.CourierClient;
import courier.CourierCredentials;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateCourierTest {

    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }
    @Test
    @DisplayName("Создаём курьера, заполнив все обязательные поля")
    public void createCourierOnlyRequiredFieldsResponse201() {
        courier = Courier.getRandomRequiredField();
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndBodyAboutCreation(response);
        CourierCredentials courierCredentials = CourierCredentials.from(courier);
        Response responseLogin = courierClient.postToCourierLogin(courierCredentials);
        courierClient.compareLoginResponseAndBodyIdNotNull(responseLogin);
    }
    @Test
    @DisplayName("Создаём двух одинаковых курьеров")
    public void createTwoIdenticalCouriersResponse409() {
        courier = Courier.getRandom();
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndBodyAboutCreation(response);
        Response responseDuplicate = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndMessageWithError409(responseDuplicate);
    }
    @Test
    @DisplayName("Создаём еще одного курьера с логином, который уже существует")
    public void createCourierWithLoginThatAlreadyExistsResponse409() {
        String password = "123456";
        String firstName = "Ivanov";
        courier = Courier.getRandom();
        Courier courierDuplicate = new Courier(courier.getLogin(), password, firstName);
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareResponseCodeAndBodyAboutCreation(response);
        Response responseDuplicateLogin = courierClient.postCreateToCourier(courierDuplicate);
        courierClient.compareResponseCodeAndMessageWithError409(responseDuplicateLogin);
    }
    @Test
    @DisplayName("Создаем курьера с пустым логином")
    public void createCourierEmptyRequiredLoginResponse400() {
        String login = "";
        String password = "123456";
        String firstName = "Ivanov";
        courier = new Courier(login, password, firstName);
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareCodeAndMessageWithError400(response);
    }
    @Test
    @DisplayName("Создаем курьера с пустым паролем")
    public void createCourierEmptyRequiredPasswordResponse400() {
        String login = "Marzipan";
        String password = "";
        String firstName = "Ivanov";
        courier = new Courier(login, password, firstName);
        Response response = courierClient.postCreateToCourier(courier);
        courierClient.compareCodeAndMessageWithError400(response);
    }
    @After
    @Step("Получение id и удаление курьера")
    public void tearDown() {
        CourierCredentials courierCredentials = CourierCredentials.from(courier);
        Response response = courierClient.postToCourierLogin(courierCredentials);
        if (response.getStatusCode() == 200) {
            int courierId = response.then().extract().path("id");
            if (courierId != 0) {
                courierClient.compareDeleteResponseCodeAndBodyOk(courierClient.deleteCourier(courierId));
            }
        }
    }
}
