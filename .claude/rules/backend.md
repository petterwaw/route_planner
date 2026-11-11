---
paths:
  - "backend/**"
---

# Backend: konwencje

## Spring Boot 4 (twoja wiedza może być sprzed tej wersji)

- Zależności dodawaj przez start.spring.io albo sprawdzaj ich nazwy w oficjalnym przewodniku migracji do 4.0. Nie pisz `pom.xml` z pamięci.
- Startery mają nowe nazwy: `spring-boot-starter-webmvc` (zamiast `-web`), `spring-boot-starter-flyway`, `spring-boot-starter-h2-console`, `spring-boot-starter-restclient`. Każdy ma odpowiednik testowy, np. `spring-boot-starter-webmvc-test`.
- JSON obsługuje Jackson 3 w pakietach `tools.jackson.*`. Sprawdź w dokumentacji, które adnotacje zostały w starym pakiecie.
- Gdy coś „nie konfiguruje się samo", najpierw sprawdź, czy w projekcie jest właściwy starter.

## Struktura

- Pakiet bazowy `pl.zdaze`, podział według funkcji (lista w `docs/architecture.md`).
- Kontroler robi tylko mapowanie HTTP i walidację wejścia. Logika jest w serwisach i w klasach domenowych, w miarę możliwości bez zależności od Springa.
- DTO jako `record`. Encji JPA nie zwracaj z kontrolerów.
- Zewnętrzne usługi tylko za interfejsem: `RouteProvider`, `ScheduleExtractor`. Każdy ma implementację `mock`.

## Zasady

- Teksty dla użytkownika po angielsku: `detail` błędów, komunikaty walidacji, opisy `summary`.
- Czas: wstrzykuj `java.time.Clock`. Strefa z konfiguracji (`Europe/Warsaw`).
- Błędy: `ProblemDetail` z polem `code` (lista kodów w `docs/api.md`), przez jeden `@RestControllerAdvice`.
- Walidacja: `jakarta.validation` na DTO wejściowych.
- Baza: tylko migracje Flyway w `src/main/resources/db/migration`, SQL przenośny (bez składni specyficznej dla H2). Hibernate w trybie `validate`.
- HTTP na zewnątrz: `RestClient` z limitami czasu (połączenie 3 s, odczyt 10 s; dla AI odczyt 60 s).
- Przed każdym wywołaniem zewnętrznego API sprawdź dzienny limit. Wynik zapisz w pamięci podręcznej.
- Logi: nigdy klucze API ani treść obrazów. Pełnych odpowiedzi dostawców nie loguj na poziomie INFO.

## Testy

- JUnit 5 i AssertJ. Logika domenowa: testy jednostkowe bez kontekstu Springa, z `Clock.fixed`.
- Kontrolery: MockMvc z dostawcami `mock`.
- Adaptery zewnętrzne: testy mapowania na zapisanych plikach JSON z `src/test/resources`. Żadnych połączeń sieciowych w testach.
- Komenda kontrolna: `./mvnw verify`.
