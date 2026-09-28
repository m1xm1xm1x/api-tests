package tests;
import config.ApiConfig;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class TestRailApiTest {
    private static final String API = "/index.php?/api/v2/";
    private static final String GET_CASE = "get_case/";
    private static final String GET_CASES = "get_cases/";
    private static final String ADD_CASE = "add_case/";
    private static final String UPDATE_CASE = "update_case/";
    private static final String DELETE_CASE = "delete_case/";
    private static final String GET_HISTORY_FOR_CASE = "get_history_for_case/";
    private static final String MOVE_CASES_TO_SECTION = "move_cases_to_section/";

    @Test
    @DisplayName("API-01 Получение информации о тест-кейсе")
    void getCase() {
        given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .when()
                .get(API + GET_CASE + "131")
                .then()
                .statusCode(200)
                .body("id", equalTo(131))
                .body("title", not(emptyOrNullString()))
                .body("section_id", notNullValue());
    }
    @Test
    @DisplayName("API-02 Получение информации о тест-кейсах")
    void getCases() {
                given()
                    .baseUri(ApiConfig.BASE_URL)
                    .auth()
                    .preemptive()
                    .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .when()
                    .get(API + GET_CASES + ApiConfig.PROJECT_ID + "&suite_id=" + ApiConfig.SUITE_ID)
                .then()
                    .statusCode(200)
                    .body("cases", notNullValue())
                    .body("cases.size()", greaterThan(0))
                    .body("size", greaterThan(0));
    }

    @Test
    @DisplayName("API-03 Создание тест-кейса")
    void addCase() throws Exception {
        Response response = given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .body(Files.readString(
                        Path.of("src/test/resources/json/addCase.json"),
                        StandardCharsets.UTF_8))
                .when()
                .post(API + ADD_CASE + ApiConfig.SECTION_ID);
                response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("Тест-кейс созданный через API"))
                .body("section_id", equalTo(Integer.parseInt(ApiConfig.SECTION_ID)));
    }

    @Test
    @DisplayName("API-04 Удаление тест-кейса")
        void deleteCase() {
                Response response = given()
                        .baseUri(ApiConfig.BASE_URL)
                        .auth()
                        .preemptive()
                        .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                        .contentType("application/json")
                        .body(new File("src/test/resources/json/deleteCase.json"))
                        .when()
                        .post(API + ADD_CASE + ApiConfig.SECTION_ID);
                int caseId = response.jsonPath().getInt("id");
                Response deleteResponse = given()
                        .baseUri(ApiConfig.BASE_URL)
                        .auth()
                        .preemptive()
                        .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                        .contentType("application/json")
                        .when()
                        .post(API + DELETE_CASE + caseId);
                deleteResponse.then()
                        .statusCode(200)
                        .body(emptyString());
    }

    @Test
    @DisplayName("API-05 Обновление тест-кейса")
    void updateCase() {
        Response response = given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .body(new File("src/test/resources/json/createUpdateCase.json"))
                .when()
                .post(API + ADD_CASE + ApiConfig.SECTION_ID);
                int caseId = response.jsonPath().getInt("id");
                given()
                        .baseUri(ApiConfig.BASE_URL)
                        .auth()
                        .preemptive()
                        .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                        .contentType("application/json")
                        .body(new File("src/test/resources/json/updateCase.json"))
                        .when()
                        .post(API + UPDATE_CASE + caseId)
                        .then()
                        .statusCode(200)
                        .body("title", equalTo("Обновленный через API тест-кейс"));
    }
    @Test
    @DisplayName("API-06 Получение информации о тест-кейсе")
    void getHistoryCase() {
        Response response = given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .body(new File("src/test/resources/json/historyCase.json"))
                .when()
                .post(API + ADD_CASE + ApiConfig.SECTION_ID);
        int caseId = response.jsonPath().getInt("id");
        given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .when()
                .get(API + GET_HISTORY_FOR_CASE + caseId)
                .then()
                .statusCode(200)
                .body("history", notNullValue());
    }

    @Test
    @DisplayName("API-07 Перенос тест-кейса в другую секцию")
    void moveTestCase() throws Exception {
        Response response = given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .body(new File("src/test/resources/json/moveCase.json"))
                .when()
                .post(API + ADD_CASE + ApiConfig.SECTION_ID);
                int caseId = response.jsonPath().getInt("id");
        String moveBody = Files.readString(
                Path.of("src/test/resources/json/moveCasesToSection.json"));
        moveBody = moveBody.formatted(caseId);
                given()
                        .baseUri(ApiConfig.BASE_URL)
                        .auth()
                        .preemptive()
                        .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                        .contentType("application/json")
                        .body(moveBody)
                        .when()
                        .post(API + MOVE_CASES_TO_SECTION + ApiConfig.TARGET_SECTION_ID)
                        .then()
                        .statusCode(200)
                        .body(emptyString());
    }
    @Test
    @DisplayName("API-08 Запрос без авторизации")
    void unAuthRequest() {
        given()
                .baseUri(ApiConfig.BASE_URL)
                .when()
                .get(API + GET_CASES + ApiConfig.PROJECT_ID
                        + "&suite_id=" + ApiConfig.SUITE_ID)
                .then()
                .statusCode(401)
                .body("error", notNullValue());
    }
    @Test
    @Disabled("Баг: для несуществующего ID тест-кейса ожидается 400, но сервер возвращает 302")
    @DisplayName("API-09 Запрос несуществующего тест-кейса")
    void getInvalidCase() {
        given()
                .baseUri(ApiConfig.BASE_URL)
                .redirects().follow(false)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .when()
                .get(API + GET_CASE + "abv3")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }
    @Test
    @DisplayName("API-10 Запрос на несуществующий endpoint.")
    void invalidPoint() {
        given()
                .baseUri(ApiConfig.BASE_URL)
                .redirects().follow(false)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .when()
                .get(API + "invalidEndPoint/")
                .then()
                .statusCode(404)
                .body("error", notNullValue());
    }
}

