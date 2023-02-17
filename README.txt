Po odpaleniu clienta gracz music wpisac komende ready
Po dołączeniu co najmniej 2 graczy serwer zaczyna rozgrywke.
W trakcie rozgrywki gracz moze wpisac komende ready:
Jeśli nie jego ruch:
\actions - wyswietla akcje ktore moze wykonac gracz
\my_hand - wyswietla karty w rece
\balance - wyswietla stan konta
\pool - wyswietla stan puli
Jesli jest jego ruch to dodatkowo:
\fold - aby spasowac
\bet x - aby obstawic x
\call - aby dobic do stawki
\all_in - aby zagrac wszystko

Po pierszej turze następuje wymiana kart, w której gracze którzy nie spasowali mogą wymienić karty komendasmi:
\swap a,b,c - aby wymienic karty o numerach a,b,c
\end_swap - aby zakonczyc wymiane bez wymiany kart

Po 3 turach obstawiania runda sie konczy i nastepuje sprawdzenie kto wygral, pokazanie tego i przejscie do nastepnej rundy.


Program uruchamia sie wpisujac w konsoli w module server:
java -jar nazwa_jara_servera.jar

Nastepnie w module client dla 2-4 graczy:
java -jar nazwa_jara_clienta.jar

Serwer moze wysylac komunikaty na podstawie wpisywanych komend i dodatkowo informacje gdy:
gracz dolączy i nie wpisze jeszcze \ready
informacje o ruchach innych graczy
informacje o końcu rundy i tury i o tym kto wygral
informacje o tym kto wygral cala gre
informacje o wymianie kart
