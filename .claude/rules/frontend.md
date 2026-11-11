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

- Najpierw telefon: projektuj dla szerokości 360 px, treść w kolumnie `max-w-md`. Dolna nawigacja z dwiema zakładkami: Today i Settings.
- Komponenty: shadcn/ui na Radix UI (styl `radix-nova`, konfiguracja w `components.json`). Kod leży w `src/components/ui/` i dochodzi poleceniem `npx shadcn add <nazwa>` uruchomionym w `frontend/`. Nie pisz od zera tego, co ma shadcn (przyciski, pola, okna, listy wyboru, karty). Własne komponenty składaj z nich w `src/components/<obszar>/`.
- Stany ładowania, błędu i pustki tylko przez `LoadingState`, `ErrorState` i `EmptyState` z `src/components/feedback/`.
- Pliki komponentów w kebab-case (`bottom-nav.tsx`), eksporty w PascalCase. Klasy łącz przez `cn()` z `@/lib/utils`.
- Teksty po angielsku, wpisane w komponentach (bez biblioteki i18n). Godziny w formacie 24-godzinnym, strefa `Europe/Warsaw` przez `Intl.DateTimeFormat` z lokalizacją `en-GB`. Liczby z kropką dziesiętną („3.3 kg").
- Każdy ekran ma stan ładowania, stan pusty i stan błędu z komunikatem po angielsku.
- Kolor nie może być jedynym nośnikiem informacji. Status opcji ma też tekst.
- Przyciski to `<button>`, pola mają `<label>`.

## Dostępność

- ESLint ma pełny zestaw reguł `jsx-a11y` (recommended) jako błędy. Łapie je `npm run lint`, hook po edycji i CI.
- Każda strona ma `metadata.title` i jeden `<h1>`. `<html lang="en">` jest w `layout.tsx`.
- Ikony dekoracyjne mają `aria-hidden="true"`. Przycisk z samą ikoną ma `aria-label`.
- Nawigacja w `<nav aria-label="…">`, aktywny link z `aria-current="page"`.
- Ładowanie ogłaszane przez `role="status"`, błędy przez `role="alert"` (mają to `LoadingState` i `ErrorState`).
- Widoczny fokus z klawiatury (`focus-visible`) na wszystkim, co klikalne. Cele dotykowe co najmniej 24×24 px (WCAG 2.2, poziom AA).

## Zależności

Dozwolone bez pytania:

- `@tanstack/react-query`, `vitest` jako zależność deweloperska (od etapu 4);
- narzędzia formatowania i lintowania z etapu 1: `prettier`, `eslint-config-prettier`, `prettier-plugin-tailwindcss`, `eslint-plugin-jsx-a11y`;
- shadcn/ui i to, co dodał przy instalacji: `shadcn`, `radix-ui`, `class-variance-authority`, `cn`, `lucide-react`, `tw-animate-css`.

Komponent shadcn, który dociąga inną paczkę (np. kalendarz `react-day-picker`), wymaga zgody. Każda inna zależność też.

## Kontrola

`npm run lint && npm run format:check && npm run typecheck && npm run build`. Formatowanie: `npm run format`; po edycji pliku w `src/` hook Claude Code sam uruchamia na nim Prettier i ESLint. Czyste funkcje (czas, odliczanie) testuj w Vitest, gdy etap tego wymaga.
