# DHBW Raumsuche
## Projektbegründung
Das Projekt soll Studierenden der DHBW helfen, schnell freie Lernräume auf dem Campus zu finden. Obwohl die Stundenpläne der Kurse zugänglich sind, zeigen sie nicht an, wann ein Raum tatsächlich frei oder belegt ist. Die App schließt diese Lücke, indem sie aktuelle Raumverfügbarkeiten direkt anzeigt.

## Projektumfeld
Das Projekt ist eine Android-App für DHBW-Studierende. Sie nutzt die Stundenplandaten im iCal-Format, die über einen Proxy-Server zwischengespeichert werden. Die Standortbestimmung erfolgt über die GPS-API des Smartphones, um den passenden Standort der Räume anzuzeigen.

## Schnittstellen
Die App greift auf zwei Schnittstellen zu: die GPS-API des Smartphones für die Standorterkennung und die iCal-Daten des Stundenplans, die über einen Proxy aufbereitet werden. So können die Raumverfügbarkeiten aktuell und verlässlich angezeigt werden.

## Projektziel
Ziel ist es, eine einfache App bereitzustellen, mit der Studierende schnell freie Lernräume finden können. Die App soll Räume filtern und sortieren, bevorzugte Gebäude berücksichtigen und den Standort automatisch anpassen.

# Anwendung
## 1. Zielbestimmung
Studierende der DHBW sollen in der Lage sein, mithilfe einer Android-App schnell und einfach verfügbare Räume zum Lernen zwischen Vorlesungen zu finden.

## 2. Produkteinsatz
Die App wird dazu dienen, freie Lernräume an der DHBW anzuzeigen und kann von den Studierenden ohne Anmeldung verwendet werden. Zielgruppe der App sind DHBW-Studierende, die in ihren Pausen nach verfügbaren Lernräumen suchen.

## 3. Produktfunktionen
/FO1/ Anzeigen einer Liste von aktuell verfügbaren Räumen. \
/FO2/ Filtern und Sortieren der Räume nach Gebäude und Verfügbarkeit. \
/FO3/ Automatische Filterung der Räume nach aktuellem Standort (Gebäude) mithilfe von GPS. \
/FO4/ Möglichkeit zur Auswahl von bevorzugten Gebäuden für die Filterung und Sortierung. \
/FO5/ Hinzufügen von häufig genutzten Räumen als Favoriten. \
/FO6/ Unterstützung für Light- und Darkmode der Benutzeroberfläche.


### Optional
/FO7/ Herausfiltern von Pausen in belegten Räumen (wenn Raum laut Plan frei, aber Kurs im Raum bleibt). \
/FO8/ Bereitstellung eines Widgets zur schnellen Anzeige verfügbarer Räume.

## 4. Produktdaten
/FD10/ Die App speichert bevorzugte Filtereinstellungen und favorisierte Räume der Nutzer