# Produkt

Co budujemy, dla kogo i gdzie kończy się MVP. Czytaj przy pracy nad ekranami i przepływami.

## W jednym zdaniu

Zdążę bierze plan zajęć użytkownika i każdego dnia mówi, o której wyjść i czym dojechać do kolejnego punktu, a przy okazji pokazuje, ile CO2 użytkownik zaoszczędził tego dnia, nie jadąc autem.

## Użytkownik

Jedna osoba: student dojeżdżający na zajęcia. Nie ma kont ani logowania. Kto uruchamia aplikację, ten pracuje na jedynym zestawie danych.

## Po co to, skoro są Mapy Google i czaty z AI

- Aplikacja zna plan użytkownika i sama układa dojazdy na cały dzień, bez pytania.
- Pamięta wybory i pokazuje, ile CO2 dzięki nim zaoszczędził w danym dniu.
- Reaguje na spóźnienie: pokazuje, co jeszcze realnie da się złapać.

## Pętla główna

1. Jednorazowo w Settings: adres domu, miejsca, plan (ręcznie albo import ze zrzutu ekranu).
2. Codziennie: ekran Today pokazuje dojazdy dnia i opcje dojazdu.
3. Użytkownik klika „I'm taking this" przy wybranej opcji.
4. Na górze ekranu Today rośnie CO2 zaoszczędzone tego dnia.

## Ekrany

Interfejs jest po angielsku. Teksty w cudzysłowach poniżej to brzmienie w aplikacji.

Dolna nawigacja: dwie zakładki, Today i Settings.

### Today (`/`)

- Nagłówek: data, strzałki poprzedniego i następnego dnia, przycisk „Today". Wybrana data jest w adresie (`/?date=YYYY-MM-DD`).
- Pod nagłówkiem jedna linia z CO2 zaoszczędzonym w wybranym dniu (pole `savedCo2Grams` planu dnia): „CO2 saved: 3.3 kg", przy zerze „CO2 saved: 0 g". Innych statystyk nie ma.
- Brak adresu domu: karta „Add your home address first" z linkiem do Settings.
- Brak punktów w dniu: „Nothing planned for this day" z linkiem do Settings.
- Oś dnia od góry do dołu: na przemian karty dojazdów (odcinków) i wiersze zajęć, w kolejności łańcucha.
- Karta odcinka:
  - tytuł „Home → Faculty" i termin „by 08:15",
  - odcinek aktywny jest rozwinięty, pozostałe zwinięte do jednej linii z opcją polecaną,
  - ostrzeżenia tekstem, np. „Tight transfer: by public transport you only make it if you leave before the class ends".
- Wiersz opcji (opcje to alternatywy: wcześniejsze i późniejsze wyjścia, różne środki transportu):
  - ikona i nazwa trybu: „Transit", „Car", „Bike", „Walk",
  - godziny „07:21 → 08:03" i czas „42 min",
  - oszczędność „−3.3 kg CO2 vs car",
  - status tekstem: „On time, 12 min to spare", „About 8 min late", „Already gone", „Leave 9 min before class ends",
  - znacznik „Recommended".
- Odliczanie przy opcji polecanej: „Leave in 12 min", „Leave now", a gdy zostało ponad 60 min: „Leave at 07:21".
- Po rozwinięciu opcji widać segmenty, np. „Walk 6 min → Train R towards Tczew, 07:27 Gdańsk Główny → 07:59 Pszczółki, 6 stops → Walk 4 min".
- Przyciski: „I'm taking this" (etap 7) i „Replan from now" (etap 6).
- Odcinek zapisany: „Chosen: Train R 07:27 · −3.3 kg CO2" i „Undo".
- Stopka: „Emissions are estimates based on average factors."

### Settings (`/settings`)

Jedna przewijana strona z sekcjami:

- **Home:** adres domu, bufor przyjazdu w minutach.
- **Places:** lista miejsc z dodawaniem, edycją i usuwaniem.
- **Schedule:** lista tygodnia pogrupowana po dniach (od poniedziałku) i osobna grupa „One-off". Dodawanie, edycja i usuwanie punktów. Przycisk „Import from screenshot".
  - Formularz punktu: tytuł, miejsce (lista i „New place"), rodzaj („Weekly" albo „One-off"), dzień tygodnia albo data, godziny od–do, opcjonalnie „Valid from" i „Valid until".
- **Data sources:** informacja, z jakich dostawców korzysta backend (dane testowe czy prawdziwe).

### Import (`/settings/import`)

Podstrona Settings. W dolnej nawigacji podświetlona jest zakładka Settings.

1. Wybór pliku: PNG, JPG albo WebP, do 5 MB. Informacja: „The image will be sent to an external AI model."
2. Stan „Reading your schedule…".
3. Tabela do przeglądu. Każdy wiersz można poprawić albo usunąć. Kolumna „Place" wymaga przypisania; tekst odczytany z obrazu (np. „room 204, bldg A") jest podpowiedzią. Akcja „Use this place for all rows with the same location". Ostrzeżenia modelu widoczne nad tabelą.
4. „Save N events" i powrót do Settings.

Nic nie zapisuje się bez potwierdzenia użytkownika.

## Zakres MVP

- Plan ręczny: punkty cykliczne i jednorazowe.
- Import planu ze zrzutu ekranu przez model AI, z ekranem przeglądu.
- Łańcuch dnia i opcje dojazdu: komunikacja, auto, rower, pieszo.
- Obsługa spóźnienia i wyjścia po czasie.
- Zapis wybranej opcji i CO2 zaoszczędzone w danym dniu.

## Poza MVP

Logowanie i konta, powiadomienia, lokalizacja z urządzenia, opóźnienia pociągów na żywo, tygodnie parzyste i nieparzyste, taksówki, zakup biletów, wdrożenie na serwer, aplikacja natywna, statystyki zbiorcze i seria zielonych dni. Pomysły trafiają do `docs/backlog.md`.
