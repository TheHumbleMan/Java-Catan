# CATAN Board Positioning Fix

## Problem identifiziert:
1. **Hexagon-Tiles verschoben**: Die Tiles sind nach oben-links verschoben im Vergleich zu Vertices/Edges
2. **Zu wenig Abstand**: Die Hexagons sind zu nah beieinander, sodass Straßen und Städte nicht dazwischen passen

## Lösung:
1. **Einheitliches Koordinatensystem**: Alle Elemente verwenden dasselbe Berechnungsschema
2. **Vergrößerter Abstand**: HEX_SPACING_MULTIPLIER = 1.8 für bessere Sichtbarkeit
3. **Korrekte Zentrierung**: Hexagon-Gruppen werden richtig zentriert positioniert

## Änderungen:
- HEX_SIZE: 50.0 (vergrößert)
- HEX_SPACING_MULTIPLIER: 1.8 (vergrößert)
- BOARD_CENTER: 400x350 (angepasst)
- Vereinheitlichte Berechnungsformeln für alle Elemente
