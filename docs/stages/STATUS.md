# Stan etapów

Agent czyta ten plik na początku każdej sesji i aktualizuje go przy rozpoczęciu i zakończeniu etapu. Statusy: `do zrobienia`, `w toku`, `zrobiony`.

| Nr | Etap | Plik | Zależy od | Klucze | Status | Data | Notatka |
|---|---|---|---|---|---|---|---|
| 1 | Szkielet projektu | `01-szkielet.md` | brak | nie | w toku | 2026-11-11 | |
| 2 | Plan ręczny | `02-plan-reczny.md` | 1 | nie | do zrobienia | | |
| 3 | Łańcuch dnia (backend) | `03-lancuch-dnia.md` | 2 | nie | do zrobienia | | |
| 4 | Ekran „Dziś" | `04-ekran-dzis.md` | 3 | nie | do zrobienia | | |
| 5 | Trasy z Google | `05-google-routes.md` | 3 | Google | do zrobienia | | |
| 6 | „Nie zdążę" | `06-nie-zdaze.md` | 4 | nie | do zrobienia | | |
| 7 | Wybór przejazdu i statystyki | `07-wybor-i-statystyki.md` | 4 | nie | do zrobienia | | |
| 8 | Import planu ze zrzutu ekranu (AI) | `08-import-ai.md` | 2 | AI | do zrobienia | | |
| 9 | Szlif i demo | `09-szlif-i-demo.md` | 1–8 | nie | do zrobienia | | |
| 10 | Zwierzak (opcjonalny) | `10-zwierzak.md` | 7 | nie | do zrobienia | | |

## Kolejność

- Domyślnie po kolei.
- Etapy 5, 6, 7 i 8 nie zależą od siebie nawzajem. Można zmienić ich kolejność.
- Etap 8 (funkcja AI) zależy tylko od etapu 2. Jeśli goni czas albo trzeba szybko pokazać funkcję AI, zrób go zaraz po etapie 2.
- Minimalna wersja do pokazania: etapy 1–4 i 8 (wszystko na danych testowych plus import AI). Etap 5 dodaje prawdziwe połączenia.

## Otwarte sprawy

(agent dopisuje tu rzeczy czekające na użytkownika, np. „weryfikacja na żywo z kluczem Google")
