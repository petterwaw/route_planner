# Etap 3: Łańcuch dnia (backend)

**Cel:** `GET /api/days/{date}` zwraca odcinki dnia z opcjami, statusami, CO2 i rekomendacją. Trasy pochodzą z dostawcy `mock`.

**Zależy od:** 2. **Klucze:** niepotrzebne.

## Przeczytaj najpierw

- `docs/domain-rules.md`: sekcje 1–5 i 7–9 oraz przypadki T1, T3, T7–T10, C1–C5, R1 i R2
- `docs/architecture.md`: sekcja 3 (Porty) i sekcja 4
- `docs/data-model.md`: `leg_snapshot`
- `docs/api.md`: Plan dnia

## Zakres

- `schedule`: wyliczanie wystąpień dla daty (reguły, sekcja 2).
- `planning`: budowa łańcucha (sekcja 3), planowanie odcinka (sekcja 4), statusy (sekcja 5), stan odcinka (sekcja 7), rekomendacja i kolejność (sekcja 8), ostrzeżenie `OVERLAP`.
- `routing`: port i typy z architektury oraz `MockRouteProvider` opisany niżej.
- `co2`: współczynniki z konfiguracji i kalkulator (sekcja 9).
- Migracja z tabelą `leg_snapshot`. Migawki rodzaju `PLANNED` z regułami ważności z architektury.
- `DayController`: `GET /api/days/{date}` według `docs/api.md`.
- `scripts/smoke.sh`: dzień z dwoma punktami w różnych miejscach zwraca trzy odcinki, a każdy ma co najmniej jedną opcję.

Na tym etapie wszystkie opcje mają `source` równe `PLANNED` i `leaveEarlyMin` równe 0, pole `log` jest puste, a stan `DONE` nie występuje. Z sekcji 8 reguł zaimplementuj punkty 1 i 3 oraz kolejność; punkt 2 (kandydaci B) dochodzi w etapie 6 razem z zapytaniami „na żywo", ostrzeżeniem `TIGHT_TRANSFER` i `replan`. Zapis przejazdu dochodzi w etapie 7.

## Dostawca `mock`

Wyniki mają być deterministyczne i wyglądać realistycznie.

- Dystans auta w km: `2 + (abs(hash(origin + "|" + destination)) % 380) / 10`.
- `DRIVE`: `ceil(km × 1,6 + 4)` minut.
- `BICYCLE`: `ceil(km × 4)` minut. `WALK`: `ceil(km × 12)` minut. Dystans jak dla auta.
- `TRANSIT`:
  - pojazd `RAIL`, gdy dystans przekracza 12 km; w przeciwnym razie `TRAM` dla parzystego skrótu i `BUS` dla nieparzystego,
  - przejazd trwa `ceil(km × 1,8 + 3)` minut, a jego dystans to dystans auta minus 0,8 km (nie mniej niż 0,5 km),
  - dojście 6 min i 500 m przed przejazdem, 4 min i 300 m po nim,
  - pojazd odjeżdża co 20 minut, w minutach :07, :27 i :47,
  - `ArriveBy(t)`: trzy ostatnie kursy, po których dotarcie wypada nie później niż t,
  - `DepartAt(t)`: trzy najbliższe kursy, na które da się dojść, wychodząc o t.
- Nazwy: linia `R` dla kolei, numer 1–9 dla tramwaju, 100–199 dla autobusu (ze skrótu). Przystanki „Przystanek A" i „Przystanek B".
- Adres zawierający `nieznany` powoduje błąd `ADDRESS_NOT_FOUND`. Adres zawierający `bez-komunikacji` daje pustą listę dla `TRANSIT`.

## Poza zakresem

Frontend, dostawca Google, tryb „na żywo", zapis przejazdu (do etapu 7 pole `savedCo2Grams` planu dnia zawsze wynosi 0).

## Kryteria akceptacji

- [ ] Każdy wymieniony przypadek z `docs/domain-rules.md` ma test jednostkowy, a nazwa testu zawiera numer przypadku (np. `t1_onTimeWithSlack`).
- [ ] Testy planera używają sztucznego `RouteProvider`, który zwraca dokładnie opcje z tabel. Nie używają `MockRouteProvider`.
- [ ] `MockRouteProvider` ma własne testy: wynik jest powtarzalny, `ArriveBy` nie zwraca dotarcia po t, `DepartAt` nie zwraca wyjścia przed t.
- [ ] Test: dwa kolejne wywołania `GET /api/days/{date}` wołają dostawcę tylko przy pierwszym. Zmiana adresu miejsca unieważnia migawkę.
- [ ] Test: data przeszła nie wywołuje dostawcy.
- [ ] Test MockMvc: odpowiedź ma wszystkie pola odcinka i opcji z `docs/api.md`.
- [ ] Test: błąd `ADDRESS_NOT_FOUND` daje ostrzeżenie na odcinku, a pozostałe odcinki dnia są policzone.
- [ ] Test: data poza zakresem zwraca 400 `DATE_OUT_OF_RANGE`.
- [ ] `./mvnw verify` i `./scripts/smoke.sh` przechodzą.

## Weryfikacja

```
cd backend && ./mvnw verify
./scripts/smoke.sh
```

Pokaż użytkownikowi odpowiedź `GET /api/days/{dzisiejsza data}` dla przykładowego planu.
