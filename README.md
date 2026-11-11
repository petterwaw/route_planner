# Zdążę

Aplikacja webowa dla studenta, który dojeżdża na zajęcia. Na podstawie planu zajęć codziennie pokazuje, o której wyjść i czym dojechać do każdego punktu planu, oraz ile CO2 oszczędzasz danego dnia, nie jadąc autem. Plan wpisuje się ręcznie albo importuje ze zrzutu ekranu przez model AI. Jeden użytkownik, bez logowania; interfejs jest po angielsku. „Zdążę" to nazwa robocza.

## Uruchomienie

Potrzebne są JDK 21+ i Node.js 20.9+. Domyślnie backend korzysta z danych testowych (`mock`), więc żadne klucze nie są potrzebne. Prawdziwe trasy Google i import AI wymagają kluczy: `docs/setup.md`. W dwóch terminalach (na Windowsie w Git Bash):

```
cd backend && ./mvnw spring-boot:run       # API: http://localhost:8080
cd frontend && npm install && npm run dev   # aplikacja: http://localhost:3000
```

Szybkie sprawdzenie działającego backendu: `./scripts/smoke.sh`.

## Dokumentacja

Wszystko jest w `docs/`: produkt i ekrany (`product.md`), reguły domenowe (`domain-rules.md`), architektura (`architecture.md`), kontrakt API (`api.md`), model danych (`data-model.md`), uruchomienie, klucze i podgląd bazy (`setup.md`). Praca idzie etapami, a ich stan jest w `docs/stages/STATUS.md`. Instrukcje dla agenta AI są w `CLAUDE.md`.
