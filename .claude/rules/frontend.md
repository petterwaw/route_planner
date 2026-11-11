---
paths:
  - "frontend/**"
---

# Frontend: konwencje

## Next.js (twoja wiedza może być nieaktualna)

- Przed pracą nad czymkolwiek z Next.js przeczytaj właściwy dokument z `frontend/node_modules/next/dist/docs/`. To źródło prawdy dla zainstalowanej wersji.
- App Router, TypeScript w trybie strict, katalog `src/`.

## Architektura

- Frontend to cienki klient. Cała logika planu, statusów i CO2 jest w backendzie. Po stronie klienta liczysz tylko odliczanie i to, czy godzina wyjścia już minęła.
- Ekrany z danymi to komponenty klienckie (`"use client"`) z TanStack Query. Nie używaj Server Actions ani route handlers do logiki. Dane idą z backendu.
- Wszystkie wywołania API przechodzą przez `src/lib/api/client.ts`. Typy w `src/lib/api/types.ts` odwzorowują `docs/api.md` jeden do jednego.
- Adresy względne `/api/...`. Przekierowanie do backendu jest w `next.config.ts` (rewrites).
- Aktualny czas to `Date.now() + offset`, gdzie offset wynika z pola `now` w odpowiedzi backendu. Nie ufaj zegarowi urządzenia.

## Interfejs

- Najpierw telefon: projektuj dla szerokości 360 px, treść w kolumnie `max-w-md`. Dolna nawigacja z czterema zakładkami.
- Tailwind i własne proste komponenty w `src/components/ui/`. Bez bibliotek komponentów.
- Teksty po polsku, wpisane w komponentach (bez biblioteki i18n). Godziny w formacie 24-godzinnym, strefa `Europe/Warsaw` przez `Intl.DateTimeFormat`.
- Każdy ekran ma stan ładowania, stan pusty i stan błędu z komunikatem po polsku.
- Kolor nie może być jedynym nośnikiem informacji. Status opcji ma też tekst.
- Przyciski to `<button>`, pola mają `<label>`.

## Zależności

Dozwolone bez pytania: `@tanstack/react-query` oraz `vitest` jako zależność deweloperska (od etapu 4). Każda inna wymaga zgody użytkownika.

## Kontrola

`npm run lint && npm run typecheck && npm run build`. Czyste funkcje (czas, odliczanie) testuj w Vitest, gdy etap tego wymaga.
