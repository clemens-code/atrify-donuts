Meine Lösung zu der Aufgabe des Donut Shops. 

Ein paar kurze Informationen:
- Der tomcat Server startet auf Port 8080. 
- dieser kann in der application.yaml verändert werden
- es gibt wie besprochen keine Authentifizierung 
- Premium-Nutzer erhalten vor allen anderen ihre Bestellung 

Endpunkte haben den Standardpfad /api/v1

- der "normale" Bestellungsendpunkt mit dem Standardpfad /order
  - POST /add {"clientId": int, "quantity": int} --- anlegen von Bestellungen 
  - DELETE /{clientId} --- löscht eine Bestellung, falls vorhanden 
  - GET /{clientId} --- liefert eine Bestellung, falls vorhanden
- der Endpunkt um Bestellungen abzuholen mit dem Standardpfad /cart
  - GET /next --- liefert die nächsten Bestellungen
- der Managementendpunkt mit dem Pfad /management
  - GET / --- liefert alle Bestellungen in der Warteschlange