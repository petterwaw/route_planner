# Etap 8: Import planu ze zrzutu ekranu (AI)

**Cel:** użytkownik wgrywa zrzut ekranu planu zajęć, dostaje szkic do poprawienia, przypisuje miejsca i zapisuje punkty jednym kliknięciem.

**Zależy od:** 2. **Klucze:** dostawca AI (`docs/setup.md`, sekcja 4). Bez klucza cały etap da się zbudować i przetestować na dostawcy `mock`.

## Przeczytaj najpierw

- `docs/integrations/ai-import.md`: całość
- `docs/api.md`: Import planu z obrazu, `POST /api/events/bulk`, Błędy
- `docs/product.md`: ekran Import
- `docs/architecture.md`: sekcja 3 (Porty, Konfiguracja) i sekcja 4 (dzienny limit)

## Zanim zaczniesz

Zapytaj użytkownika, którego dostawcy AI używa i czy klucz jest w `backend/.env`. Jeśli tabela `api_usage` jeszcze nie istnieje (etap 5 nie był robiony), utwórz ją i klasę `ApiBudget` teraz, według `docs/data-model.md` i architektury, sekcja 4.

## Zakres

**Backend**

- Port `ScheduleExtractor` i typy wyniku.
- `importer.mock`: stały szkic według dokumentu integracji, punkt 7.
- `importer.openai`: żądanie, instrukcja, schemat i obsługa `AI_RESPONSE_FORMAT` według dokumentu integracji.
- `DraftValidator` według dokumentu integracji, punkt 6.
- `POST /api/import/schedule-image`: sprawdzenie typu po pierwszych bajtach i rozmiaru, dzienny limit, kody błędów z `docs/api.md`.
- Limity przesyłania plików w konfiguracji Springa.
- Wybór dostawcy przez `app.ai.provider`. Brak adresu, klucza albo modelu przy dostawcy `openai` zatrzymuje start aplikacji z czytelnym komunikatem.

**Frontend**

- Przycisk „Importuj ze zdjęcia" na ekranie Plan.
- Ekran `/plan/import` w czterech krokach według `docs/product.md`: wybór pliku z informacją o wysyłce do zewnętrznego modelu, stan oczekiwania, tabela przeglądu, zapis.
- Tabela przeglądu: edycja pól w wierszu, usuwanie wiersza, przypisanie miejsca w wierszu, akcja „Ustaw to miejsce dla wszystkich z tą samą lokalizacją", dodanie nowego miejsca bez wychodzenia z ekranu.
- Zapis jest zablokowany, dopóki któryś wiersz nie ma miejsca albo ma błąd. Błędy z `POST /api/events/bulk` pokazuj przy właściwym wierszu (pola `index` i `field`).
- Komunikaty dla każdego kodu błędu importu.

**Skrypty**

- `scripts/smoke.sh`: w trybie `mock` wysyła mały plik PNG i sprawdza, że szkic ma elementy; wysyła plik tekstowy i oczekuje 400 `FILE_INVALID`.

## Poza zakresem

Import z tekstu, PDF i `.ics`, zapamiętywanie przypisań lokalizacji, przechowywanie obrazów, kolejkowanie zadań, przesyłanie strumieniowe odpowiedzi modelu.

## Kryteria akceptacji

- [ ] Testy `DraftValidator`: odpowiedź poprawna; błędne godziny; koniec przed początkiem; dzień spoza listy; ogrodzenie markdown wokół JSON; pusta lista (422 `AI_NO_RESULT`); niepoprawny JSON (jedno ponowienie, potem `AI_PROVIDER_ERROR`); więcej niż 60 elementów.
- [ ] Test adaptera na zapisanej odpowiedzi `chat/completions`: żądanie ma obraz jako adres `data:` i właściwy `response_format` dla każdej wartości `AI_RESPONSE_FORMAT`.
- [ ] Testy kontrolera z dostawcą `mock`: poprawny PNG; plik o złym typie mimo rozszerzenia `.png` (400); plik za duży (413); wyczerpany limit (429 `AI_LIMIT`).
- [ ] Żaden test nie łączy się z siecią. Obraz nie jest zapisywany na dysk ani logowany.
- [ ] `./mvnw verify`, kontrola frontendu i `./scripts/smoke.sh` przechodzą.
- [ ] Ręcznie na `mock`: pełne przejście od wyboru pliku do punktów widocznych na ekranie Plan, w tym przypisanie jednego miejsca wielu wierszom.
- [ ] Z kluczem: użytkownik wgrywa własny zrzut ekranu. Wynik (model, liczba wierszy poprawnych na liczbę wszystkich, co model pomylił) jest zapisany w `docs/ai-log.md`.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run typecheck && npm run test && npm run build
./scripts/smoke.sh
```

Prywatne zrzuty ekranu trzymaj w katalogu `samples/`, który jest poza gitem.
