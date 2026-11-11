---
name: etap
description: Realizuje kolejny albo wskazany etap projektu Zdążę według docs/stages
argument-hint: [numer-etapu]
disable-model-invocation: true
---

Zrealizuj etap projektu. Opcjonalny numer etapu: $ARGUMENTS

## 1. Ustal etap

1. Przeczytaj `docs/stages/STATUS.md`.
2. Jeśli podano numer, weź ten etap. Jeśli nie, weź pierwszy ze statusem innym niż `zrobiony`.
3. Sprawdź, czy etapy, od których zależy, są zrobione. Jeśli nie, powiedz o tym i zatrzymaj się.
4. Uruchom `git status` i sprawdź bieżącą gałąź. Jeśli są niezacommitowane zmiany, zapytaj, co z nimi zrobić.

## 2. Zbierz kontekst

1. Przeczytaj w całości plik etapu z `docs/stages/`.
2. Przeczytaj dokumenty z sekcji „Przeczytaj najpierw" tego etapu, i tylko te.
3. Obejrzyj istniejący kod, którego etap dotyka. Do szerokiego przeszukiwania użyj subagenta, żeby nie zapychać kontekstu.

## 3. Plan

Przedstaw plan w maksymalnie 10 punktach: co powstanie, w jakiej kolejności, jakie testy. Wypisz pytania, jeśli coś jest niejasne. Poczekaj na akceptację użytkownika. Rozmowa o planie to jeszcze nie akceptacja.

## 4. Wykonanie

1. Po akceptacji planu utwórz od `main` gałąź `etap-NN-nazwa` (np. `etap-02-plan-reczny`) i ustaw w `docs/stages/STATUS.md` status `w toku`.
2. Realizuj plan punkt po punkcie. Po każdym punkcie:
   - uruchom właściwe testy;
   - zrób commit: jedna linia po angielsku (Conventional Commits), bez podpisów;
   - krótko zaraportuj: co powstało, wynik testów, co jest następne;
   - zatrzymaj się i poczekaj na zgodę użytkownika na kolejny punkt.
3. Logika domenowa (punkt kontrolny TDD):
   1. Najpierw napisz testy z przypadków w `docs/domain-rules.md`.
   2. Uruchom je i pokaż, że nie przechodzą.
   3. Pokaż użytkownikowi listę testów i poczekaj na jego akceptację.
   4. Dopiero potem pisz implementację, aż testy przejdą. Nie zmieniaj testów po to, żeby przeszły. Jeśli test wydaje się błędny, zapytaj.
4. Trzymaj się planu. Odejście od planu, decyzję architektoniczną albo nową bibliotekę najpierw uzgodnij z użytkownikiem. Pomysły spoza zakresu dopisz do `docs/backlog.md`.

## 5. Weryfikacja

1. Uruchom komendy kontrolne z `CLAUDE.md` i z sekcji „Weryfikacja" etapu.
2. Przejdź kryteria akceptacji jedno po drugim. Przy każdym podaj dowód: komendę i jej wynik albo nazwę testu.
3. Uruchom `/code-review` na diffie gałęzi etapu względem `main`. Zgłoszenia popraw albo opisz, dlaczego zostają.
4. Jeśli etap zmieniał logikę opisaną w `docs/domain-rules.md`, poproś subagenta o przegląd diffu względem kryteriów etapu. Ma zgłaszać tylko braki wpływające na poprawność lub kryteria, bez uwag stylistycznych.
5. Nie oznaczaj etapu jako zrobionego, jeśli jakiekolwiek kryterium nie ma dowodu. Powiedz wprost, czego brakuje.

## 6. Zamknięcie

1. Zaktualizuj `docs/stages/STATUS.md`: status, data, jedno zdanie o wyniku.
2. Dopisz wpis do `docs/ai-log.md` według szablonu z tego pliku.
3. Zaktualizuj dokumenty, jeśli zmienił się kontrakt, model danych lub reguła.
4. Zrób commit zamykający etap.
5. Push gałęzi i pull request do `main` (`gh pr create`) tylko na polecenie użytkownika. Scalenie także.
6. Podsumuj w 3–5 zdaniach: co działa, co użytkownik ma sprawdzić ręcznie, co jest następne. Zaproponuj `/clear` przed kolejnym etapem.
