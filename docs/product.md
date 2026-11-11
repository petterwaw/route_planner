# Produkt

Co budujemy, dla kogo i gdzie kończy się MVP. Czytaj przy pracy nad ekranami i przepływami.

## W jednym zdaniu

Zdążę bierze plan zajęć użytkownika i każdego dnia mówi, o której wyjść i czym dojechać do kolejnego punktu, a przy okazji liczy, ile CO2 użytkownik zaoszczędził, nie jadąc autem.

## Użytkownik

Jedna osoba: student dojeżdżający na zajęcia. Nie ma kont ani logowania. Kto uruchamia aplikację, ten pracuje na jedynym zestawie danych.

## Po co to, skoro są Mapy Google i czaty z AI

- Aplikacja zna plan użytkownika i sama układa dojazdy na cały dzień, bez pytania.
- Pamięta wybory i liczy z nich statystykę CO2.
- Reaguje na spóźnienie: pokazuje, co jeszcze realnie da się złapać.

## Pętla główna

1. Jednorazowo: adres domu, miejsca, plan (ręcznie albo import ze zrzutu ekranu).
2. Codziennie: ekran „Dziś" pokazuje odcinki dnia i opcje dojazdu.
3. Użytkownik klika „Jadę tym" przy wybranej opcji.
4. Statystyki rosną: zaoszczędzone CO2 i seria zielonych dni.

## Ekrany

Dolna nawigacja: Dziś, Plan, Statystyki, Ustawienia.

### Dziś (`/`)

- Nagłówek: data, przyciski poprzedniego i następnego dnia, przycisk „Dziś".
- Brak adresu domu: karta „Najpierw podaj adres domu" z linkiem do Ustawień.
- Brak punktów w dniu: „Dziś nic nie masz w planie" z linkiem do Planu.
- Oś dnia: na przemian karty odcinków i wiersze zajęć, w kolejności łańcucha.
- Karta odcinka:
  - tytuł „Dom → Wydział" i termin „na 08:15",
  - odcinek aktywny jest rozwinięty, pozostałe zwinięte do jednej linii z opcją polecaną,
  - ostrzeżenia tekstem, np. „Ciasna przesiadka: komunikacją zdążysz tylko wtedy, gdy wyjdziesz przed końcem zajęć".
- Wiersz opcji:
  - ikona i nazwa trybu: „Komunikacja", „Auto", „Rower", „Pieszo",
  - godziny „07:21 → 08:03" i czas „42 min",
  - oszczędność „−3,3 kg CO2 wobec auta",
  - status tekstem: „Zdążysz, zapas 12 min", „Spóźnisz się ok. 8 min", „Już odjechał", „Wyjście 9 min przed końcem zajęć",
  - znacznik „Polecane".
- Odliczanie przy opcji polecanej: „Wyjdź za 12 min", „Wyjdź teraz", a gdy zostało ponad 60 min: „Wyjdź o 07:21".
- Po rozwinięciu opcji widać segmenty, np. „Pieszo 6 min → Pociąg R kier. Tczew, 07:27 Gdańsk Główny → 07:59 Pszczółki, 6 przystanków → Pieszo 4 min".
- Przyciski: „Jadę tym" (etap 7) i „Przelicz od teraz" (etap 6).
- Odcinek zapisany: „Wybrano: Pociąg R 07:27 · −3,3 kg CO2" i „Cofnij".
- Stopka: „Emisje to szacunek na podstawie średnich współczynników."

### Plan (`/plan`)

- Lista tygodnia pogrupowana po dniach (od poniedziałku), osobna sekcja „Jednorazowe".
- Dodawanie, edycja i usuwanie punktów. Przycisk „Importuj ze zdjęcia".
- Formularz punktu: tytuł, miejsce (lista i „Nowe miejsce"), rodzaj („Co tydzień" albo „Jednorazowo"), dzień tygodnia albo data, godziny od–do, opcjonalnie „obowiązuje od" i „obowiązuje do".

### Import (`/plan/import`)

1. Wybór pliku: PNG, JPG albo WebP, do 5 MB. Informacja: „Obraz zostanie wysłany do zewnętrznego modelu AI."
2. Stan „Czytam plan…".
3. Tabela do przeglądu. Każdy wiersz można poprawić albo usunąć. Kolumna „Miejsce" wymaga przypisania; tekst odczytany z obrazu (np. „s. 204, bud. A") jest podpowiedzią. Akcja „Ustaw to miejsce dla wszystkich z tą samą lokalizacją". Ostrzeżenia modelu widoczne nad tabelą.
4. „Zapisz N punktów" i powrót do Planu.

Nic nie zapisuje się bez potwierdzenia użytkownika.

### Statystyki (`/stats`)

- „Zaoszczędzone CO2: 12,8 kg" i „To tyle, co 75,5 km autem".
- „W tym tygodniu: 5,3 kg".
- „Seria: 4 zielone dni".
- Słupki z ostatnich 7 dni, liczba zapisanych przejazdów.
- Nota: „To szacunek. Współczynniki emisji są średnie."

### Ustawienia (`/settings`)

- Adres domu, bufor przyjazdu w minutach.
- Lista miejsc: dodawanie, edycja, usuwanie.
- Informacja, z jakich dostawców korzysta backend (dane testowe czy prawdziwe).

## Zakres MVP

- Plan ręczny: punkty cykliczne i jednorazowe.
- Import planu ze zrzutu ekranu przez model AI, z ekranem przeglądu.
- Łańcuch dnia i opcje dojazdu: komunikacja, auto, rower, pieszo.
- Obsługa spóźnienia i wyjścia po czasie.
- Zapis wybranej opcji, CO2, statystyki, seria.

## Poza MVP

Logowanie i konta, powiadomienia, lokalizacja z urządzenia, opóźnienia pociągów na żywo, tygodnie parzyste i nieparzyste, taksówki, zakup biletów, wdrożenie na serwer, aplikacja natywna. Pomysły trafiają do `docs/backlog.md`.
