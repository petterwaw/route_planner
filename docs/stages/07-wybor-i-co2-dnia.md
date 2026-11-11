# Etap 7: Wybór przejazdu i CO2 dnia

**Cel:** użytkownik klika „I'm taking this", a aplikacja zapisuje wybór i pokazuje na górze ekranu Today, ile CO2 zaoszczędził w tym dniu.

**Zależy od:** 4. **Klucze:** niepotrzebne.

## Przeczytaj najpierw

- `docs/domain-rules.md`: sekcje 7, 10 i 11 oraz przypadki T11 i D1–D5
- `docs/data-model.md`: `trip_log`
- `docs/api.md`: Plan dnia (pole `savedCo2Grams`), Zapis przejazdu
- `docs/product.md`: ekran Today (linia z CO2 dnia, odcinek zapisany)

## Zakres

**Backend**

- Migracja z tabelą `trip_log`.
- Pakiet `trip`: `PUT` i `DELETE` dla `/api/days/{date}/legs/{legKey}/log` według `docs/api.md`. Opcja jest wyszukiwana w migawkach odcinka.
- Stan `DONE` w planie dnia i pole `log` w odcinku.
- Pole `savedCo2Grams` planu dnia liczone z zapisów według sekcji 11 reguł.

**Frontend**

- Przycisk „I'm taking this" przy każdej opcji odcinka aktywnego i odcinków nadchodzących. Dla odcinków minionych akcja „Mark how I travelled".
- Odcinek zapisany: „Chosen: …" z oszczędnością CO2 i przyciskiem „Undo".
- Linia „CO2 saved: …" pod nagłówkiem ekranu Today, według `docs/product.md`.
- Po zapisie i cofnięciu pobierz plan dnia ponownie.

**Skrypty**

- `scripts/smoke.sh`: zapis opcji, sprawdzenie stanu `DONE` i `savedCo2Grams` większego od zera, cofnięcie i powrót do zera.

## Poza zakresem

Statystyki zbiorcze (suma, tydzień, wykresy), seria zielonych dni, ranking, eksport danych, edycja zapisanych liczb.

## Kryteria akceptacji

- [ ] Testy D1–D5 dla CO2 dnia; nazwy testów zawierają numery przypadków.
- [ ] Test T11: zapis zmienia stan odcinka na `DONE`, a kolejny odcinek staje się `ACTIVE`.
- [ ] Test: zapis przechowuje liczby z chwili zapisu. Zmiana współczynników w konfiguracji nie zmienia istniejących zapisów.
- [ ] Test: ponowny zapis dla tego samego odcinka nadpisuje poprzedni (jeden wiersz).
- [ ] Test: nieznana opcja zwraca 409 `OPTION_NOT_FOUND`; nieznany odcinek 404 `LEG_NOT_FOUND`.
- [ ] Test: `savedCo2Grams` nigdy nie jest ujemne; wybór auta daje 0.
- [ ] `./mvnw verify`, kontrola frontendu i `./scripts/smoke.sh` przechodzą.
- [ ] Ręcznie: wybór opcji komunikacją zwiększa „CO2 saved" na górze ekranu Today, „Undo" go zmniejsza, a wybór auta nie zmienia oszczędności.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run format:check && npm run typecheck && npm run test && npm run build
./scripts/smoke.sh
```
