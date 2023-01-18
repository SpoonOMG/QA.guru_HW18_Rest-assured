
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.AuthModel;
import model.CreateUser;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class SimpleRestApiTests {

    String baseUri = "https://reqres.in/";
    String listUsersEndpoint = "/api/users";
    String listResourceEndpoint = "/api/unknown";
    String createEndpoint = "/api/users";
    String loginEndpoint = "/api/login";
    String deleteEndpoint = "/api/users/2";


    @Test
    /*
    Вопросы наставнику:
    1) Как правильно организовать тесты, если я хочу из этого метода брать токен (в переменную)
     и переиспользовать его в других своих тестах? Иными словами, использовать его как предусловие к нескольким тестам?
     При этом хочется избежать дублирования кода и вынести метод отдельно, а не "пихать" в каждый тест, где нужно предварительно
     дернуть токен.
     Вынести отдельно аннотацию в beforeEach не имеет смысла, так как я не могу вернуть из метода
     переменную для дальнейшего ее переиспользования.
    2) Исходя из первого вопроса, как правильно организовать тесты, если я хочу в дальнейшем, допустим, только в половине
     тестов переиспользовать данное предусловие для передачи токена? То есть не перед каждым методом делать запрос на получение
     токена.
     */
    void simpleLoginTest() {
        step("Успешно логинемся и забираем токен в переменную authToken", () -> {
            AuthModel auth = new AuthModel();
            auth.setEmail("eve.holt@reqres.in");
            auth.setPassword("cityslicka");
            Response response = given()
                    .baseUri(baseUri).contentType(ContentType.JSON)
                    .log().all()
                    .when()
                    .body(auth)
                    .post(loginEndpoint)
                    .then().log().all()
                    .statusCode(200)
                    .body("token", is("QpwL5tke4Pnpja7X4"))
                    .extract().response();
            String authToken = response.path("token");
            System.out.println("Используемый токен: " + authToken);
            return authToken;
        });
    }

    @Test
    void simpleCreateUserTest() {
        step("Создаем нового юзера", () -> {
            CreateUser user = new CreateUser();
            user.setName("morpheus");
            user.setJob("leader");
            Response response = given()
                    .baseUri(baseUri)
                    .contentType(ContentType.JSON)
                    .log().all()
                    .when()
                    .body(user)
                    .post(createEndpoint)
                    .then().log().all()
                    .statusCode(201)
                    .body("name", is("morpheus"))
                    .body("job", is("leader"))
                    .extract().response();
            String userId = response.path("id");
            System.out.println("Id пользователя: " + userId);
        });
    }

    @Test
    void simpleGetListUserTest() {
        /*
        Вопросы наставнику:
        1) Как в тестах, где в json-ответе присутствует объект, передающий список, провалидировать:
            а) Размер отданного листа в объекте
            б) Убедиться, что в элементе листа, где id = 8, first_name = Lindsay?
            Какой наиболее простой путь для подобных проверок?
         */
        step("Получаем список юзеров", () -> {
            Response response = given()
                    .baseUri(baseUri)
                    .when().log().all()
                    .param("page", 2)
                    .get(listUsersEndpoint)
                    .then().log().all()
                    .body("page", is(2))
                    .body("total", is(12))
                    .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"))
                    .extract().response();
        });
    }

    @Test
    void simpleListResourceTest() {
        step("Получаем список ресурсов", () -> {
            Response response = given()
                    .baseUri(baseUri)
                    .when().log().all()
                    .param("page", 2)
                    .get(listResourceEndpoint)
                    .then().log().all()
                    .body("page", is(2))
                    .body("total", is(12))
                    .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"))
                    .extract().response();
        });
    }

    @Test
    void simpleDeleteTest() {
        step("Удаляем юзера", () -> {
            given()
                    .baseUri(baseUri)
                    .when().log().all()
                    .delete(deleteEndpoint)
                    .then().log().all()
                    .statusCode(204);

        });

    }


}
