# Integracja: Google Routes API

Czytaj tylko przy pracy nad pakietem `routing.google`. Stan wiedzy: listopad 2026, na podstawie oficjalnej dokumentacji. Przed implementacją otwórz źródła z końca pliku i potwierdź szczegóły, bo API się zmienia.

## 1. Wywołanie

- `POST https://routes.googleapis.com/directions/v2:computeRoutes`
- Nagłówki: `Content-Type: application/json`, `X-Goog-Api-Key`, `X-Goog-FieldMask`.
- Maska pól jest wymagana. Podawaj tylko pola, których używasz. Nie używaj `*`.
- Jedno zapytanie to jeden tryb podróży.

Przykład dla komunikacji z godziną przyjazdu:

```json
{
  "origin": { "address": "ul. Przykładowa 1, Gdańsk" },
  "destination": { "address": "ul. Uczelniana 2, Gdańsk" },
  "travelMode": "TRANSIT",
  "arrivalTime": "2026-11-17T07:10:00Z",
  "computeAlternativeRoutes": true,
  "languageCode": "pl",
  "regionCode": "pl",
  "units": "METRIC"
}
```

Czasy w żądaniu podawaj w UTC (RFC 3339 z literą `Z`).

## 2. Zasady, na których łatwo się wyłożyć

- `arrivalTime` działa tylko dla `TRANSIT`. W innych trybach jest ignorowane.
- Nie wolno podać jednocześnie `arrivalTime` i `departureTime`.
- `departureTime` w przeszłości jest dozwolone tylko dla `TRANSIT`. W innych trybach pomiń pole albo podaj chwilę przyszłą.
- Połączenia komunikacji są dostępne od 7 dni wstecz do 100 dni naprzód.
- `routingPreference` wolno ustawić tylko dla `DRIVE`. Wysłane z `TRANSIT`, `WALK` albo `BICYCLE` powoduje błąd zapytania.
- `transitPreferences` wolno ustawić tylko dla `TRANSIT`. Nie używamy.
- Nie używaj trybu `TWO_WHEELER` (najdroższa kategoria).
- Czasy trwania w odpowiedzi to napisy z sekundami, np. `"2843s"`.
- Brak trasy może dać odpowiedź bez pola `routes`. Traktuj to jako pustą listę.

## 3. Zapytania na jeden odcinek

| Tryb | Ograniczenie czasu | Dodatkowe pola | Maska pól |
|---|---|---|---|
| `DRIVE` (zawsze) | brak | `routingPreference: TRAFFIC_UNAWARE` | `routes.duration,routes.distanceMeters` |
| `DRIVE` z korkami (opcjonalnie) | `departureTime` | `routingPreference: TRAFFIC_AWARE` | `routes.duration,routes.staticDuration,routes.distanceMeters` |
| `TRANSIT` | `arrivalTime` albo `departureTime` | `computeAlternativeRoutes: true` | patrz niżej |
| `BICYCLE`, `WALK` | brak | brak | `routes.duration,routes.distanceMeters` |

Maska dla `TRANSIT`:

```
routes.duration,routes.distanceMeters,routes.legs.steps.travelMode,routes.legs.steps.distanceMeters,routes.legs.steps.staticDuration,routes.legs.steps.transitDetails
```

Zapytanie `DRIVE` z korkami wykonuj tylko wtedy, gdy `app.routing.traffic-aware` jest włączone, data to dziś i do wyjścia zostało mniej niż `app.routing.traffic-window-min`. Jako `departureTime` podaj późniejszą z wartości: teraz plus 1 minuta oraz planowe wyjście wyliczone z czasu bez korków.

## 4. Odpowiedź dla komunikacji

Każdy element `routes[].legs[0].steps[]` ma `travelMode` (`WALK` albo `TRANSIT`), `distanceMeters`, `staticDuration`, a krok przejazdu ma `transitDetails`:

| Pole | Znaczenie |
|---|---|
| `stopDetails.departureStop.name`, `stopDetails.departureTime` | przystanek i godzina odjazdu |
| `stopDetails.arrivalStop.name`, `stopDetails.arrivalTime` | przystanek i godzina przyjazdu |
| `headsign` | kierunek |
| `stopCount` | liczba przystanków |
| `transitLine.nameShort`, `transitLine.name` | numer albo nazwa linii |
| `transitLine.vehicle.type` | typ pojazdu |
| `tripShortText` | numer kursu albo pociągu, jeśli jest |

Dokładny kształt potwierdź na nagranych odpowiedziach z punktu 8. To one są źródłem prawdy dla mapowania.

## 5. Mapowanie na model domenowy

Jedna trasa z `routes[]` to jeden `RouteResult`.

- Kolejne kroki `WALK` sklej w jeden segment pieszy. Każdy krok z `transitDetails` to osobny segment przejazdu.
- `departAt` = godzina odjazdu pierwszego przejazdu minus suma `staticDuration` kroków przed nim.
- `arriveAt` = godzina przyjazdu ostatniego przejazdu plus suma `staticDuration` kroków po nim.
- Trasę bez żadnego kroku z `transitDetails` (samo dojście pieszo) odrzuć jako wynik komunikacji. Gdy nie zostanie żadna, odcinek dostaje ostrzeżenie `NO_TRANSIT`.
- Weź najwyżej `app.planning.transit-max-options` tras.
- Dla `DRIVE`, `BICYCLE`, `WALK` weź pierwszą trasę: `duration` i `distanceMeters`.

Typ pojazdu Google na `VehicleKind`:

| `transitLine.vehicle.type` | `VehicleKind` |
|---|---|
| `BUS`, `INTERCITY_BUS`, `TROLLEYBUS`, `SHARE_TAXI` | `BUS` |
| `TRAM`, `METRO_RAIL`, `MONORAIL`, `CABLE_CAR`, `FUNICULAR`, `GONDOLA_LIFT` | `TRAM` |
| `SUBWAY` | `SUBWAY` |
| `RAIL`, `COMMUTER_TRAIN`, `HEAVY_RAIL`, `LONG_DISTANCE_TRAIN`, `HIGH_SPEED_TRAIN` | `RAIL` |
| `FERRY` | `FERRY` |
| `OTHER` i nieznane | `OTHER` |

## 6. Błędy

- Nierozpoznany adres: zachowanie API sprawdź doświadczalnie przy nagrywaniu odpowiedzi (jedno zapytanie z bezsensownym adresem) i zmapuj na `ADDRESS_NOT_FOUND`.
- HTTP 4xx z powodu złego żądania: to błąd w kodzie. Zaloguj status i komunikat (bez klucza) i zwróć `UNAVAILABLE`.
- HTTP 429, 5xx i przekroczenie czasu: `UNAVAILABLE`. Bez automatycznych ponowień.
- Wyczerpany dzienny limit aplikacji: `BUDGET_EXCEEDED`, bez wywołania API.

## 7. Koszty i bezpieczniki

Stan na listopad 2026:

- Zapytanie bez funkcji dodatkowych należy do kategorii Essentials: 10 000 darmowych zapytań miesięcznie.
- `routingPreference` równe `TRAFFIC_AWARE` albo `TRAFFIC_AWARE_OPTIMAL` przenosi zapytanie do kategorii Pro: 5 000 darmowych zapytań miesięcznie.
- Korzystanie z API wymaga włączonych rozliczeń w projekcie Google Cloud.

Jeden odcinek to zwykle 2–4 zapytania Essentials. Dzień z trzema odcinkami to około 10 zapytań, a przeliczenie „na żywo" to jedno dodatkowe. Bezpieczniki z `docs/architecture.md` (migawki i dzienny limit 150) trzymają zużycie poniżej darmowej puli nawet przy błędzie w pętli.

Klucza nie wolno logować ani zapisywać w plikach testowych.

## 8. Nagrywanie odpowiedzi do testów

Wymaga klucza w `backend/.env`. Najpierw zapytaj użytkownika o zgodę i o dwa prawdziwe adresy z jego trasy. Wykonaj najwyżej 8 zapytań (np. przez `curl`) i zapisz odpowiedzi w `backend/src/test/resources/google/`:

- `transit_arrive_by.json`, `transit_depart_at.json`
- `drive.json`, `bicycle.json`, `walk.json`
- `no_route.json` (np. komunikacja w środku nocy albo między odległymi miejscowościami)
- `bad_address.json`

Sprawdź, że w plikach nie ma klucza. Testy mapowania czytają te pliki. Bez klucza zbuduj adapter według tego dokumentu i zapisz w `docs/stages/STATUS.md`, że weryfikacja na żywo czeka.

## 9. Źródła

- Opis żądania i odpowiedzi: https://developers.google.com/maps/documentation/routes/reference/rpc/google.maps.routing.v2
- Trasy komunikacją: https://developers.google.com/maps/documentation/routes/transit-route
- Rozliczenia: https://developers.google.com/maps/documentation/routes/usage-and-billing
- Kategorie cenowe: https://developers.google.com/maps/billing-and-pricing/sku-details
- Cennik: https://developers.google.com/maps/billing-and-pricing/pricing
