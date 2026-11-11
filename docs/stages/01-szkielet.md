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
   - Układ: kolumna `max-w-md`, dolna nawigacja z czterema zakładkami. Strony Plan, Statystyki i Ustawienia jako zaślepki z nagłówkiem.
   - Strona główna: nazwa aplikacji i stan backendu z `/api/health` (ładowanie, sukces, błąd po polsku).
   - Komponenty bazowe w `src/components/ui/`: `Button`, `Card`, `Spinner`, `ErrorState`, `EmptyState`.
4. **`scripts/smoke.sh`.** Sprawdza `GET /api/health`: status 200 i `"status":"ok"`. Kod wyjścia różny od zera przy błędzie. Adres backendu z parametru albo `http://localhost:8080`.
5. **`README.md`.** Trzy krótkie akapity: co to jest, jak uruchomić, gdzie jest dokumentacja.
6. **Dokumenty.** Popraw sekcję „Komendy" w `CLAUDE.md`. Wpisz faktyczne wersje do `docs/architecture.md`, sekcja 1.

## Poza zakresem

Punkty API inne niż Health, ekrany z danymi, dostawcy tras i AI, Docker, CI.

## Kryteria akceptacji

- [ ] `cd backend && ./mvnw verify` przechodzi. Są testy: kontekst wstaje; `GET /api/health` zwraca oczekiwany JSON; w `settings` jest dokładnie jeden wiersz; nieznana ścieżka pod `/api/` zwraca `problem+json` z polem `code`.
- [ ] Test: przy ustawionym `app.clock.fixed` pole `now` w Health ma tę wartość.
- [ ] Po starcie backendu `curl -s localhost:8080/api/health` zwraca `"status":"ok"`, `"routingProvider":"mock"` i `"aiProvider":"mock"`.
- [ ] `cd frontend && npm run lint && npm run typecheck && npm run build` przechodzi.
- [ ] Przy działających obu serwerach `curl -s localhost:3000/api/health` zwraca ten sam JSON co backend.
- [ ] Strona główna pokazuje stan backendu, a po jego zatrzymaniu komunikat błędu po polsku.
- [ ] `./scripts/smoke.sh` kończy się kodem 0 przy działającym backendzie i kodem różnym od zera przy zatrzymanym.
- [ ] `git check-ignore backend/.env backend/data` potwierdza, że oba są ignorowane.
- [ ] `CLAUDE.md` ma sprawdzone komendy, a `docs/architecture.md` faktyczne wersje.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run typecheck && npm run build
./scripts/smoke.sh
```

Stronę główną w obu stanach (backend działa, backend zatrzymany) pokaż użytkownikowi albo poproś go o potwierdzenie.
