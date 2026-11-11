# Etap 10: Zwierzak (opcjonalny)

**Cel:** oszczędności CO2 dostają twarz: mały stworek, który ma się tym lepiej, im więcej zielonych przejazdów w tygodniu.

**Zależy od:** 7. **Klucze:** niepotrzebne. Rób ten etap tylko wtedy, gdy zostaje czas po etapie 9.

## Przeczytaj najpierw

- `docs/api.md`: Statystyki
- `docs/product.md`: ekran Statystyki

## Zakres

Tylko frontend. Backend i kontrakt API się nie zmieniają.

- Stan zwierzaka to czysta funkcja danych z `GET /api/stats`:

  | Poziom | Warunek (`weekSavedGrams`) | Opis |
  |---|---|---|
  | 0 | 0 | śpi |
  | 1 | 1–999 | budzi się |
  | 2 | 1000–2999 | zadowolony |
  | 3 | 3000–5999 | szczęśliwy |
  | 4 | 6000 i więcej | rozkwita |

- Jeśli trzy ostatnie pozycje `last7Days` mają zero oszczędności, poziom spada o jeden (nie niżej niż 0).
- Rysunek: własny, prosty SVG w pięciu wariantach. Postać wymyślona na potrzeby projektu, bez nawiązań do istniejących postaci.
- Imię zwierzaka wpisuje użytkownik. Przechowuj je w `localStorage`, z obsługą braku dostępu (wtedy imię domyślne).
- Miejsce: karta na ekranie Statystyki z imieniem, rysunkiem i jednym zdaniem („Do następnego poziomu brakuje 1,2 kg CO2"). Mała ikona w nagłówku ekranu Dziś.
- Animacje tylko subtelne i z poszanowaniem ustawienia `prefers-reduced-motion`.

## Poza zakresem

Zmiany w backendzie, sklep z dodatkami, powiadomienia, ranking znajomych.

## Kryteria akceptacji

- [ ] Funkcja wyliczająca poziom ma testy Vitest dla każdej granicy z tabeli i dla reguły obniżenia.
- [ ] Każdy poziom ma inny rysunek i tekstowy opis stanu (nie tylko kolor).
- [ ] Imię zostaje po odświeżeniu strony; brak dostępu do `localStorage` nie powoduje błędu.
- [ ] Kontrola frontendu przechodzi.
- [ ] Ręcznie: zapisanie zielonego przejazdu zmienia tekst „do następnego poziomu".

## Weryfikacja

```
cd frontend && npm run lint && npm run typecheck && npm run test && npm run build
```
