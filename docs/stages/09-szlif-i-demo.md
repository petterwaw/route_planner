# Etap 9: Szlif i demo

**Cel:** projekt da się pokazać komisji: uruchamia się z instrukcji, ma dane demonstracyjne, opisany scenariusz pokazu i uczciwy opis użycia AI.

**Zależy od:** etapów, które użytkownik zdecydował się zrobić (co najmniej 1–4 i 8). **Klucze:** niepotrzebne.

## Przeczytaj najpierw

- `docs/product.md`: całość
- `docs/ai-log.md`: całość
- `docs/stages/STATUS.md`: notatki i otwarte sprawy
- `docs/architecture.md`: sekcje 1, 2 i 7

## Zakres

1. **README.md.** Co to jest i dla kogo; 3–5 zrzutów ekranu; schemat architektury (Mermaid); uruchomienie w trybie danych testowych i z kluczami; ograniczenia MVP; źródło współczynników emisji; sekcja „Jak używałem AI" napisana na podstawie `docs/ai-log.md`, razem z tym, co poszło źle.
2. **Dane demonstracyjne.** `scripts/seed-demo.sh` tworzy realistyczny tydzień zajęć w co najmniej dwóch miejscach.
3. **Scenariusz pokazu** w `docs/demo.md`, na około 3 minuty: zwykły poranek, spóźnienie (z wartością `APP_CLOCK_FIXED`), wybór opcji i statystyki, import ze zrzutu ekranu. Przy każdym kroku: co kliknąć i co powinno być widać.
4. **Przegląd interfejsu.** Każdy ekran przy szerokości 360 px i na komputerze; stany ładowania, pusty i błędu; spójne teksty; widoczny fokus z klawiatury; etykiety pól; wystarczający kontrast.
5. **Przegląd spójności.** `docs/api.md` zgodne z kontrolerami i `types.ts`; `docs/data-model.md` zgodne z migracjami; `CLAUDE.md` zgodne z rzeczywistymi komendami. Rozbieżności popraw w dokumentach albo w kodzie i wypisz w podsumowaniu.
6. **Sprzątanie.** Martwy kod, zostawione `TODO`, nadmiarowe logi, nieużywane zależności.

## Poza zakresem

Nowe funkcje, wdrożenie na serwer, Docker, CI. Pomysły zapisz w `docs/backlog.md`.

## Kryteria akceptacji

- [ ] Świeży klon repozytorium w katalogu tymczasowym uruchamia się według README w trybie danych testowych, bez żadnego klucza.
- [ ] Wszystkie komendy kontrolne z `CLAUDE.md` przechodzą.
- [ ] `./scripts/seed-demo.sh` i `./scripts/smoke.sh` przechodzą na świeżej bazie.
- [ ] `docs/demo.md` istnieje, a użytkownik przeszedł scenariusz i potwierdził, że każdy krok działa.
- [ ] W repozytorium nie ma sekretów: przeszukanie historii gita po fragmentach kluczy nic nie znajduje.
- [ ] README mówi wprost o ograniczeniach: emisje to szacunek, brak logowania, źródła danych.
- [ ] Lista rozbieżności dokumentów z kodem jest pusta albo opisana w podsumowaniu.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run typecheck && npm run test && npm run build
./scripts/seed-demo.sh && ./scripts/smoke.sh
```

Przegląd spójności zleć subagentowi w świeżym kontekście: ma porównać dokumenty z kodem i zgłosić tylko faktyczne rozbieżności.
