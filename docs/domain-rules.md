# Reguły domenowe

Źródło prawdy dla logiki planu dnia, statusów, CO2 i CO2 dnia. Etapy wskazują numery sekcji do przeczytania. Przypadki z sekcji 12 zamień na testy jednostkowe.

Wszystkie godziny są w strefie `Europe/Warsaw`. „Teraz" (`now`) pochodzi z wstrzykniętego zegara.

## 1. Pojęcia

- **Dom**: adres z ustawień. Początek i koniec każdego dnia.
- **Miejsce** (`Place`): nazwany adres, np. „Wydział".
- **Punkt planu** (`Event`): tytuł, miejsce, godzina początku i końca. Cykliczny co tydzień (`WEEKLY`) albo jednorazowy (`ONCE`).
- **Wystąpienie**: punkt planu w konkretnym dniu.
- **Odcinek** (`Leg`): przejazd między dwoma kolejnymi punktami łańcucha dnia.
- **Opcja** (`Option`): konkretny sposób pokonania odcinka: tryb, godzina wyjścia, godzina dotarcia.
- **Termin** (`deadline`): początek zajęć, na które prowadzi odcinek.
- **Punkt odniesienia** (`baseline`): emisja CO2 tej samej trasy autem z jedną osobą.

Tryby (`TravelMode`): `TRANSIT` (komunikacja i pociągi), `DRIVE`, `BICYCLE`, `WALK`.

## 2. Wystąpienia w dniu D

Do dnia D należą:

- punkty `WEEKLY`, których `dayOfWeek` równa się dniowi tygodnia D oraz `validFrom ≤ D ≤ validUntil` (pusta granica oznacza brak ograniczenia),
- punkty `ONCE`, których `date` równa się D.

Sortowanie: rosnąco po `startTime`, przy remisie po `id`.

## 3. Łańcuch dnia

Dla wystąpień E1…En (po sortowaniu) łańcuch to: Dom → E1 → E2 → … → En → Dom.

1. Dla każdego Ei poprzednim punktem jest miejsce E(i−1), a dla E1 jest nim Dom.
2. Jeśli poprzedni punkt i Ei mają to samo miejsce (`placeId`), odcinka nie ma.
3. W przeciwnym razie powstaje odcinek `TO_EVENT` z kluczem `to-event-{eventId}`:
   - `deadline` = początek Ei,
   - `arriveBy` = `deadline` minus bufor przyjazdu z ustawień (domyślnie 5 min),
   - `previousEnd` = koniec E(i−1) albo brak, gdy poprzednim punktem jest Dom.
4. Na końcu powstaje odcinek `HOME` z kluczem `home`: z miejsca En do Domu. Jego `previousEnd` to koniec En i jest to zarazem planowa godzina wyjścia (`plannedDepartAt`). Nie ma terminu.
5. Dzień bez wystąpień nie ma odcinków.
6. Brak adresu domu: odpowiedź ma `setupComplete = false` i pustą listę odcinków.
7. Jeśli początek Ei jest wcześniejszy niż koniec E(i−1), odcinek dostaje ostrzeżenie `OVERLAP`. Liczymy go normalnie.

Uproszczenie MVP: przy długiej przerwie między zajęciami nie zakładamy powrotu do domu.

## 4. Planowanie odcinka

Odcinek planujemy jednym z dwóch trybów:

- `ARRIVE_BY(t)`: „mam być na miejscu najpóźniej o t". Używany dla odcinków `TO_EVENT`, t = `arriveBy`.
- `DEPART_AT(t)`: „wychodzę o t". Używany dla odcinka `HOME` (t = koniec ostatnich zajęć) i w trybie „nie zdążę" (sekcja 6).

Kolejność zapytań do dostawcy tras dla jednego odcinka:

1. `DRIVE` bez korków. Zawsze. Daje dystans do punktu odniesienia CO2 i do progów z punktu 3.
2. `TRANSIT`, do 3 wyników.
3. `BICYCLE` tylko gdy dystans auta ≤ 15 km. `WALK` tylko gdy dystans auta ≤ 3 km.

Godziny opcji:

- `TRANSIT`: `departAt` i `arriveAt` podaje dostawca jako czasy „od drzwi do drzwi" (z dojściem pieszo).
- Pozostałe tryby w `ARRIVE_BY(t)`: `arriveAt = t`, `departAt = t − czas przejazdu`. Dla `DRIVE` odejmij jeszcze 5 min na parkowanie.
- Pozostałe tryby w `DEPART_AT(t)`: `departAt = t`, `arriveAt = t + czas przejazdu`. Dla `DRIVE` dodaj 5 min na parkowanie.

Czas trwania opcji (`durationMin`) to `arriveAt − departAt`, czyli dla auta razem z parkowaniem.

Zaokrąglenia: czas trwania w górę do pełnej minuty, `slackMin` w dół, `lateByMin` i `leaveEarlyMin` w górę.

Każdy odcinek ma też `earliestDeparture`, czyli najwcześniejszą realną chwilę wyjścia:

- dla dzisiejszej daty: późniejsza z wartości `now` i `previousEnd` (samo `now`, gdy `previousEnd` nie ma),
- dla dat przyszłych: `previousEnd` (może go nie być),
- dla dat przeszłych: puste.

Dla dat przeszłych nie wołamy dostawcy. Zwracamy odcinki z zapisanych migawek (jeśli są) i zapisane przejazdy.

## 5. Statusy opcji

Statusy liczymy przy każdym odczycie, z aktualnym `now`. Nie zapisujemy ich w pamięci podręcznej.

- `MISSED`: `departAt < now`. Wyjście dokładnie o `now` jeszcze się liczy.
- `LATE`: odcinek ma termin i `arriveAt > deadline`. `lateByMin = arriveAt − deadline`.
- `ON_TIME`: pozostałe. `slackMin = deadline − arriveAt` (dla odcinka `HOME` brak).

`MISSED` ma pierwszeństwo przed pozostałymi. Z definicji wynika, że w datach przyszłych żadna opcja nie jest `MISSED`, a w przeszłych są nimi wszystkie.

## 6. „Nie zdążę" i „wyszedłem później"

Jedna reguła obsługuje spóźnienie, wyjście po czasie i ciasne przesiadki. Opiera się na `earliestDeparture` z sekcji 4.

Zasady:

1. Opcja, która wymaga wyjścia przed końcem poprzednich zajęć (`now ≤ departAt < previousEnd`), nie jest `MISSED`. Dostaje `leaveEarlyMin = previousEnd − departAt`. Użytkownik może ją wybrać, bo może wyjść wcześniej. Nie dotyczy odcinka `HOME`.
2. Dla każdego trybu osobno: jeśli żadna opcja planowa tego trybu nie ma `departAt ≥ earliestDeparture`, dokładamy opcje „na żywo" tego trybu, liczone jak `DEPART_AT(earliestDeparture)`. Dla `TRANSIT` to nowe zapytanie do dostawcy. Dla pozostałych trybów użyj czasów przejazdu z migawki planowej, bez nowego zapytania.
3. Opcje „na żywo" mają `source = LIVE`. Opcję identyczną z planową (ten sam tryb i `departAt`) pomiń.
4. Jeśli istnieje opcja `TRANSIT` ze statusem `ON_TIME` i `leaveEarlyMin > 0`, a nie istnieje opcja `TRANSIT` ze statusem `ON_TIME` i `leaveEarlyMin = 0`, odcinek dostaje ostrzeżenie `TIGHT_TRANSFER` (komunikacją da się zdążyć tylko po wcześniejszym wyjściu). Gdy odcinek ma już `OVERLAP`, nie dodawaj `TIGHT_TRANSFER`.
5. Jeśli `now ≥ arriveBy`, pomiń planowe zapytanie `TRANSIT` i od razu zrób zapytanie „na żywo". Zapytanie `DRIVE` bez korków robisz zawsze.
6. Przeliczenie na żądanie (`replan`) wymusza nowe zapytanie „na żywo", nie częściej niż raz na 30 s dla odcinka.
7. Punkt wyjścia w trybie „na żywo" to planowy początek odcinka. Nie używamy lokalizacji urządzenia.

## 7. Stan odcinka

- `DONE`: istnieje zapisany przejazd dla (data, klucz odcinka).
- `PAST`: nie `DONE` oraz: dla `TO_EVENT` `now ≥` koniec zajęć, dla `HOME` data jest wcześniejsza niż dziś. W datach przeszłych każdy niezapisany odcinek jest `PAST`.
- `ACTIVE`: tylko dziś. Pierwszy w kolejności odcinek, który nie jest `DONE` ani `PAST`.
- `UPCOMING`: pozostałe.

Odcinek, na którego zajęcia użytkownik jest już spóźniony (zajęcia trwają), pozostaje `ACTIVE`.

## 8. Rekomendacja i kolejność opcji

Rekomendowana jest jedna opcja na odcinek:

1. Kandydaci A: nie `MISSED`, status `ON_TIME`, `leaveEarlyMin = 0`, czas trwania ≤ 1,5 × czas najszybszego kandydata + 10 min. Wygrywa najniższe `co2Grams`, przy remisie najpóźniejsze `departAt`.
2. Jeśli A jest pusty, kandydaci B: nie `MISSED`, `departAt ≥ earliestDeparture`. Wygrywa najwcześniejsze `arriveAt`, przy remisie najniższe `co2Grams`.
3. Jeśli B jest pusty, nic nie rekomendujemy.

Kolejność zwracanych opcji: rekomendowana, potem `ON_TIME` od najpóźniejszego wyjścia, potem `LATE` od najwcześniejszego dotarcia, na końcu `MISSED`.

## 9. CO2

Współczynniki w gramach CO2e na pasażerokilometr (konfiguracja `app.co2.grams-per-km`):

| Pojazd | g/km |
|---|---|
| auto (`DRIVE`) | 170 |
| autobus (`BUS`) | 96 |
| kolej (`RAIL`) | 35 |
| tramwaj (`TRAM`) | 28 |
| metro (`SUBWAY`) | 27 |
| prom (`FERRY`) | 19 |
| inne (`OTHER`) | 96 |
| pieszo, rower | 0 |

Źródło: Our World in Data, „Which form of transport has the smallest carbon footprint?" (2023), dane rządu Wielkiej Brytanii. To średnie brytyjskie. Polska kolej ma inny miks energii, więc wynik jest przybliżeniem. Interfejs ma to mówić wprost („szacunek").

Wzory:

- `co2Grams(opcja)` = suma po segmentach: dystans w km × współczynnik pojazdu. Segmenty piesze liczą się jako 0.
- `baseline` = dystans auta bez korków dla tego odcinka × 170.
- `savedCo2Grams = max(0, baseline − co2Grams)`. Nigdy poniżej zera.
- Wyniki zaokrąglaj do pełnych gramów.
- Gdy zapytanie `DRIVE` się nie powiodło, `baseline` licz z dystansu samej opcji i dodaj ostrzeżenie `PROVIDER_ERROR`.

## 10. Zapis przejazdu

- Użytkownik wskazuje opcję („Jadę tym"). Zapisujemy migawkę liczb z tej chwili: tryb, opis, dystans, `co2Grams`, `baseline`, `savedCo2Grams`.
- Jeden zapis na (data, klucz odcinka). Ponowny wybór nadpisuje poprzedni.
- Zapisać można każdą opcję obecną w migawce odcinka, także `MISSED` (użytkownik zaznacza po fakcie).
- Późniejsza zmiana współczynników nie zmienia zapisanych przejazdów.

## 11. CO2 dnia

- `savedCo2Grams` dnia D: suma `savedCo2Grams` wszystkich zapisów z datą D. Brak zapisów: 0.
- Liczą się wszystkie zapisy z tej daty, także te, których odcinka nie ma już w łańcuchu (np. punkt planu usunięto po zapisie). Zapis zostaje w historii (`docs/data-model.md`, `trip_log`).
- To jedyna statystyka w MVP. Ekran Today pokazuje ją na górze dla wybranego dnia.

## 12. Przypadki testowe

Dane wspólne: bufor przyjazdu 5 min, parkowanie 5 min. Miejsca: A, B. Dom: H. Sztuczny dostawca zwraca dla `DRIVE` na każdym odcinku 24,0 km i 30 min, a dla `BICYCLE` i `WALK` nic (dystans przekracza progi). Opcje `TRANSIT` podaje każdy przypadek; ich segmenty i emisja są jak w C1 (770 g).

Plan dnia: E1 08:15–09:45 w A, E2 10:00–11:30 w A, E3 12:15–13:45 w B.

Oczekiwany łańcuch: `to-event-1` (H→A, `arriveBy` 08:10), brak odcinka dla E2, `to-event-3` (A→B, `arriveBy` 12:10, `previousEnd` 11:30), `home` (B→H, `previousEnd` i `plannedDepartAt` 13:45).

Planowa opcja `DRIVE` dla `to-event-1` to zawsze 07:35→08:10 (35 min razem z parkowaniem), a dla `to-event-3` 11:35→12:10.

| Nr | Sytuacja | Oczekiwany wynik |
|---|---|---|
| T1 | `now` 06:00. Odcinek `to-event-1`, planowa opcja `TRANSIT` 07:21→08:03 | `TRANSIT`: `ON_TIME`, `slackMin` 12, rekomendowana (niższe CO2 niż auto). `DRIVE`: `ON_TIME`, `slackMin` 5 |
| T2 | `now` 07:30. Te same opcje planowe | `TRANSIT` 07:21 jest `MISSED`. Żadna planowa opcja `TRANSIT` nie wychodzi od 07:30, więc zapytanie `DEPART_AT(07:30)`; wynik 07:41→08:23 jest `LATE`, `lateByMin` 8, `source` `LIVE`. `DRIVE` 07:35 zostaje `ON_TIME`, bez opcji „na żywo", i jest rekomendowana |
| T3 | `now` 07:21:00. Opcja z `departAt` 07:21 | nie jest `MISSED` |
| T4 | `now` 06:00. Odcinek `to-event-3`, planowa opcja `TRANSIT` 11:21→12:02 | `TRANSIT`: `ON_TIME`, `leaveEarlyMin` 9. Zapytanie `DEPART_AT(11:30)`; wynik 11:41→12:22 jest `LATE`, `lateByMin` 7, `source` `LIVE`. Ostrzeżenie `TIGHT_TRANSFER`. `DRIVE` 11:35: `ON_TIME`, `leaveEarlyMin` 0 |
| T5 | Osobny plan dnia: tylko E1 08:15–09:45 w A i E4 09:30–11:00 w B. `now` 06:00. Planowa opcja `TRANSIT` 08:41→09:23 | odcinek `to-event-4`: ostrzeżenie `OVERLAP`, `previousEnd` 09:45. Opcja 08:41 jest `ON_TIME` z `leaveEarlyMin` 64. Zapytanie `DEPART_AT(09:45)`; wynik 10:01→10:43 jest `LATE`, `lateByMin` 73. `DRIVE` planowa 08:50→09:25 ma `leaveEarlyMin` 55, a „na żywo" 09:45→10:20 jest `LATE`, `lateByMin` 50 |
| T6 | `now` 15:00, odcinek `home` niezapisany. Planowa opcja `TRANSIT` 13:51→14:33 | planowe `TRANSIT` i `DRIVE` (13:45→14:20) są `MISSED`. Zapytanie `TRANSIT` `DEPART_AT(15:00)`. `DRIVE` „na żywo" 15:00→15:35 bez nowego zapytania do dostawcy |
| T7 | Data jutrzejsza, dowolne `now` | żadna opcja nie jest `MISSED`; `earliestDeparture` równa się `previousEnd` (dla `to-event-1` puste, dla `to-event-3` 11:30) |
| T8 | Brak adresu domu | `setupComplete` false, brak odcinków |
| T9 | Dzień bez wystąpień | puste listy `events` i `legs` |
| T10 | `now` 10:00, brak zapisów | `to-event-1` jest `PAST`, `to-event-3` jest `ACTIVE`, `home` jest `UPCOMING` |
| T11 | `now` 10:00, zapis dla `to-event-3` | `to-event-3` jest `DONE`, `home` jest `ACTIVE` |

CO2 (dystans auta 24,0 km, więc `baseline` 4080 g):

| Nr | Opcja | `co2Grams` | `savedCo2Grams` |
|---|---|---|---|
| C1 | pieszo 0,5 km + kolej 22,0 km + pieszo 0,3 km | 770 | 3310 |
| C2 | `DRIVE` 24,0 km | 4080 | 0 |
| C3 | `BICYCLE` 23,0 km | 0 | 4080 |
| C4 | autobus 5,0 km + tramwaj 3,0 km | 564 | 3516 |
| C5 | autobus 50,0 km (objazd) | 4800 | 0 |

Rekomendacja:

| Nr | Opcje `ON_TIME` | Rekomendowana |
|---|---|---|
| R1 | tramwaj 15 min i 84 g, pieszo 36 min i 0 g | tramwaj (pieszo przekracza 1,5 × 15 + 10 = 32,5 min) |
| R2 | tramwaj 15 min i 84 g, rower 12 min i 0 g, auto 10 min i 510 g | rower |
| R3 | brak `ON_TIME`; `LATE`: pociąg dotarcie 08:23, auto dotarcie 08:19 | auto |

CO2 dnia (plan dnia jak wyżej, data D):

| Nr | Zapisy | `savedCo2Grams` dnia D |
|---|---|---|
| D1 | brak zapisów | 0 |
| D2 | `to-event-1` opcja C1 (3310 g), `home` opcja C2 (0 g) | 3310 |
| D3 | `to-event-1` opcja C1, potem ponowny wybór C2 dla tego samego odcinka | 0 |
| D4 | `to-event-1` opcja C1 z datą D, `to-event-3` opcja C4 z datą D+1 | 3310 |
| D5 | `to-event-3` opcja C4 (3516 g), potem usunięty punkt E3 | 3516 |
