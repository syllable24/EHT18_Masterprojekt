# EHT18_Masterprojekt
EHT18 Masterprojekt: eRezept

Konzept: Diese Android-App soll es ermöglichen, mittels einer SMS eine Dauermedikationsliste 
einlesen zu können und basierend auf dieser Liste an die Medikationseinnahme erinnert zu werden.

Ziele: 
- Bestimmung eines Standards, der zur Übermittelung der Dauermedikationsliste verwendet werden soll.
  (Aktuelle Überlegung: HL7v3). 
- Die App soll die standardisierte SMS einlesen und deren Inhalte anzeigen können.
- Die App soll, basierend auf der eingelesenen Dauermedikationsliste, den User zur verordneten Zeit
  (Morgens, Mittags, Abends, Nachts) mittels Push-Nachrichten an die Einnahme der Medikation erinnern.
 
Optionale Ziele (Nice to have):
- Die Erstellung der Dauermedikationsliste soll mittels einer Web-App erfolgen können.
- Die Inhalte der SMS sollen verschlüsselt werden. Die App soll die verschlüsselte SMS 
  wieder entschlüsseln können.
- Die Push-Nachrichten sollen durch den User bestätigt werden. Der Bestätigungszeitpunkt soll
  protokolliert werden.    
- Die protokollierten Einnahmen sollen dem Arzt übermittelt werden können.
  (Methodik der Übermittelung muss noch festgelegt werden)
- Mittels der Information über die Stückzahl der verschriebenen Medikamente in der 
  Dauermedikationsliste sollen die Medikationsvorräte des Nutzer verwaltet werden.
  Sobald diese zur Neige gehen, soll der Nutzer, über eine Push-Nachricht, darauf
  hingewiesen werden. 
- Dem standardisierten Datensatz der Dauermedikationsliste sollen Informationen über die Ordinations-
  Öffnungszeiten und die Urlaubszeiten des Arztes sowie Notfallinformationen (genaue Inhalte müssen 
  definiert werden) beigefügt werden.
  
Nicht-Ziele:
- Die  Anbindung an ELGA ist nicht vorhergesehen.
- Der Nutzer der App darf die Dauermedikationsliste nicht selbst ändern.
