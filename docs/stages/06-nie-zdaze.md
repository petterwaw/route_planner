# Etap 6: „Nie zdążę"

**Cel:** aplikacja radzi sobie, gdy użytkownik jest spóźniony, wyszedł później albo ma za ciasną przesiadkę. Zamiast opcji, na które już nie zdąży, pokazuje to, co realnie da się jeszcze złapać, i mówi, o ile się spóźni.

**Zależy od:** 4. **Klucze:** niepotrzebne (działa na `mock`; z kluczem Google także na prawdziwych danych).

## Przeczytaj najpierw

- `docs/domain-rules.md`: sekcja 6, sekcja 8 (kandydaci B) oraz przypadki T2, T4, T5, T6
- `docs/architecture.md`: sekcja 4
- `docs/api.md`: Plan dnia (pola `earliestDeparture`, `leaveEarlyMin`, `source`) i Przeliczenie od teraz

## Zakres

**Backend**

- `earliestDeparture` i `leaveEarlyMin` według sekcji 6 reguł.
- Opcje „na żywo": zapytanie `DEPART_AT(earliestDeparture)`, migawka rodzaju `LIVE`, pomijanie duplikatów opcji planowych.
- Dla trybów innych niż `TRANSIT` opcje „na żywo" liczone z czasów przejazdu z migawki planowej, bez nowych zapytań.
- Pominięcie planowego zapytania `TRANSIT`, gdy `now ≥ arriveBy`.
- Ostrzeżenie `TIGHT_TRANSFER`.
- `POST /api/days/{date}/legs/{legKey}/replan` według `docs/api.md`, z ograniczeniem do jednego nowego zapytania na 30 s dla odcinka.

**Frontend**

- Przycisk „Przelicz od teraz" na odcinku aktywnym (tylko dla dzisiejszej daty).
- Grupy opcji z nagłówkami: „Zdążysz", „Spóźnisz się", „Już odjechały" (ostatnia zwinięta).
- Teksty: „Spóźnisz się ok. N min", „Wyjście N min przed końcem zajęć", znacznik „na żywo" przy opcjach ze źródłem `LIVE`.
- Gdy upływ czasu sprawi, że żadna opcja nie jest już możliwa do złapania, ekran sam jeden raz pobiera plan dnia ponownie. Kolejne przeliczenia tylko na żądanie.

## Poza zakresem

Lokalizacja z urządzenia, powiadomienia, zmiany w dostawcach, zapis przejazdu.

## Kryteria akceptacji

- [ ] Testy jednostkowe T2, T4, T5 i T6 ze stałym zegarem i sztucznym `RouteProvider`; nazwy testów zawierają numery przypadków.
- [ ] Test: opcja wymagająca wyjścia przed końcem poprzednich zajęć nie jest `MISSED` i ma poprawne `leaveEarlyMin`.
- [ ] Test: przy `now ≥ arriveBy` dostawca nie dostaje planowego zapytania `TRANSIT` (sprawdzona liczba i rodzaj wywołań).
- [ ] Test: opcje „na żywo" dla `DRIVE`, `BICYCLE` i `WALK` nie powodują nowych wywołań dostawcy.
- [ ] Test: dwa wywołania `replan` w ciągu 30 s dają jedno wywołanie dostawcy; po 31 s drugie.
- [ ] Test: `replan` dla innej daty niż dziś zwraca 409 `NOT_TODAY`, a dla nieznanego odcinka 404 `LEG_NOT_FOUND`.
- [ ] Test: rekomendacja przy braku opcji `ON_TIME` wybiera najwcześniejsze dotarcie (przypadek R3).
- [ ] Testy Vitest dla nowej logiki klienta, jeśli powstała.
- [ ] `./mvnw verify` i kontrola frontendu przechodzą. `./scripts/smoke.sh` sprawdza `replan`.
- [ ] Ręcznie z `APP_CLOCK_FIXED`: (a) godzina tuż po ostatnim planowym wyjściu pierwszego odcinka pokazuje opcje „na żywo" z tekstem o spóźnieniu; (b) godzina po końcu ostatnich zajęć pokazuje dla powrotu do domu wyjścia od tej godziny.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run typecheck && npm run test && npm run build
./scripts/smoke.sh
```

Scenariusze ręczne opisz użytkownikowi krok po kroku (jaką wartość `APP_CLOCK_FIXED` ustawić i co ma zobaczyć).
