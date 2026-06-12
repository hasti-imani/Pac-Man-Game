package model;

/**
 * PacMan-Klasse (Model)
 * ----------------------------------------------------------
 * Speichert die Position, Richtung und Bewegungslogik von PacMan.
 * Diese Klasse gehört ins MODEL: Sie kennt nur Daten und Regeln,
 * aber NICHTS über Grafik oder Tasteneingaben.
 */
public class PacMan {

    // --- Richtungen als Enum (übersichtlicher als int-Konstanten) ---
    public enum Direction { UP, DOWN, LEFT, RIGHT, NONE }

    // --- Position auf der Tilemap (in Kachel-Koordinaten, nicht Pixel) ---
    private int x;
    private int y;

    // --- Aktuelle Bewegungsrichtung ---
    private Direction direction;

    // --- Nächste gewünschte Richtung (vom Spieler eingegeben) ---
    // Wird erst übernommen, wenn der Weg frei ist (klassisches Pac-Man-Feeling)
    private Direction nextDirection;

    /**
     * Konstruktor: Startposition setzen
     * @param startX  Startspalte auf der Tilemap
     * @param startY  Startzeile auf der Tilemap
     */
    public PacMan(int startX, int startY) {
        this.x             = startX;
        this.y             = startY;
        this.direction     = Direction.NONE;   // Steht still beim Start
        this.nextDirection = Direction.NONE;
    }

    /**
     * Bewegt PacMan einen Schritt in die aktuelle Richtung.
     * Vorher wird geprüft, ob die gewünschte nächste Richtung möglich ist.
     *
     * @param tilemap  Die Karte – wird für Kollisionsprüfung benötigt
     */
    public void move(Tilemap tilemap) {

        // --- 1) Gewünschte Richtung übernehmen, falls der Weg frei ist ---
        if (canMove(nextDirection, tilemap)) {
            direction = nextDirection;
        }

        // --- 2) In aktueller Richtung bewegen, falls möglich ---
        if (!canMove(direction, tilemap)) {
            return; // Wand blockiert → PacMan bleibt stehen
        }

        // --- 3) Position anpassen ---
        switch (direction) {
            case UP    -> y--;
            case DOWN  -> y++;
            case LEFT  -> x--;
            case RIGHT -> x++;
            case NONE  -> { /* kein Schritt */ }
        }
    }

    /**
     * Prüft, ob PacMan sich in einer bestimmten Richtung bewegen kann
     * (ohne dabei in eine Wand zu laufen).
     *
     * @param dir      Die zu prüfende Richtung
     * @param tilemap  Die Karte mit Wandinformationen
     * @return         true = Weg frei, false = Wand oder Spielfeldrand
     */
    private boolean canMove(Direction dir, Tilemap tilemap) {
        // Nächste Position berechnen
        int newX = x;
        int newY = y;

        switch (dir) {
            case UP    -> newY--;
            case DOWN  -> newY++;
            case LEFT  -> newX--;
            case RIGHT -> newX++;
            case NONE  -> { return false; } // NONE = kein Move möglich
        }

        // Wandprüfung über die Tilemap
        return !tilemap.isWall(newX, newY);
    }

    /**
     * Setzt die gewünschte nächste Richtung (vom Controller aufgerufen).
     * Die Richtung wird erst beim nächsten move()-Aufruf übernommen.
     *
     * @param dir  Gewünschte Richtung
     */
    public void setNextDirection(Direction dir) {
        this.nextDirection = dir;
    }

    // --- Getter ---

    /** @return Aktuelle X-Position (Spalte) */
    public int getX() { return x; }

    /** @return Aktuelle Y-Position (Zeile) */
    public int getY() { return y; }

    /** @return Aktuell aktive Bewegungsrichtung */
    public Direction getDirection() { return direction; }

    /** @return Gewünschte nächste Richtung (noch nicht bestätigt) */
    public Direction getNextDirection() { return nextDirection; }

    @Override
    public String toString() {
        return "PacMan[x=" + x + ", y=" + y + ", dir=" + direction + "]";
    }
}
