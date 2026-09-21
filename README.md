# API Test Automation
Проект автоматизации API-тестирования Testrail

## Технологии

- Java 17
- Gradle
- JUnit 5
- Rest Assured
- TestRail API

## Настройка окружения

Для запуска тестов необходимо задать переменные окружения:

```powershell
$env:testRailApiUrl = "https://your-instance.testrail.io"
$env:testRailApiEmail = "your-email"
$env:testRailApiKey = "your-api-key"
$env:testRailProject = "your-project-id"
$env:testRailSuite = "your-suite-id"
$env:testRailSection = "your-section-id"
$env:testRailTargetSection = "your-target-section-id"
```

Реальные данные авторизации и API Key в репозитории не хранятся.


## Запуск тестов

Запуск всех API-тестов:

```powershell
.\gradlew.bat clean test --no-daemon
```

Запуск одного теста:

```powershell
.\gradlew.bat test --tests "tests.TestRailApiTest.getCase" --no-daemon
```

## Отчёт о тестировании

После запуска Gradle HTML-отчёт находится по пути:

```text
build/reports/tests/test/index.html
```

## API тест-кейсы

| ID | Проверка | Метод |
|---|---|---|
| API-01 | Получение информации о тест-кейсе | `getCase()` |
| API-02 | Получение информации о тест-кейсах | `getCases()` |
| API-03 | Создание тест-кейса | `addCase()` |
| API-04 | Удаление тест-кейса | `deleteCase()` |
| API-05 | Обновление тест-кейса | `updateCase()` |
| API-06 | Получение истории тест-кейса | `getHistoryCase()` |
| API-07 | Перенос тест-кейса в другую секцию | `moveTestCase()` |
| API-08 | Запрос без авторизации | `unAuthRequest()` |
| API-09 | Запрос несуществующего тест-кейса | `getInvalidCase()` |
| API-10 | Запрос на несуществующий endpoint | `invalidPoint()` |

## Баг

API-09 временно отключён через `@Disabled`.

Ожидаемый результат для запроса несуществующего test case ID — HTTP 400.
Фактический результат TestRail — HTTP 302.

В тесте сохранён ожидаемый статус 400, фактическое некорректное поведение не используется как ожидаемый результат.