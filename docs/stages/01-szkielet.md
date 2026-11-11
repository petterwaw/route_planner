# Etap 1: Szkielet projektu

**Cel:** oba projekty wstają, rozmawiają ze sobą, a komendy kontrolne działają. Żadnej logiki biznesowej.

**Zależy od:** niczego. **Klucze:** niepotrzebne.

## Przeczytaj najpierw

- `docs/architecture.md`: sekcje 1, 2, 3 (Konfiguracja, Zegar, Błędy), 5 i 6
- `docs/setup.md`: sekcje 1, 5 i 6
- `docs/api.md`: Zasady ogólne, Health, Błędy
- `docs/data-model.md`: Zasady, `settings`

## Zakres

1. **Repozytorium.** `git init`, jeśli trzeba. Plik `.gitignore` już jest. Uzupełnij go, gdy generatory dodadzą coś nowego.
2. **Backend w `backend/`.**
   - Wygeneruj projekt przez start.spring.io: Maven, Java 21, najnowszy stabilny Spring Boot 4, grupa `pl.zdaze`, artefakt `backend`, pakiet `pl.zdaze`. Zależności: Spring Web (MVC), Validation, Spring Data JPA, Flyway, H2, klient HTTP (`RestClient`). Identyfikatory zależności weź z metadanych generatora, nie z pamięci.
   - `application.yml` według architektury: port 8080, H2 w pliku `./data/zdaze`, Hibernate w trybie `validate`, import pliku `.env`, właściwości `app.*` z wartościami domyślnymi.
   - `AppProperties`, bean `Clock` z obsługą `app.clock.fixed`, `@RestControllerAdvice` zwracający `ProblemDetail` z polem `code`.
   - Migracja V1: tabela `settings` z jednym wierszem.
   - `GET /api/health` według `docs/api.md`. Nazwy dostawców to na razie wartości z konfiguracji.
   - `backend/.env.example` o treści z `docs/setup.md`.
3. **Frontend w `frontend/`.**
   - Najpierw `npx create-next-app@latest --help`. Potem wygeneruj projekt z opcjami: TypeScript, ESLint, Tailwind, App Router, katalog `src/`, npm. Bez dodatków.
   - Generator tworzy `frontend/AGENTS.md` z odesłaniem do dokumentacji w `node_modules/next/dist/docs/`. Zostaw go. Jeśli nie powstał `frontend/CLAUDE.md`, utwórz go z jedną linią `@AGENTS.md`.
   - Skrypt `typecheck` (`tsc --noEmit`) w `package.json`.
   - Przekierowanie `/api/:path*` do `BACKEND_URL` w `next.config.ts`.
   - TanStack Query z dostawcą w `layout.tsx`.
   - `src/lib/api/client.ts`: pobieranie JSON i zamiana odpowiedzi `problem+json` na błąd z polami `code` i `detail`. `src/lib/api/types.ts`. `src/lib/time.ts`: przesunięcie zegara z pola `now`.
   - Układ: kolumna `max-w-md`, dolna nawigacja z dwiema zakładkami: Today (`/`) i Settings (`/settings`). Strona Settings jako zaślepka z nagłówkiem.
   - Strona główna (Today): nazwa aplikacji i stan backendu z `/api/health` (ładowanie, sukces, błąd po angielsku).
   - Komponenty z shadcn/ui (Radix) w `src/components/ui/`: `Button`, `Card`, `Alert`, `Empty`, `Spinner`. Na nich `LoadingState`, `ErrorState` i `EmptyState` w `src/components/feedback/`. ESLint z pełnym zestawem reguł `jsx-a11y`.
4. **`scripts/smoke.sh`.** Sprawdza `GET /api/health`: status 200 i `"status":"ok"`. Kod wyjścia różny od zera przy błędzie. Adres backendu z parametru albo `http://localhost:8080`.
5. **`README.md`.** Trzy krótkie akapity: co to jest, jak uruchomić, gdzie jest dokumentacja.
6. **Dokumenty.** Popraw sekcję „Komendy" w `CLAUDE.md`. Wpisz faktyczne wersje do `docs/architecture.md`, sekcja 1. Opisz nowe narzędzia z punktów 7–11 w `docs/architecture.md` (sekcja 6) i w `.claude/rules/`. Usuń CI z „Poza zakresem" etapu 9.

Punkty 7–11 dodane 2026-11-11 decyzją użytkownika: jakość kodu i proces pracy.

Też 2026-11-11, decyzją użytkownika: cały interfejs i komunikaty API po angielsku (także `detail` błędów i komunikaty walidacji z punktu 2); dwie zakładki zamiast czterech; zamiast ekranu statystyk tylko CO2 dnia na ekranie Today; domyślna etykieta domu `Home` (poprawka w V1 jako wyjątek, bo migracja nie opuściła komputera); gotowe, dostępne komponenty shadcn/ui na Radix zamiast własnych oraz pełne reguły `jsx-a11y` w ESLint.

7. **Proces pracy w `/etap`** (`.claude/skills/etap/SKILL.md`).
   - Start etapu: gałąź `etap-NN-nazwa` od `main`.
   - Wykonanie: po każdym punkcie planu krótki raport (co powstało, wynik testów) i czekanie na zgodę użytkownika.
   - Logika domenowa: najpierw testy z przypadków w `docs/domain-rules.md`, uruchomione i czerwone, pokazane użytkownikowi. Implementacja dopiero po jego akceptacji.
   - Zamknięcie: `/code-review` na diffie gałęzi. Push i pull request do `main` tylko na polecenie użytkownika.
8. **Formatowanie.**
   - Backend: wtyczka Spotless z `palantir-java-format`, `spotless:check` podpięty pod `verify`.
   - Frontend: Prettier z `eslint-config-prettier` i `prettier-plugin-tailwindcss`. Skrypty `format` i `format:check`; `format:check` wchodzi do komendy kontrolnej.
   - Jednorazowe sformatowanie istniejącego kodu osobnym commitem.
9. **Zasady architektury jako testy (ArchUnit)** w `ArchitectureTest`:
   - żadnych wywołań `now()` bez `Clock` (`Instant`, `LocalDate`, `LocalTime`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`);
   - kontrolery nie zależą od repozytoriów Spring Data i nie zwracają encji JPA;
   - `RestClient` tylko w adapterach `routing.google` i `importer.openai`;
   - klasy spoza `pl.zdaze.routing` nie zależą od `routing.google` ani `routing.mock`; analogicznie dla `importer.openai` i `importer.mock`;
   - brak cykli między pakietami funkcji `pl.zdaze.*`;
   - brak wstrzykiwania przez pola i brak `System.out` oraz `System.err`.
   - Reguły dla pakietów, które jeszcze nie istnieją, nie mogą psuć testów.
10. **Hooki Claude Code** w `.claude/settings.json`: po każdej edycji pliku w `frontend/src/` uruchamiają Prettier i ESLint z `--fix` na tym pliku. Błąd, którego nie da się poprawić automatycznie, wraca do agenta. Javy hook nie sprawdza (Maven po każdej edycji jest za wolny); robi to `./mvnw verify`.
11. **CI.** `.github/workflows/ci.yml`: przy pushu do `main` i przy pull requeście uruchamia komendy kontrolne backendu i frontendu. Bez kluczy i bez połączeń z zewnętrznymi API.

## Poza zakresem

Punkty API inne niż Health, ekrany z danymi, dostawcy tras i AI, Docker.

## Kryteria akceptacji

- [ ] `cd backend && ./mvnw verify` przechodzi. Są testy: kontekst wstaje; `GET /api/health` zwraca oczekiwany JSON; w `settings` jest dokładnie jeden wiersz; nieznana ścieżka pod `/api/` zwraca `problem+json` z polem `code`.
- [ ] Test: przy ustawionym `app.clock.fixed` pole `now` w Health ma tę wartość.
- [ ] Po starcie backendu `curl -s localhost:8080/api/health` zwraca `"status":"ok"`, `"routingProvider":"mock"` i `"aiProvider":"mock"`.
- [ ] `cd frontend && npm run lint && npm run typecheck && npm run build` przechodzi.
- [ ] Przy działających obu serwerach `curl -s localhost:3000/api/health` zwraca ten sam JSON co backend.
- [ ] Strona główna pokazuje stan backendu, a po jego zatrzymaniu komunikat błędu po angielsku.
- [ ] `./scripts/smoke.sh` kończy się kodem 0 przy działającym backendzie i kodem różnym od zera przy zatrzymanym.
- [ ] `git check-ignore backend/.env backend/data` potwierdza, że oba są ignorowane.
- [ ] `CLAUDE.md` ma sprawdzone komendy, a `docs/architecture.md` faktyczne wersje.
- [ ] `/etap` opisuje gałąź etapu, zatrzymanie po każdym punkcie planu, punkt kontrolny TDD, `/code-review` oraz push i pull request na polecenie użytkownika.
- [ ] Źle sformatowany plik Java powoduje błąd `./mvnw verify`, a po `./mvnw spotless:apply` weryfikacja przechodzi.
- [ ] `npm run format:check` przechodzi i jest częścią komendy kontrolnej frontendu.
- [ ] `ArchitectureTest` działa w `./mvnw verify`. Celowe naruszenie (np. `LocalDateTime.now()` bez zegara) powoduje błąd testu.
- [ ] Hook: edycja pliku `.tsx` ze złym formatowaniem kończy się sformatowanym plikiem, a błąd ESLint wraca do agenta.
- [ ] Workflow CI przechodzi na GitHubie dla pull requesta etapu 1 (`gh pr checks`).
- [ ] `/code-review` uruchomiony na diffie etapu; zgłoszenia poprawione albo opisane.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run format:check && npm run typecheck && npm run build
./scripts/smoke.sh
```

Stronę główną w obu stanach (backend działa, backend zatrzymany) pokaż użytkownikowi albo poproś go o potwierdzenie.
