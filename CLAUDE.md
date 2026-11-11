# Zdążę — instrukcje dla agenta

Aplikacja webowa (MVP, jeden użytkownik, bez logowania). Użytkownik ma plan zajęć, a aplikacja codziennie pokazuje, **o której wyjść i czym dojechać** do każdego punktu planu, oraz liczy **zaoszczędzone CO2** względem jazdy autem. Funkcja AI: import planu zajęć ze zrzutu ekranu.

„Zdążę" to nazwa robocza.

## Jak pracujemy

- Praca idzie **etapami**. Stan jest w `docs/stages/STATUS.md`. Jeden etap to jedna sesja.
- Start etapu: komenda `/etap` (kolejny niezrobiony) albo `/etap 4` (wskazany). Procedura jest w `.claude/skills/etap/SKILL.md`.
- Czytaj **tylko** plik bieżącego etapu i dokumenty, które on wymienia. Nie czytaj całego `docs/` na zapas.
- Nie rób rzeczy spoza zakresu etapu. Pomysły dopisuj do `docs/backlog.md`.
- Gdy dokumenty sobie przeczą albo czegoś brakuje, zatrzymaj się i zapytaj. Nie zgaduj.
- Zmieniasz kontrakt API, model danych albo regułę domenową? Zaktualizuj właściwy dokument w tym samym commicie.

## Mapa dokumentów

| Plik | Kiedy czytać |
|---|---|
| `docs/product.md` | ekrany, przepływy, zakres MVP, czego nie robimy |
| `docs/domain-rules.md` | łańcuch dnia, „nie zdążę", CO2, seria dni, przypadki testowe |
| `docs/architecture.md` | stos, struktura repo i pakietów, konfiguracja, testy, decyzje |
| `docs/data-model.md` | tabele i migracje |
| `docs/api.md` | kontrakt REST między frontendem a backendem |
| `docs/integrations/google-routes.md` | tylko przy pracy nad trasami z Google |
| `docs/integrations/ai-import.md` | tylko przy imporcie planu przez AI |
| `docs/setup.md` | narzędzia, klucze, zmienne środowiskowe, uruchamianie |
| `docs/stages/` | pliki etapów i `STATUS.md` |
| `docs/ai-log.md` | dziennik pracy z AI; dopisujesz wpis po każdym etapie |
| `docs/backlog.md` | pomysły poza MVP |

Konwencje kodu są w `.claude/rules/backend.md` i `.claude/rules/frontend.md`. Ładują się same, gdy czytasz pliki z `backend/` lub `frontend/`.

## Struktura repo

- `backend/`: Java, Spring Boot, Maven (port 8080)
- `frontend/`: Next.js, TypeScript (port 3000; ścieżka `/api/*` jest przekierowana do backendu)
- `scripts/`: skrypty pomocnicze (`smoke.sh`, `seed-demo.sh`)
- `docs/`: dokumentacja dla agenta i ludzi

## Komendy

Zweryfikuj i popraw tę sekcję w etapie 1, gdy komendy naprawdę działają.

- Backend, testy i formatowanie: `cd backend && ./mvnw verify` (zawiera `spotless:check`)
- Backend, sformatuj kod: `cd backend && ./mvnw spotless:apply`
- Backend, start: `cd backend && ./mvnw spring-boot:run`
- Frontend, kontrola: `cd frontend && npm run lint && npm run format:check && npm run typecheck && npm run build`
- Frontend, sformatuj kod: `cd frontend && npm run format`
- Frontend, start: `cd frontend && npm run dev`
- Test dymny API (backend musi działać): `./scripts/smoke.sh`

## Git

- Repozytorium: `https://github.com/petterwaw/route_planner`, gałąź główna `main`.
- Każdy etap, nowa funkcja i większa zmiana kodu powstaje na osobnej gałęzi (np. `etap-02-plan-reczny`), nie na `main`.
- Commituj często, po każdej większej zmianie kodu.
- Komunikat commita: jedna krótka linia po angielsku (Conventional Commits), opisująca zmianę. Bez podpisów, bez `Co-Authored-By`, bez wzmianek o Claude czy AI.
- Push i scalanie do `main` tylko na polecenie użytkownika.

## Twarde zasady

- **Nic bez zgody użytkownika.** Rozmowa o pomyśle to nie zgoda na wdrożenie. Kod piszesz dopiero po wyraźnej akceptacji.
- **Zgoda obejmuje tylko to, co zaakceptowano.** „Dalej" oznacza następny uzgodniony krok, nigdy resztę etapu, kolejny etap ani całą aplikację.
- **Architekturę decyduje użytkownik.** Każda decyzja spoza dokumentów (nowy pakiet, warstwa, wzorzec, biblioteka, zmiana kontraktu API lub modelu danych) wymaga pytania, zanim powstanie kod.
- **Brak logowania i kont.** Jeden użytkownik. Nie dodawaj autoryzacji, ról ani tabeli użytkowników.
- **Testy nigdy nie wołają zewnętrznych API** (Google, AI). W testach i domyślnie w trybie deweloperskim działają dostawcy `mock`.
- **Sekrety tylko w zmiennych środowiskowych** (`backend/.env`, poza gitem). Klucz nie może trafić do kodu, logów, commitów ani do frontendu.
- **Zewnętrzne API kosztują.** Każde wywołanie przechodzi przez dzienny limit i pamięć podręczną opisane w `docs/architecture.md`. Nie wołaj ich w pętli ani „na próbę".
- **Czas bierz z wstrzykniętego `Clock`**, strefa `Europe/Warsaw`. Nigdy `LocalDateTime.now()` bez zegara.
- **Schemat bazy zmieniasz tylko migracją Flyway.** Nie edytuj migracji, które już są w repo.
- Etap jest skończony dopiero wtedy, gdy przechodzą komendy kontrolne, a każde kryterium akceptacji ma dowód (wynik komendy albo test).
- Nowa biblioteka wymaga zgody użytkownika, chyba że etap ją wymienia.

## Język

- Rozmowa z użytkownikiem, dokumenty i teksty w interfejsie: po polsku.
- Kod, nazwy, komentarze i komunikaty commitów: po angielsku.

## Kompaktowanie

Przy kompaktowaniu zachowaj: numer bieżącego etapu, listę zmienionych plików, niezaliczone kryteria akceptacji i komendy kontrolne.
