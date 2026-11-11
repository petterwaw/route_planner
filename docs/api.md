# Kontrakt API

REST między frontendem a backendem. Typy w `frontend/src/lib/api/types.ts` mają odwzorowywać ten plik. Zmiana kontraktu wymaga zmiany tego pliku w tym samym commicie.

## Zasady ogólne

- Prefiks `/api`. Treść żądań i odpowiedzi w JSON, poza importem obrazu.
- Daty: `YYYY-MM-DD`. Godziny planu: `HH:mm`. Chwile: ISO 8601 z przesunięciem strefy `Europe/Warsaw`, np. `2026-11-17T07:21:00+01:00`.
- Typy wyliczeniowe: napisy wielkimi literami.
- Pole `now` to czas serwera. Frontend liczy z niego przesunięcie zegara.
- Błędy: `application/problem+json` z polem `code` (sekcja „Błędy").

## Health

`GET /api/health`

```json
{ "status": "ok", "now": "2026-11-17T06:00:00+01:00", "routingProvider": "mock", "aiProvider": "mock" }
```

## Ustawienia

`GET /api/settings`, `PUT /api/settings`

```json
{ "homeLabel": "Home", "homeAddress": "ul. Przykładowa 1, Gdańsk", "arrivalBufferMin": 5, "setupComplete": true }
```

- `PUT` przyjmuje `homeLabel` (1–60 znaków), `homeAddress` (3–255 znaków albo `null`), `arrivalBufferMin` (0–60).
- `setupComplete` jest tylko do odczytu: prawda, gdy adres domu nie jest pusty.

## Miejsca

| Metoda i ścieżka | Wynik |
|---|---|
| `GET /api/places` | 200, lista posortowana po nazwie |
| `POST /api/places` | 201, utworzone miejsce |
| `PUT /api/places/{id}` | 200 |
| `DELETE /api/places/{id}` | 204; 409 `PLACE_IN_USE`, gdy używa go punkt planu |

```json
{ "id": 1, "name": "Wydział", "address": "ul. Uczelniana 2, Gdańsk", "inUse": true }
```

Wejście: `name` (1–80 znaków, unikalna; duplikat daje 409 `PLACE_NAME_TAKEN`), `address` (3–255 znaków).

## Punkty planu

| Metoda i ścieżka | Wynik |
|---|---|
| `GET /api/events` | 200, lista: najpierw `WEEKLY` według dnia tygodnia i godziny, potem `ONCE` według daty |
| `POST /api/events` | 201 |
| `PUT /api/events/{id}` | 200 |
| `DELETE /api/events/{id}` | 204 |
| `POST /api/events/bulk` | 201, `{ "created": [ ... ] }`; wszystko albo nic |

```json
{
  "id": 12,
  "title": "Analiza matematyczna",
  "placeId": 1,
  "placeName": "Wydział",
  "recurrence": "WEEKLY",
  "dayOfWeek": "MONDAY",
  "date": null,
  "startTime": "08:15",
  "endTime": "09:45",
  "validFrom": null,
  "validUntil": null,
  "source": "MANUAL"
}
```

Wejście (`POST`, `PUT`, elementy `bulk`): wszystkie pola poza `id`, `placeName` i `source`.

Walidacja:

- `title` 1–120 znaków, `placeId` musi istnieć,
- `endTime` po `startTime`,
- `WEEKLY` wymaga `dayOfWeek` i pustego `date`; `ONCE` wymaga `date` i pustego `dayOfWeek`,
- `validFrom` nie później niż `validUntil`; oba tylko dla `WEEKLY`.

`POST /api/events/bulk` przyjmuje `{ "events": [ ... ] }`, najwyżej 60 elementów, i ustawia `source` na `IMPORT`. Przy błędzie zwraca 400 `VALIDATION_ERROR` z listą `errors`, w której każdy wpis ma `index`, `field` i `message`.

## Plan dnia

`GET /api/days/{date}`

Zakres dat: od 30 dni wstecz do 30 dni naprzód, inaczej 400 `DATE_OUT_OF_RANGE`. Reguły wyliczania: `docs/domain-rules.md`.

```json
{
  "date": "2026-11-17",
  "now": "2026-11-17T06:00:00+01:00",
  "setupComplete": true,
  "savedCo2Grams": 0,
  "events": [
    {
      "id": 1,
      "title": "Analiza matematyczna",
      "placeName": "Wydział",
      "start": "2026-11-17T08:15:00+01:00",
      "end": "2026-11-17T09:45:00+01:00"
    }
  ],
  "legs": [
    {
      "legKey": "to-event-1",
      "kind": "TO_EVENT",
      "eventId": 1,
      "origin": { "label": "Home", "address": "ul. Przykładowa 1, Gdańsk" },
      "destination": { "label": "Wydział", "address": "ul. Uczelniana 2, Gdańsk" },
      "deadline": "2026-11-17T08:15:00+01:00",
      "arriveBy": "2026-11-17T08:10:00+01:00",
      "plannedDepartAt": null,
      "previousEnd": null,
      "earliestDeparture": "2026-11-17T06:00:00+01:00",
      "state": "ACTIVE",
      "warnings": [],
      "baseline": { "distanceMeters": 24000, "co2Grams": 4080 },
      "options": [
        {
          "id": "TRANSIT-0721-0803",
          "mode": "TRANSIT",
          "source": "PLANNED",
          "departAt": "2026-11-17T07:21:00+01:00",
          "arriveAt": "2026-11-17T08:03:00+01:00",
          "durationMin": 42,
          "distanceMeters": 22800,
          "status": "ON_TIME",
          "slackMin": 12,
          "lateByMin": 0,
          "leaveEarlyMin": 0,
          "recommended": true,
          "co2Grams": 770,
          "savedCo2Grams": 3310,
          "summary": "Train R · 07:27 Gdańsk Główny → Pszczółki",
          "segments": [
            { "kind": "WALK", "durationMin": 6, "distanceMeters": 500 },
            {
              "kind": "TRANSIT",
              "vehicle": "RAIL",
              "line": "R",
              "headsign": "Tczew",
              "from": "Gdańsk Główny",
              "to": "Pszczółki",
              "departAt": "2026-11-17T07:27:00+01:00",
              "arriveAt": "2026-11-17T07:59:00+01:00",
              "stops": 6,
              "durationMin": 32,
              "distanceMeters": 22000
            },
            { "kind": "WALK", "durationMin": 4, "distanceMeters": 300 }
          ]
        }
      ],
      "log": null
    }
  ]
}
```

`savedCo2Grams` to CO2 zaoszczędzone w tym dniu według sekcji 11 reguł; 0, gdy nie ma zapisów. Do etapu 7, który dodaje zapisy przejazdów, zawsze 0.

Pola odcinka:

| Pole | Opis |
|---|---|
| `kind` | `TO_EVENT` albo `HOME` |
| `deadline`, `arriveBy`, `eventId` | tylko `TO_EVENT`; dla `HOME` puste |
| `plannedDepartAt` | tylko `HOME`: koniec ostatnich zajęć |
| `previousEnd` | koniec poprzednich zajęć albo puste |
| `earliestDeparture` | według sekcji 4 reguł; puste, gdy nie ma ograniczenia |
| `state` | `UPCOMING`, `ACTIVE`, `PAST`, `DONE` |
| `warnings` | `ADDRESS_NOT_FOUND`, `NO_TRANSIT`, `PROVIDER_ERROR`, `BUDGET_EXCEEDED`, `OVERLAP`, `TIGHT_TRANSFER` |
| `baseline` | dystans auta i emisja odniesienia; puste, gdy nie udało się ich ustalić |
| `log` | zapisany przejazd albo puste |

Pola opcji:

| Pole | Opis |
|---|---|
| `id` | `{TRYB}-{HHmm wyjścia}-{HHmm dotarcia}`; przy duplikacie w odcinku dopisz `-2` |
| `source` | `PLANNED` albo `LIVE` |
| `status` | `ON_TIME`, `LATE`, `MISSED` |
| `slackMin` | zapas do terminu; puste dla odcinka `HOME` |
| `segments` | tylko `TRANSIT`; dla innych trybów pusta lista |
| `summary` | krótki opis po angielsku |

Wzorce `summary`: „Train R · 07:27 Gdańsk Główny → Pszczółki" (pierwszy przejazd; przy kolejnych dopisz „+ 1 transfer" albo „+ 2 transfers"), „Car · 24 km", „Bike · 5.2 km", „Walk · 1.4 km". Nazwy pojazdów: `RAIL` Train, `BUS` Bus, `TRAM` Tram, `SUBWAY` Metro, `FERRY` Ferry, `OTHER` Transit.

Opcje są już posortowane według sekcji 8 reguł. Frontend zachowuje kolejność.

### Przeliczenie od teraz (etap 6)

`POST /api/days/{date}/legs/{legKey}/replan`

- Tylko dla dzisiejszej daty, inaczej 409 `NOT_TODAY`. Nieznany odcinek: 404 `LEG_NOT_FOUND`.
- Wymusza nowe zapytanie „na żywo". Jeśli od poprzedniego minęło mniej niż 30 s, zwraca odcinek z istniejącej migawki.
- Odpowiedź: `{ "now": "...", "leg": { ... } }` z odcinkiem w tym samym kształcie.

### Zapis przejazdu (etap 7)

`PUT /api/days/{date}/legs/{legKey}/log` z treścią `{ "optionId": "TRANSIT-0721-0803" }`

- Szuka opcji w migawkach odcinka. Brak opcji: 409 `OPTION_NOT_FOUND` („The plan has changed, please refresh.").
- Odpowiedź: `{ "now": "...", "leg": { ... } }`, odcinek ma `state` `DONE` i wypełnione `log`.

`DELETE /api/days/{date}/legs/{legKey}/log` usuwa zapis i zwraca odcinek w tym samym kształcie.

```json
{
  "mode": "TRANSIT",
  "summary": "Train R · 07:27 Gdańsk Główny → Pszczółki",
  "distanceMeters": 22800,
  "co2Grams": 770,
  "baselineCo2Grams": 4080,
  "savedCo2Grams": 3310,
  "loggedAt": "2026-11-17T07:05:00+01:00"
}
```

Po zapisie i cofnięciu frontend pobiera plan dnia ponownie, żeby odświeżyć `savedCo2Grams`.

## Import planu z obrazu (etap 8)

`POST /api/import/schedule-image`, treść `multipart/form-data` z częścią `file`.

- Dozwolone: PNG, JPEG, WebP, do 5 MB.
- Niczego nie zapisuje. Zwraca szkic do przeglądu. Zapis idzie przez `POST /api/events/bulk`.

```json
{
  "items": [
    {
      "title": "Analiza matematyczna",
      "dayOfWeek": "MONDAY",
      "startTime": "08:15",
      "endTime": "09:45",
      "locationText": "s. 204, bud. A",
      "note": "wykład"
    }
  ],
  "warnings": ["Could not read two fields in the Friday column."],
  "provider": "openai",
  "model": "nazwa-modelu"
}
```

## Błędy

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "This place is used by 3 events.",
  "code": "PLACE_IN_USE"
}
```

Pole `detail` jest po angielsku i nadaje się do pokazania użytkownikowi. Błąd walidacji ma dodatkowo `errors`: listę wpisów z `field` i `message`; `message` też jest po angielsku.

| Kod | Status | Kiedy |
|---|---|---|
| `VALIDATION_ERROR` | 400 | niepoprawne dane wejściowe |
| `DATE_OUT_OF_RANGE` | 400 | data planu dnia poza zakresem |
| `NOT_FOUND` | 404 | nie ma zasobu o tym identyfikatorze |
| `LEG_NOT_FOUND` | 404 | nie ma takiego odcinka w tym dniu |
| `PLACE_NAME_TAKEN` | 409 | miejsce o tej nazwie już istnieje |
| `PLACE_IN_USE` | 409 | miejsce jest używane przez punkt planu |
| `NOT_TODAY` | 409 | przeliczenie od teraz dla innej daty niż dziś |
| `OPTION_NOT_FOUND` | 409 | wskazanej opcji nie ma w migawce |
| `FILE_INVALID` | 400 | brak pliku albo zły format |
| `FILE_TOO_LARGE` | 413 | plik większy niż 5 MB |
| `AI_NO_RESULT` | 422 | model nie zwrócił żadnego poprawnego punktu |
| `AI_LIMIT` | 429 | wyczerpany dzienny limit wywołań AI |
| `AI_PROVIDER_ERROR` | 502 | błąd albo przekroczenie czasu u dostawcy AI |
| `INTERNAL_ERROR` | 500 | pozostałe |
