# Architektura

Stos, struktura, konfiguracja, pamięć podręczna, testy i decyzje. Etapy wskazują sekcje do przeczytania.

## 1. Stos

| Warstwa | Wybór | Uwagi |
|---|---|---|
| Frontend | Next.js (App Router), TypeScript, Tailwind | generowany przez `create-next-app@latest` |
| Dane w przeglądarce | TanStack Query | jedyna dodatkowa biblioteka |
| Backend | Java 21, Spring Boot 4 (Spring MVC), Maven | generowany przez start.spring.io |
| Baza | H2 w pliku, Flyway, Spring Data JPA | SQL przenośny |
| HTTP do zewnętrznych API | `RestClient` | z limitami czasu |
| Trasy | Google Routes API | za interfejsem `RouteProvider` |
| AI | API zgodne z OpenAI Chat Completions | za interfejsem `ScheduleExtractor`; domyślnie Gemini z darmowym limitem |
| Testy | JUnit 5, AssertJ, MockMvc; Vitest dla czystych funkcji frontendu | bez połączeń sieciowych |

Wersje: stan na 11.11.2026 to Spring Boot 4.1.x (wersja 4.2 zapowiedziana na listopad 2026) i Next.js 16.x. Przy starcie weź najnowsze stabilne wersje, które proponują generatory, i wpisz je tutaj po etapie 1.

## 2. Układ repo

```
.
├── CLAUDE.md
├── .claude/            # reguły i komenda /etap
├── docs/
├── scripts/            # smoke.sh, seed-demo.sh, live-check.sh
├── backend/            # Spring Boot
│   ├── .env            # sekrety, poza gitem
│   ├── .env.example
│   └── data/           # plik bazy H2, poza gitem
└── frontend/           # Next.js
```

## 3. Backend

### Pakiety

| Pakiet | Zawartość |
|---|---|
| `pl.zdaze.common` | `AppProperties`, bean `Clock`, obsługa błędów, `HealthController` |
| `pl.zdaze.settings` | ustawienia (jeden wiersz) |
| `pl.zdaze.place` | miejsca |
| `pl.zdaze.schedule` | punkty planu i wyliczanie wystąpień |
| `pl.zdaze.planning` | łańcuch dnia, planowanie odcinków, statusy, rekomendacja, migawki, `DayController` |
| `pl.zdaze.routing` | port `RouteProvider`, typy zapytań i wyników, `ApiBudget`; podpakiety `mock` i `google` |
| `pl.zdaze.co2` | współczynniki emisji i kalkulator |
| `pl.zdaze.trip` | zapisane przejazdy i CO2 dnia |
| `pl.zdaze.importer` | port `ScheduleExtractor`, walidacja szkicu; podpakiety `mock` i `openai` |

### Porty

Szkic. Nazwy i pola są wiążące, szczegóły składni dopasuj.

```java
public interface RouteProvider {
    String id();                               // "mock" albo "google"
    List<RouteResult> find(RouteQuery query);  // brak trasy = pusta lista
}

public record RouteQuery(String origin, String destination, TravelMode mode,
                         TimeConstraint time, int maxResults, boolean trafficAware) {}

public sealed interface TimeConstraint {
    record ArriveBy(Instant at) implements TimeConstraint {}
    record DepartAt(Instant at) implements TimeConstraint {}
    record Unspecified() implements TimeConstraint {}
}

// departAt i arriveAt wypełnia tylko TRANSIT (czasy od drzwi do drzwi)
public record RouteResult(TravelMode mode, Duration duration, int distanceMeters,
                          Instant departAt, Instant arriveAt, List<Segment> segments) {}

public record Segment(SegmentKind kind,        // WALK albo TRANSIT
                      VehicleKind vehicle,     // BUS, TRAM, SUBWAY, RAIL, FERRY, OTHER
                      String line, String headsign, String from, String to,
                      Instant departAt, Instant arriveAt, Integer stops,
                      int distanceMeters, Duration duration) {}

public interface ScheduleExtractor {
    String id();                               // "mock" albo "openai"
    ExtractionResult extract(byte[] image, String mimeType);
}
```

Błędy dostawców: `RouteProviderException` z powodem `ADDRESS_NOT_FOUND`, `UNAVAILABLE` albo `BUDGET_EXCEEDED`. Planer zamienia je na ostrzeżenia odcinka i nie przerywa budowy całego dnia: `ADDRESS_NOT_FOUND` i `BUDGET_EXCEEDED` pod tą samą nazwą, `UNAVAILABLE` jako `PROVIDER_ERROR`. Pusta lista dla `TRANSIT` daje ostrzeżenie `NO_TRANSIT`.

### Konfiguracja

Właściwości pod prefiksem `app`, wiązane do `AppProperties`. Wartości z `backend/.env` ładuje wpis `spring.config.import: optional:file:.env[.properties]`.

| Właściwość | Zmienna środowiskowa | Domyślnie |
|---|---|---|
| `app.zone` | | `Europe/Warsaw` |
| `app.clock.fixed` | `APP_CLOCK_FIXED` | puste |
| `app.routing.provider` | `ROUTING_PROVIDER` | `mock` |
| `app.routing.google.api-key` | `GOOGLE_MAPS_API_KEY` | puste |
| `app.routing.daily-call-limit` | `ROUTING_DAILY_CALL_LIMIT` | 150 |
| `app.routing.traffic-aware` | `ROUTING_TRAFFIC_AWARE` | `false` |
| `app.routing.traffic-window-min` | | 120 |
| `app.planning.drive-parking-buffer-min` | | 5 |
| `app.planning.walk-max-km` | | 3 |
| `app.planning.bike-max-km` | | 15 |
| `app.planning.transit-max-options` | | 3 |
| `app.planning.recommend-duration-factor` | | 1.5 |
| `app.planning.recommend-duration-extra-min` | | 10 |
| `app.cache.planned-ttl-min` | | 360 |
| `app.cache.near-ttl-min` | | 10 |
| `app.cache.near-window-min` | | 120 |
| `app.cache.live-ttl-sec` | | 120 |
| `app.cache.replan-min-interval-sec` | | 30 |
| `app.co2.grams-per-km.*` | | tabela w `docs/domain-rules.md`, sekcja 9 |
| `app.ai.provider` | `AI_PROVIDER` | `mock` |
| `app.ai.base-url` | `AI_BASE_URL` | puste |
| `app.ai.api-key` | `AI_API_KEY` | puste |
| `app.ai.model` | `AI_MODEL` | puste |
| `app.ai.response-format` | `AI_RESPONSE_FORMAT` | `json_schema` |
| `app.ai.daily-call-limit` | `AI_DAILY_CALL_LIMIT` | 20 |
| `app.ai.max-image-bytes` | | 5242880 |
| `app.ai.max-items` | | 60 |

Wybór dostawcy: jeden bean na port, tworzony warunkowo według `app.routing.provider` i `app.ai.provider`. Gdy wybrano prawdziwego dostawcę, a brakuje klucza, aplikacja ma się nie uruchomić i podać czytelny powód.

### Zegar

Jeden bean `Clock`. Domyślnie zegar systemowy w strefie `app.zone`. Gdy ustawiono `app.clock.fixed` (np. `2026-11-17T15:40:00+01:00`), zegar stoi na tej chwili. Służy to do demonstracji i testów ręcznych scenariuszy „nie zdążę".

### Błędy

Jeden `@RestControllerAdvice`. Odpowiedź to `ProblemDetail` (`application/problem+json`) z dodatkowym polem `code`. Lista kodów jest w `docs/api.md`.

## 4. Pamięć podręczna i limity

Zewnętrzne zapytania kosztują, więc obowiązują dwie bariery.

**Migawki odcinków** (tabela `leg_snapshot`):

- Migawka przechowuje surowe wyniki dostawcy i punkt odniesienia CO2. Statusy liczymy przy każdym odczycie.
- Rodzaj `PLANNED`: wynik zapytań planowych. Jest ważna, gdy zgadza się `input_hash` i nie minął czas życia: 360 min, a gdy do czasu odniesienia odcinka (`arriveBy` albo `departAt`) zostało mniej niż 120 min, to 10 min.
- Rodzaj `LIVE`: wynik zapytania „na żywo". Jest ważna 120 s.
- `input_hash` to skrót SHA-256 z: identyfikatora dostawcy, adresu początku i końca, trybu planowania i jego czasu, flagi korków oraz progów z konfiguracji.
- Przeliczenie na żądanie pomija migawkę `LIVE`, ale nie częściej niż raz na 30 s dla odcinka.

**Dzienny limit** (tabela `api_usage`, klasa `ApiBudget`):

- Przed każdym zewnętrznym wywołaniem zwiększ licznik dla (dzisiejsza data, dostawca). Po przekroczeniu limitu nie wołaj API.
- Trasy: odcinek dostaje ostrzeżenie `BUDGET_EXCEEDED` i opcje z ostatniej migawki, jeśli istnieje.
- AI: odpowiedź HTTP 429 z kodem `AI_LIMIT`.
- Dostawcy `mock` nie zużywają limitu.

Domyślny limit tras (150 dziennie) daje najwyżej 4650 zapytań w miesiącu, czyli poniżej darmowych progów Google opisanych w `docs/integrations/google-routes.md`.

## 5. Frontend

```
frontend/src/
├── app/
│   ├── layout.tsx                # dostawca TanStack Query, dolna nawigacja (Today, Settings)
│   ├── page.tsx                  # Today
│   ├── settings/page.tsx         # Settings: Home, Places, Schedule, Data sources
│   └── settings/import/page.tsx  # Import ze zrzutu ekranu
├── components/
│   ├── ui/                       # Button, Card, Chip, Field, Spinner, EmptyState, ErrorState
│   ├── day/                      # DayHeader, DayCo2, LegCard, OptionRow, SegmentList, Countdown
│   ├── settings/                 # HomeForm, PlaceList, ProviderInfo
│   ├── plan/                     # EventForm, EventList, PlaceSelect
│   └── import/                   # UploadStep, ReviewTable
└── lib/
    ├── api/                  # client.ts, types.ts, queries.ts
    └── time.ts               # czas serwera, formatowanie, odliczanie
```

- Przekierowanie `/api/:path*` do `BACKEND_URL` (domyślnie `http://localhost:8080`) w `next.config.ts`.
- Czas: po każdej odpowiedzi z polem `now` zapisz `offset = now − Date.now()`. Odliczanie liczy się z `Date.now() + offset` i odświeża co 30 s bez pobierania danych.
- Odświeżanie danych dnia: przy powrocie do karty przeglądarki i co 5 min.

## 6. Testy i kontrola

| Co | Jak |
|---|---|
| Reguły domenowe | testy jednostkowe z przypadków w `docs/domain-rules.md`, `Clock.fixed`, sztuczny `RouteProvider` |
| Kontrolery | MockMvc, dostawcy `mock` |
| Adapter Google, adapter AI | mapowanie na zapisanych odpowiedziach JSON |
| Frontend | lint, sprawdzenie typów, build; Vitest dla `lib/time.ts` |
| Całość | `scripts/smoke.sh` na działającym backendzie w trybie `mock` |

`scripts/smoke.sh` rośnie z każdym etapem: dopisuj do niego sprawdzenie nowych punktów API. Ma kończyć się kodem różnym od zera przy pierwszym błędzie.

## 7. Decyzje

| Decyzja | Powód |
|---|---|
| Brak logowania, jeden użytkownik | MVP; mniej kodu i ryzyka |
| H2 w pliku zamiast serwera bazy | zero instalacji; SQL przenośny, więc zmiana na Postgresa to konfiguracja |
| Dostawcy `mock` jako domyślni | praca i testy bez kluczy i kosztów |
| Logika w backendzie, frontend cienki | jedna implementacja reguł, łatwe testy |
| Adapter AI zgodny z OpenAI | zmiana dostawcy (Gemini, Ollama, inny) to trzy zmienne środowiskowe |
| Bez Spring AI | wystarcza jedno wywołanie HTTP; mniej zależności i ryzyka niezgodności wersji |
| Statusy liczone przy odczycie | zależą od aktualnej godziny |
| Migawki odcinków w bazie | restart backendu nie powoduje ponownych płatnych zapytań |
| Punkt odniesienia CO2 to auto z jedną osobą | proste i zrozumiałe |
| Adresy jako tekst, bez geokodowania | Google Routes przyjmuje adres; mniej API i kosztów |
| Czas auta domyślnie bez korków | zapytania zostają w tańszej kategorii cenowej Google |
