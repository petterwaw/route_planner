# Etap 5: Trasy z Google

**Cel:** po przełączeniu `ROUTING_PROVIDER=google` plan dnia pokazuje prawdziwe połączenia i czasy, a liczba zapytań jest pod kontrolą.

**Zależy od:** 3. **Klucze:** Google Routes API (`docs/setup.md`, sekcja 3).

## Przeczytaj najpierw

- `docs/integrations/google-routes.md`: całość
- `docs/architecture.md`: sekcja 3 (Porty, Konfiguracja) i sekcja 4
- `docs/data-model.md`: `api_usage`

## Zanim zaczniesz

Zapytaj użytkownika:

1. Czy klucz jest w `backend/.env`? Jeśli nie, zbuduj adapter według dokumentu, zrób testy na ręcznie przygotowanych odpowiedziach i zapisz w `docs/stages/STATUS.md`, że weryfikacja na żywo czeka.
2. O dwa prawdziwe adresy z jego trasy (do nagrania odpowiedzi).
3. O zgodę na najwyżej 8 zapytań do API w celu nagrania odpowiedzi.

## Zakres

- Migracja z tabelą `api_usage`. Klasa `ApiBudget` według architektury, sekcja 4.
- `routing.google`: budowa żądań dla każdego trybu, wywołanie przez `RestClient`, mapowanie odpowiedzi na `RouteResult`, mapowanie błędów.
- Wybór dostawcy przez `app.routing.provider`. Brak klucza przy dostawcy `google` zatrzymuje start aplikacji z czytelnym komunikatem.
- Czas auta z korkami za flagą `app.routing.traffic-aware`, według dokumentu integracji, punkt 3.
- Nagrane odpowiedzi w `backend/src/test/resources/google/` i testy mapowania na nich.
- `scripts/live-check.sh`: przy działającym backendzie z dostawcą `google` pobiera plan dzisiejszego dnia i wypisuje dla każdego odcinka opcje w czytelnej postaci (tryb, godziny, opis). Służy użytkownikowi do porównania z Mapami Google.
- Ostrzeżenie `BUDGET_EXCEEDED` i zwracanie opcji z ostatniej migawki, gdy limit jest wyczerpany.

## Poza zakresem

Tryb „na żywo" i `replan` (etap 6), opóźnienia pociągów, geokodowanie, autouzupełnianie adresów.

## Kryteria akceptacji

- [ ] Testy mapowania dla każdego nagranego pliku: liczba opcji, godziny wyjścia i dotarcia, segmenty, typy pojazdów, dystanse.
- [ ] Test: kroki piesze przed pierwszym przejazdem i po ostatnim są wliczone w `departAt` i `arriveAt`.
- [ ] Test: trasa bez przejazdu komunikacją jest odrzucana, a odcinek dostaje `NO_TRANSIT`.
- [ ] Test budowy żądań: `routingPreference` jest wysyłane tylko dla `DRIVE`; `arrivalTime` tylko dla `TRANSIT`; maski pól zgodne z dokumentem.
- [ ] Test `ApiBudget`: licznik rośnie o jeden na wywołanie; po przekroczeniu limitu dostawca nie jest wołany, a odcinek ma `BUDGET_EXCEEDED`; dostawca `mock` nie zużywa limitu.
- [ ] Żaden test nie łączy się z siecią (testy przechodzą bez internetu).
- [ ] W nagranych plikach i w logach nie ma klucza: `grep -r` po fragmencie klucza nic nie znajduje.
- [ ] Z kluczem: `./scripts/live-check.sh` pokazuje prawdziwe połączenia, a użytkownik potwierdza zgodność jednego odcinka z Mapami Google (z dokładnością do kilku minut).
- [ ] Z kluczem: drugie wywołanie planu dnia w ciągu minuty nie zwiększa licznika w `api_usage`.
- [ ] `./mvnw verify` przechodzi. `./scripts/smoke.sh` przechodzi w trybie `mock`.

## Weryfikacja

```
cd backend && ./mvnw verify
./scripts/smoke.sh
./scripts/live-check.sh
```

Po etapie przypomnij użytkownikowi, że `ROUTING_PROVIDER=mock` wyłącza płatne zapytania na czas dalszych prac.
