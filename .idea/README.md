# Smart Home
Aplikacja sterująca modułem Arduino (domyślnie ESP32) za pomocą bluetooth.

## Omówienie projektu: 
Prosta aplikacja sterująca modułem arduino - pobierająca dane z czujników modułu i wyświetlająca dane w aplikacji za pomocą sieci wifi.
Domyślnie - aplikacja będzie komunikowała się z dwoma modułami - jednym obsługującym czujniki (aplikacja zbiera i wyświetla dane - przechowuje je lokalnie),
oraz drugim obsługującym niewielki wyświetlacz (dane do wyświetlacza muszą zostać przekonwertowane na tablicę wartości boolean poszczególnych pikseli i wysłane w takiej formie do wyświetlacza)

## Użyte Technologie (do aplikacji):
- Android Studio
- Biblioteki: Jetpack Compose, Viewmodel Stateflow, Room, https://github.com/PhilJay/MPAndroidChart
- Architektura: MVMM

## Funkcje podstawowe:
- Połączenie aplikacji z modułami arduino
- Lokalne przechowywanie danych uzyskanych z modułów
- Wyświetlanie posiadanych danych w formie wykresów
- Kontrolowanie notatek wyświetlanych na jednym z modułów (system notatek pozwalający na przesłanie notatki do modułu z wyświetlaczem)

## Plan pracy
- 04.03.2025-08.04.2025 - Stworzenie podstawowej aplikacji i połączenie jej z przynajmniej jednym z modułów
- 08.04.2025-06.05.2025 - Implementacja systemu zbierania danych, implementacja systemu kontroli wyświetlacza (notatek)
- 06.05.2025-03.06.2025 - Prace nad kwestiami estetycznymi aplikacji, przedstawieniem zebranych danych w przystępnej formie.

## Link do projektu: https://github.com/JaroslawBernacki/Smart_Home_Arduino
