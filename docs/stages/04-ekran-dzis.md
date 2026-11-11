# Etap 4: Ekran „Dziś"

**Cel:** użytkownik widzi oś dnia z odcinkami i opcjami oraz odliczanie do wyjścia. Dane z backendu w trybie `mock`.

**Zależy od:** 3. **Klucze:** niepotrzebne.

## Przeczytaj najpierw

- `docs/product.md`: ekran Dziś
- `docs/api.md`: Plan dnia
- `docs/architecture.md`: sekcja 5

## Zakres

- `scripts/seed-demo.sh`: przez API ustawia adres domu, tworzy trzy miejsca i punkty planu na dzisiejszy i jutrzejszy dzień tygodnia. Uruchomiony drugi raz niczego nie dubluje.
- Typy `DayPlan`, `Leg`, `Option`, `Segment` w `types.ts` i zapytania w `queries.ts`.
- Komponenty w `src/components/day/`: `DayHeader`, `LegCard`, `OptionRow`, `SegmentList`, `Countdown`.
- Oś dnia: na przemian odcinki i zajęcia. Odcinek aktywny rozwinięty, pozostałe zwinięte do jednej linii z opcją polecaną.
- Teksty statusów, ostrzeżeń i odliczania według `docs/product.md`. Każdy kod ostrzeżenia z `docs/api.md` ma polski tekst.
- Nawigacja po dniach. Data w adresie (`/?date=RRRR-MM-DD`), żeby odświeżenie strony jej nie gubiło.
- `src/lib/time.ts` z testami Vitest: minuty do chwili, tekst odliczania (0 lub mniej: „Wyjdź teraz"; 1–60: „Wyjdź za N min"; ponad 60: „Wyjdź o HH:mm"), sprawdzenie, czy godzina wyjścia minęła, formatowanie CO2 („770 g", „3,3 kg").
- Opcja, której godzina wyjścia minęła od ostatniego pobrania danych, od razu pokazuje „Już odjechał", bez czekania na backend.
- Odliczanie odświeża się co 30 s bez pobierania danych. Dane dnia pobierają się ponownie przy powrocie do karty i co 5 min.
- Stany: ładowanie, błąd, pusty dzień, brak adresu domu.
- Skrypt `test` w `package.json` uruchamiający Vitest. Dopisz `npm run test` do komend kontrolnych frontendu w `CLAUDE.md`.

## Poza zakresem

Przyciski „Jadę tym" i „Przelicz od teraz" (nie pokazuj ich jeszcze), ekran Statystyki, zmiany w backendzie.

## Kryteria akceptacji

- [ ] `npm run lint && npm run typecheck && npm run test && npm run build` przechodzi.
- [ ] Testy Vitest pokrywają granice odliczania: 0, 1, 60 i 61 minut oraz chwilę równą godzinie wyjścia (jeszcze nie minęła).
- [ ] Po `./scripts/seed-demo.sh` ekran pokazuje oś dzisiejszego dnia: odcinek aktywny rozwinięty, jedna opcja oznaczona „Polecane", przy niej tekst odliczania.
- [ ] Przy `APP_CLOCK_FIXED` ustawionym po godzinach wyjścia pierwszego odcinka jego opcje mają tekst „Już odjechał".
- [ ] Przyciski dni działają, a odświeżenie strony zachowuje wybraną datę.
- [ ] Przy szerokości 360 px nie ma przewijania w poziomie. Status opcji jest zawsze podany tekstem.
- [ ] Stany pusty, błędu i braku adresu domu mają polskie komunikaty i odesłania.

## Weryfikacja

```
cd frontend && npm run lint && npm run typecheck && npm run test && npm run build
./scripts/seed-demo.sh
```

Ekran sprawdź w przeglądarce, jeśli masz do niej narzędzie, i pokaż zrzuty użytkownikowi. Jeśli nie masz, poproś użytkownika o sprawdzenie według kryteriów i zapisz jego odpowiedź jako dowód.
