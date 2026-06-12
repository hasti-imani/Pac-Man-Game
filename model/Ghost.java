package model;

import java.util.Random;

/**
 * Ghost-Klasse (Model)
 * ----------------------------------------------------------
 * Speichert Position und Bewegungslogik eines Geists.
 * Der Ghost bewegt sich zufällig durch das Labyrinth.
 *
 * Gehört ins MODEL: Nur Daten und Bewegungsregeln, keine Grafik.
 */
public class Ghost {

    // --- Richtungen als Enum ---
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    // --- Position auf der Tilemap ---
    private int x;
    private int y;

    // --- Aktuelle Bewegungsrichtung ---
    private Direction direction;

    // --- Zufallsgenerator für Bewegung ---
    private final Random random = new Random();

    /**
     * Konstruktor: Ghost an Startposition platzieren.
     * @param startX  Startspalte auf der Tilemap
     * @param startY  Startzeile auf der Tilemap
     */
    public Ghost(int startX, int startY) {
        this.x         = startX;
        this.y         = startY;
        // Zufällige Startrichtung
        this.direction = Direction.values()[random.nextInt(4)];
    }

    /**
     * Bewegt den Ghost einen Schritt.
     *  - Mit 20% Wahrscheinlichkeit wechselt er zufällig die Richtung.
     *  - Bei Wandkollision wird sofort eine neue Richtung gewählt.
     *
     * @param tilemap  Die Karte – wird für Wandprüfung benötigt
     */
    public void move(Tilemap tilemap) {

        // --- Zufällig Richtung wechseln (ca. 20% Chance) ---
        if (random.nextInt(5) == 0) {
            direction = Direction.values()[random.nextInt(4)];
        }

        // --- Nächste Position berechnen ---
        int newX = x;
        int newY = y;

        switch (direction) {
            case UP    -> newY--;
            case DOWN  -> newY++;
            case LEFT  -> newX--;
            case RIGHT -> newX++;
        }

        // --- Nur bewegen, wenn keine Wand im Weg ---
        if (!tilemap.isWall(newX, newY)) {
            x = newX;
            y = newY;
        } else {
            // Wand getroffen → neue Zufallsrichtung für den nächsten Schritt
            direction = Direction.values()[random.nextInt(4)];
        }
    }

    // --- Getter ---

    /** @return Aktuelle X-Position (Spalte) */
    public int getX() { return x; }

    /** @return Aktuelle Y-Position (Zeile) */
    public int getY() { return y; }

    /** @return Aktuelle Bewegungsrichtung */
    public Direction getDirection() { return direction; }

    @Override
    public String toString() {
        return "Ghost[x=" + x + ", y=" + y + ", dir=" + direction + "]";
    }
}
