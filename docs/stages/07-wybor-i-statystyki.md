# Etap 7: Wybór przejazdu i statystyki

**Cel:** użytkownik klika „Jadę tym", a aplikacja zapisuje wybór i pokazuje zaoszczędzone CO2 oraz serię zielonych dni.

**Zależy od:** 4. **Klucze:** niepotrzebne.

## Przeczytaj najpierw

- `docs/domain-rules.md`: sekcje 7, 10 i 11 oraz przypadki T11 i S1–S5
- `docs/data-model.md`: `trip_log`
- `docs/api.md`: Zapis przejazdu, Statystyki
- `docs/product.md`: ekran Statystyki i opis odcinka zapisanego

## Zakres

**Backend**

- Migracja z tabelą `trip_log`.
- Pakiet `trip`: `PUT` i `DELETE` dla `/api/days/{date}/legs/{legKey}/log` według `docs/api.md`. Opcja jest wyszukiwana w migawkach odcinka.
- Stan `DONE` w planie dnia i pole `log` w odcinku.
- `GET /api/stats` według `docs/api.md` i sekcji 11 reguł.

**Frontend**

- Przycisk „Jadę tym" przy każdej opcji odcinka aktywnego i odcinków nadchodzących. Dla odcinków minionych akcja „Zaznacz, czym jechałem".
- Odcinek zapisany: „Wybrano: …" z oszczędnością CO2 i przyciskiem „Cofnij".
- Ekran Statystyki według `docs/product.md`: kafelki, słupki z 7 dni (zwykłe elementy HTML i CSS, bez biblioteki wykresów), nota o szacunku.
- Po zapisie i cofnięciu odśwież plan dnia i statystyki.

**Skrypty**

- `scripts/smoke.sh`: zapis opcji, sprawdzenie stanu `DONE`, statystyki większe od zera, cofnięcie.

## Poza zakresem

Zwierzak (etap 10), ranking, eksport danych, edycja zapisanych liczb.

## Kryteria akceptacji

- [ ] Testy S1–S5 dla serii, ze stałym zegarem; nazwy testów zawierają numery przypadków.
- [ ] Test T11: zapis zmienia stan odcinka na `DONE`, a kolejny odcinek staje się `ACTIVE`.
- [ ] Test: zapis przechowuje liczby z chwili zapisu. Zmiana współczynników w konfiguracji nie zmienia istniejących zapisów.
- [ ] Test: ponowny zapis dla tego samego odcinka nadpisuje poprzedni (jeden wiersz).
- [ ] Test: nieznana opcja zwraca 409 `OPTION_NOT_FOUND`; nieznany odcinek 404 `LEG_NOT_FOUND`.
- [ ] Test: `savedCo2Grams` nigdy nie jest ujemne; wybór auta daje 0.
- [ ] Test: `weekSavedGrams` liczy tydzień od poniedziałku; `last7Days` ma 7 pozycji i kończy się dzisiejszą datą; `carKmAvoided` ma jedno miejsce po przecinku.
- [ ] `./mvnw verify`, kontrola frontendu i `./scripts/smoke.sh` przechodzą.
- [ ] Ręcznie: wybór opcji komunikacją zwiększa wynik na ekranie Statystyki, „Cofnij" go zmniejsza, a wybór auta nie zmienia oszczędności.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run typecheck && npm run test && npm run build
./scripts/smoke.sh
```
