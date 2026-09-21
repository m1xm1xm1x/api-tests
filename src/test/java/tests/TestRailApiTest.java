package tests;
import config.ApiConfig;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.DisplayName;

public class TestRailApiTest {
    @Test
    @DisplayName("API-01 Получение информации о тест-кейсе")
    void getCase() {
        given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .when()
                .get("/index.php?/api/v2/get_case/131")
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
                    .get("/index.php?/api/v2/get_cases/" + ApiConfig.PROJECT_ID + "&suite_id=" + ApiConfig.SUITE_ID)
                .then()
                    .statusCode(200)
                    .body("cases", notNullValue())
                    .body("cases.size()", greaterThan(0))
                    .body("size", greaterThan(0));
    }

    @Test
    @DisplayName("API-03 Создание тест-кейса")
    void addCase() {
        Response response = given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .body(""" 
                        {                                        
                        "title": "Тест-кейс созданный через API"              
                        }
                          """)
                .when()
                .post("/index.php?/api/v2/add_case/" + ApiConfig.SECTION_ID);
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
                        .body(""" 
                        {
                          "title": "Тест-кейст созданный и удаленный через API"
                          }
                        """)
                        .when()
                        .post("/index.php?/api/v2/add_case/" + ApiConfig.SECTION_ID);
                int caseId = response.jsonPath().getInt("id");
                Response deleteResponse = given()
                        .baseUri(ApiConfig.BASE_URL)
                        .auth()
                        .preemptive()
                        .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                        .contentType("application/json")
                        .when()
                        .post("/index.php?/api/v2/delete_case/" + caseId);
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
                .body(""" 
                      {
                      "title": "Тест-кейс для обновления через API"
                      }
                      """)
                .when()
                .post("/index.php?/api/v2/add_case/" + ApiConfig.SECTION_ID);
                int caseId = response.jsonPath().getInt("id");
                given()
                        .baseUri(ApiConfig.BASE_URL)
                        .auth()
                        .preemptive()
                        .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                        .contentType("application/json")
                        .body(""" 
                            { "title": "Обновленный через API тест-кейс"
                            }
                          """)
                        .when()
                        .post("/index.php?/api/v2/update_case/" + caseId)
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
                .body(""" 

                        { "title": "Тест-кейс для проверки истории через API"
                      }
                      """)
                .when()
                .post("/index.php?/api/v2/add_case/" + ApiConfig.SECTION_ID);
        int caseId = response.jsonPath().getInt("id");
        given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .when()
                .get("/index.php?/api/v2/get_history_for_case/" + caseId)
                .then()
                .statusCode(200)
                .body("history", notNullValue());
    }

    @Test
    @DisplayName("API-07 Перенос тест-кейса в другую секцию")
    void moveTestCase() {
        Response response = given()
                .baseUri(ApiConfig.BASE_URL)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .contentType("application/json")
                .body(""" 
                      {
                      "title": "Тест кейс для переноса через API"                                            
                      }
                      """)
                .when()
                .post("/index.php?/api/v2/add_case/" + ApiConfig.SECTION_ID);
                int caseId = response.jsonPath().getInt("id");
                given()
                        .baseUri(ApiConfig.BASE_URL)
                        .auth()
                        .preemptive()
                        .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                        .contentType("application/json")
                        .body("{\"case_ids\": [" + caseId + "]}")
                        .when()
                        .post("/index.php?/api/v2/move_cases_to_section/" + ApiConfig.TARGET_SECTION_ID)
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
                .get("/index.php?/api/v2/get_cases/" + ApiConfig.PROJECT_ID
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
                .redirects().follow(false)
                .auth()
                .preemptive()
                .basic(ApiConfig.EMAIL, ApiConfig.API_KEY)
                .when()
                .get("/index.php?/api/v2/get_case/999")
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
                .get("/index.php?/api/v2/get_case_wrong/")
                .then()
                .statusCode(404)
                .body("error", notNullValue());
    }
}

