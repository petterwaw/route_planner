# Etap 2: Plan ręczny

**Cel:** użytkownik podaje adres domu, tworzy miejsca i układa plan z punktów cyklicznych i jednorazowych.

**Zależy od:** 1. **Klucze:** niepotrzebne.

## Przeczytaj najpierw

- `docs/product.md`: ekrany Plan i Ustawienia
- `docs/data-model.md`: `settings`, `place`, `event`
- `docs/api.md`: Ustawienia, Miejsca, Punkty planu, Błędy

## Zakres

**Backend**

- Migracja V2: tabele `place` i `event`.
- Pakiety `settings`, `place`, `schedule`: encje, repozytoria, serwisy i kontrolery według `docs/api.md`, razem z `POST /api/events/bulk`.
- Walidacja i kody błędów według `docs/api.md`.

**Frontend**

- Ustawienia: formularz adresu domu i bufora przyjazdu; lista miejsc z dodawaniem, edycją i usuwaniem; informacja o dostawcach z Health.
- Plan: lista tygodnia i sekcja „Jednorazowe"; formularz punktu (dodawanie i edycja); usuwanie z potwierdzeniem; wybór miejsca z możliwością dodania nowego bez wychodzenia z formularza.
- Dziś: gdy `setupComplete` jest fałszem, karta z odesłaniem do Ustawień.
- Błędy z API pokazuj tekstem z pola `detail`.

**Skrypty**

- `scripts/smoke.sh`: zapis i odczyt ustawień, dodanie miejsca, dodanie punktu, lista punktów, próba usunięcia używanego miejsca (oczekiwane 409), usunięcie punktu i miejsca. Skrypt nie zostawia po sobie danych i przywraca wcześniejsze ustawienia.

## Poza zakresem

Import, plan dnia, trasy, przycisk „Importuj ze zdjęcia".

## Kryteria akceptacji

- [ ] Każda reguła walidacji z `docs/api.md` ma test.
- [ ] Testy: `PLACE_IN_USE`, `PLACE_NAME_TAKEN`, `NOT_FOUND`.
- [ ] Test: `POST /api/events/bulk` jest atomowy. Jeden błędny element oznacza, że nic się nie zapisało, a odpowiedź wskazuje `index` i `field`.
- [ ] Test: ustawienia zawsze mają jeden wiersz; `setupComplete` zmienia się po zapisaniu adresu.
- [ ] `./mvnw verify` i kontrola frontendu przechodzą.
- [ ] `./scripts/smoke.sh` przechodzi dwa razy z rzędu na tej samej bazie.
- [ ] Ręcznie: dodanie adresu domu, dwóch miejsc i trzech punktów (dwa cykliczne, jeden jednorazowy), edycja i usunięcie punktu. Dane zostają po restarcie backendu.
- [ ] Ręcznie przy szerokości 360 px: formularze mieszczą się bez przewijania w poziomie, a każde pole ma etykietę.

## Weryfikacja

```
cd backend && ./mvnw verify
cd frontend && npm run lint && npm run typecheck && npm run build
./scripts/smoke.sh && ./scripts/smoke.sh
```
