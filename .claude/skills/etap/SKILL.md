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
4. Uruchom `git status`. Jeśli są niezacommitowane zmiany, zapytaj, co z nimi zrobić.

## 2. Zbierz kontekst

1. Przeczytaj w całości plik etapu z `docs/stages/`.
2. Przeczytaj dokumenty z sekcji „Przeczytaj najpierw" tego etapu, i tylko te.
3. Obejrzyj istniejący kod, którego etap dotyka. Do szerokiego przeszukiwania użyj subagenta, żeby nie zapychać kontekstu.

## 3. Plan

Przedstaw plan w maksymalnie 10 punktach: co powstanie, w jakiej kolejności, jakie testy. Wypisz pytania, jeśli coś jest niejasne. Poczekaj na akceptację użytkownika.

## 4. Wykonanie

- Pracuj małymi krokami. Po każdym kroku uruchom właściwe testy.
- Logikę domenową zaczynaj od testów opartych na przypadkach z `docs/domain-rules.md`.
- Commituj po każdym działającym kroku (Conventional Commits, po angielsku).
- Trzymaj się zakresu. Rzeczy spoza zakresu dopisz do `docs/backlog.md`.
- Ustaw w `docs/stages/STATUS.md` status `w toku`, gdy zaczynasz.

## 5. Weryfikacja

1. Uruchom komendy kontrolne z `CLAUDE.md` i z sekcji „Weryfikacja" etapu.
2. Przejdź kryteria akceptacji jedno po drugim. Przy każdym podaj dowód: komendę i jej wynik albo nazwę testu.
3. Jeśli etap zmieniał logikę opisaną w `docs/domain-rules.md`, poproś subagenta o przegląd diffu względem kryteriów etapu. Ma zgłaszać tylko braki wpływające na poprawność lub kryteria, bez uwag stylistycznych.
4. Nie oznaczaj etapu jako zrobionego, jeśli jakiekolwiek kryterium nie ma dowodu. Powiedz wprost, czego brakuje.

## 6. Zamknięcie

1. Zaktualizuj `docs/stages/STATUS.md`: status, data, jedno zdanie o wyniku.
2. Dopisz wpis do `docs/ai-log.md` według szablonu z tego pliku.
3. Zaktualizuj dokumenty, jeśli zmienił się kontrakt, model danych lub reguła.
4. Zrób commit zamykający etap.
5. Podsumuj w 3–5 zdaniach: co działa, co użytkownik ma sprawdzić ręcznie, co jest następne. Zaproponuj `/clear` przed kolejnym etapem.
