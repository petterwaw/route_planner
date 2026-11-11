# Uruchomienie i klucze

Dla człowieka i dla agenta. Kroki oznaczone „(ty)" wykonuje użytkownik. Agent ich nie zrobi i nie powinien próbować.

## 1. Narzędzia

- JDK 21 lub nowszy (`java -version`)
- Node.js 20.9 lub nowszy, najlepiej aktualny LTS (`node -v`)
- git
- Claude Code
- powłoka bash do skryptów z `scripts/` (na Windowsie Git Bash albo WSL; zamiast `./mvnw` używa się tam `mvnw.cmd`)

## 2. Tryby pracy

| Tryb | Co trzeba mieć | Co działa |
|---|---|---|
| Dane testowe (domyślny) | nic | cała aplikacja na zmyślonych trasach i zmyślonym wyniku importu |
| Prawdziwe trasy | klucz Google Routes API | prawdziwe połączenia i czasy przejazdu |
| Prawdziwy import AI | klucz do API zgodnego z OpenAI albo lokalna Ollama | odczyt planu ze zrzutu ekranu |

Tryby tras i AI przełącza się niezależnie. Etapy 1–4 i 6–7 nie wymagają żadnego klucza.

## 3. Klucz Google Routes API (ty)

Potrzebny od etapu 5.

1. Załóż projekt w Google Cloud i włącz w nim rozliczenia. Google wymaga tego do korzystania z Routes API, więc potrzebna jest karta.
2. Włącz w projekcie „Routes API".
3. Utwórz klucz API i ogranicz go do Routes API.
4. Ustaw alert budżetowy na małą kwotę, żeby dostać e-mail, zanim cokolwiek zostanie naliczone.
5. Wpisz klucz do `backend/.env` i ustaw `ROUTING_PROVIDER=google`.

Koszty (stan na listopad 2026): Google daje co miesiąc darmową pulę zapytań, osobno dla każdej kategorii cenowej. Aplikacja ma własny dzienny limit, który trzyma zużycie poniżej tej puli. Szczegóły: `docs/integrations/google-routes.md`.

## 4. Klucz AI (ty)

Potrzebny od etapu 8. Są dwie darmowe drogi.

**Gemini API, darmowy limit (zalecane na start)**

1. Wejdź do Google AI Studio i utwórz klucz API.
2. Utwórz go w osobnym projekcie **bez** podpiętych rozliczeń. Klucz z projektu z aktywnymi rozliczeniami jest liczony jako usługa płatna.
3. Sprawdź w AI Studio, które modele masz w darmowym limicie, i wybierz model typu Flash.
4. Wpisz do `backend/.env`: `AI_PROVIDER=openai`, `AI_BASE_URL=https://generativelanguage.googleapis.com/v1beta/openai/`, `AI_API_KEY`, `AI_MODEL`.

**Ollama, model lokalny**

1. Zainstaluj Ollamę i pobierz model z obsługą obrazów.
2. Wpisz do `backend/.env`: `AI_PROVIDER=openai`, `AI_BASE_URL=http://localhost:11434/v1/`, `AI_API_KEY=ollama`, `AI_MODEL` z nazwą pobranego modelu.

Porównanie, ograniczenia i uwagi o regulaminie: `docs/integrations/ai-import.md`.

## 5. Zmienne środowiskowe

Plik `backend/.env` (poza gitem). Agent tworzy w etapie 1 plik `backend/.env.example` o tej treści:

```
# Trasy: mock albo google
ROUTING_PROVIDER=mock
GOOGLE_MAPS_API_KEY=
ROUTING_DAILY_CALL_LIMIT=150
# true = czas auta z korkami dla odcinków w ciągu 2 godzin (droższa kategoria zapytań)
ROUTING_TRAFFIC_AWARE=false

# Import AI: mock albo openai (dowolne API zgodne z OpenAI Chat Completions)
AI_PROVIDER=mock
AI_BASE_URL=
AI_API_KEY=
AI_MODEL=
# json_schema, json_object albo none
AI_RESPONSE_FORMAT=json_schema
AI_DAILY_CALL_LIMIT=20

# Zamrożony zegar do demonstracji, np. 2026-11-17T15:40:00+01:00
APP_CLOCK_FIXED=
```

Frontend: opcjonalnie `BACKEND_URL` w `frontend/.env.local` (domyślnie `http://localhost:8080`). Frontend nie dostaje żadnych kluczy.

## 6. Uruchamianie

Dwa terminale:

```
cd backend && ./mvnw spring-boot:run
cd frontend && npm run dev
```

Aplikacja: `http://localhost:3000`. Sprawdzenie API: `./scripts/smoke.sh`.

Podgląd bazy w przeglądarce (konsola H2, działa, gdy działa backend):

1. Otwórz `http://localhost:8080/h2-console`.
2. Wpisz: Saved Settings `Generic H2 (Embedded)`, JDBC URL `jdbc:h2:file:./data/zdaze`, User Name `sa`, hasło puste. Kliknij Connect.
3. Zapytanie, np. `SELECT * FROM settings;`, i Run (Ctrl+Enter).

Domyślny JDBC URL w formularzu to `jdbc:h2:~/test`. Bez zmiany (albo z literówką) H2 utworzy nową, pustą bazę zamiast otworzyć naszą. Konsola nie ma hasła, ale przyjmuje połączenia tylko z tego komputera.

## 7. Typowe problemy

| Objaw | Przyczyna i rozwiązanie |
|---|---|
| Backend nie startuje po ustawieniu `ROUTING_PROVIDER=google` | brak `GOOGLE_MAPS_API_KEY` w `backend/.env` |
| Wszystkie odcinki mają `BUDGET_EXCEEDED` | wyczerpany dzienny limit; poczekaj do jutra albo podnieś `ROUTING_DAILY_CALL_LIMIT` |
| Import zwraca 429 | wyczerpany `AI_DAILY_CALL_LIMIT` albo limit dostawcy |
| Frontend pokazuje błąd połączenia | backend nie działa na porcie 8080 albo zły `BACKEND_URL` |
| Baza w dziwnym stanie w trakcie prac | zatrzymaj backend i usuń `backend/data/`; migracje odtworzą schemat |
