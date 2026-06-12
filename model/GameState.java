package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GameState-Klasse (Model)
 * ----------------------------------------------------------
 * Verwaltet den gesamten Spielzustand:
 *   - Punktestand (Score)
 *   - Leben des Spielers
 *   - Pellet-Liste
 *   - Game-Over / Gewonnen Flag
 *
 * Gehört ins MODEL: Nur Daten und Spielregeln, keine Grafik.
 */
public class GameState {

    // --- Konstanten ---
    private static final int PELLET_POINTS = 10;   // Punkte pro Pellet
    private static final int GHOST_POINTS  = 200;  // Punkte pro gegessenem Ghost
    private static final int START_LIVES   = 3;    // Startleben

    // --- Spielzustand ---
    private int score;
    private int lives;
    private boolean gameOver;
    private boolean won;                           // Getrennt von gameOver für klare Unterscheidung

    // --- Pellets: jedes Pellet = int[]{x, y} ---
    private final List<int[]> pellets;

    /**
     * Konstruktor: Startzustand mit allen Pellets der Karte initialisieren.
     * @param initialPellets  Alle Pellet-Positionen aus der Tilemap
     */
    public GameState(List<int[]> initialPellets) {
        this.score    = 0;
        this.lives    = START_LIVES;
        this.gameOver = false;
        this.won      = false;
        this.pellets  = new ArrayList<>(initialPellets); // Kopie erstellen
    }

    /**
     * Prüft, ob PacMan auf einem Pellet steht und sammelt es ein.
     * BUGFIX: Iterator statt for-each, um ConcurrentModificationException zu vermeiden.
     *
     * @param pacX  PacMans X-Position
     * @param pacY  PacMans Y-Position
     * @return      true = Pellet eingesammelt, false = kein Pellet an dieser Stelle
     */
    public boolean collectPellet(int pacX, int pacY) {
        // Iterator verwenden, damit wir während der Schleife sicher entfernen können
        Iterator<int[]> it = pellets.iterator();
        while (it.hasNext()) {
            int[] pellet = it.next();
            if (pellet[0] == pacX && pellet[1] == pacY) {
                it.remove();              // Sicheres Entfernen über den Iterator
                score += PELLET_POINTS;
                checkWin();              // Nach jedem Pellet prüfen ob gewonnen
                return true;
            }
        }
        return false;
    }

    /**
     * PacMan hat einen Ghost berührt → Leben verlieren.
     * Wenn keine Leben mehr übrig: Game Over setzen.
     */
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            lives    = 0;
            gameOver = true;
        }
    }

    /**
     * PacMan hat einen Ghost gefressen (Power-Up-Modus).
     * Erhöht den Punktestand um GHOST_POINTS.
     */
    public void eatGhost() {
        score += GHOST_POINTS;
    }

    /**
     * Prüft intern ob alle Pellets eingesammelt wurden → Sieg.
     * Wird nach jedem collectPellet() aufgerufen.
     */
    private void checkWin() {
        if (pellets.isEmpty()) {
            won      = true;
            gameOver = true; // Spiel beendet (Sieg)
        }
    }

    // --- Getter ---

    /** @return Aktueller Punktestand */
    public int getScore() { return score; }

    /** @return Verbleibende Leben */
    public int getLives() { return lives; }

    /** @return true = Spiel ist vorbei (Niederlage ODER Sieg) */
    public boolean isGameOver() { return gameOver; }

    /** @return true = Spieler hat gewonnen (alle Pellets gesammelt) */
    public boolean hasWon() { return won; }

    /**
     * Gibt eine Kopie der Pellet-Liste zurück.
     * Kopie schützt die interne Liste vor ungewollten Änderungen von außen.
     * @return Unveränderliche Kopie der Pellets
     */
    public List<int[]> getPellets() { return new ArrayList<>(pellets); }

    @Override
    public String toString() {
        return "GameState[score=" + score + ", lives=" + lives
                + ", pellets=" + pellets.size() + ", gameOver=" + gameOver
                + ", won=" + won + "]";
    }
}
