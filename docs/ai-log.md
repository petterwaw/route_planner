# Dziennik pracy z AI

Projekt powstaje z pomocą AI i trzeba umieć to pokazać. Po każdym etapie agent dopisuje tu krótki wpis. Na końcu z tego pliku powstaje sekcja README „Jak używałem AI".

Zasady:

- Krótko i konkretnie. Fakty, bez reklamy.
- Zapisuj też to, co poszło źle, i to, co poprawił człowiek.
- Nowe wpisy na dole.

## Szablon

```
### Etap N: tytuł (RRRR-MM-DD)

- **Narzędzie i model:**
- **Co zrobiło AI:**
- **Co zdecydował albo poprawił człowiek:**
- **Co poszło źle i jak to naprawiono:**
- **Jak sprawdzono wynik:** komendy, testy, próba ręczna
```

## Wpisy

### Etap 0: pomysł i projekt (2026-11-11)

- **Narzędzie i model:** Claude (czat).
- **Co zrobiło AI:** zaproponowało pomysły na aplikację; sprawdziło w sieci dostępność API (PKP PLK, Google Routes, Bolt, Uber) i istniejące rozwiązania (czat w Mapach Google, Bolt w ChatGPT); zaprojektowało reguły domenowe, kontrakt API i podział na etapy; napisało dokumenty z tego folderu.
- **Co zdecydował albo poprawił człowiek:** temat (plan zajęć, dojazdy, CO2), stos (Next.js, Spring Boot, mała baza SQL), brak logowania, Google Routes jako źródło tras, import ze zrzutu ekranu, darmowy dostawca AI. Zgłosił przypadek „wyszedłem później i pokazuje mi autobus, na który już nie zdążę", z którego powstała reguła z sekcji 6 w `docs/domain-rules.md`.
- **Co poszło źle i jak to naprawiono:** pierwszy pomysł (czat „o której wyjść") okazał się łatwy do zastąpienia przez istniejące asystenty. Kierunek zmieniono na aplikację, która zna plan i pamięta wybory.
- **Jak sprawdzono wynik:** informacje o API, cenach i limitach potwierdzono w oficjalnej dokumentacji (linki w `docs/integrations/`). Kod jeszcze nie istnieje.
