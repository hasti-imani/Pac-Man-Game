package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Tilemap-Klasse (Model)
 * ----------------------------------------------------------
 * Speichert das Labyrinth als 2D-Gitter (Grid).
 *
 * Kachel-Werte:
 *   0 = freier Weg (kein Pellet)
 *   1 = Wand
 *   2 = Pellet
 *
 * Koordinaten-System:
 *   x = Spalte (horizontal), y = Zeile (vertikal)
 *   Zugriff: grid[y][x]
 *
 * Gehört ins MODEL: Nur Kartendaten und Abfragemethoden, keine Grafik.
 */
public class Tilemap {

    // --- Das Gitter als 2D-Array ---
    private final int[][] grid;

    // --- Größe des Gitters ---
    private final int rows; // Anzahl Zeilen  (y-Achse)
    private final int cols; // Anzahl Spalten (x-Achse)

    /**
     * Standard-Labyrinth.
     * Legende: 1=Wand, 2=Pellet, 0=freier Weg
     * Kann später durch eine Datei ersetzt werden.
     */
    private static final int[][] DEFAULT_MAP = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 2, 2, 2, 1, 2, 2, 2, 2, 1},
        {1, 2, 1, 2, 1, 2, 1, 1, 2, 1},
        {1, 2, 1, 2, 2, 2, 1, 2, 2, 1},
        {1, 2, 1, 1, 1, 0, 1, 2, 1, 1},
        {1, 2, 2, 2, 0, 0, 2, 2, 2, 1},
        {1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
        {1, 2, 2, 2, 2, 2, 2, 2, 2, 1},
        {1, 2, 1, 1, 1, 1, 1, 1, 2, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    /**
     * Standard-Konstruktor: Lädt das eingebaute Default-Labyrinth.
     */
    public Tilemap() {
        this(DEFAULT_MAP);
    }

    /**
     * Konstruktor mit eigenem Grid (z.B. aus einer Datei geladen).
     * @param grid  Das 2D-Array mit Kachel-Werten
     */
    public Tilemap(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    /**
     * Prüft, ob eine Kachel eine Wand ist.
     * Gibt auch true zurück, wenn die Koordinaten außerhalb des Spielfelds liegen
     * (damit Gegner/PacMan nicht aus der Karte laufen können).
     *
     * @param x  Spalte
     * @param y  Zeile
     * @return   true = Wand oder außerhalb, false = begehbar
     */
    public boolean isWall(int x, int y) {
        // Grenzen prüfen → außerhalb gilt als Wand
        if (x < 0 || y < 0 || x >= cols || y >= rows) return true;
        return grid[y][x] == 1;
    }

    /**
     * Prüft, ob an einer bestimmten Position ein Pellet liegt.
     *
     * @param x  Spalte
     * @param y  Zeile
     * @return   true = Pellet vorhanden
     */
    public boolean hasPellet(int x, int y) {
        // Grenzen prüfen → außerhalb hat kein Pellet
        if (x < 0 || y < 0 || x >= cols || y >= rows) return false;
        return grid[y][x] == 2;
    }

    /**
     * Entfernt ein Pellet von der Karte (setzt Kachel auf 0).
     * Wird aufgerufen, nachdem GameState das Pellet registriert hat.
     *
     * @param x  Spalte
     * @param y  Zeile
     */
    public void removePellet(int x, int y) {
        // Nur entfernen, wenn dort wirklich ein Pellet ist
        if (hasPellet(x, y)) {
            grid[y][x] = 0;
        }
    }

    /**
     * Gibt alle Pellet-Positionen als Liste zurück.
     * Wird beim Start verwendet, um GameState zu initialisieren.
     *
     * @return Liste von int[]{x, y} für jedes Pellet
     */
    public List<int[]> getAllPellets() {
        List<int[]> result = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == 2) {
                    // Achtung: col = x, row = y
                    result.add(new int[]{col, row});
                }
            }
        }
        return result;
    }

    // --- Getter ---

    /** @return Anzahl der Zeilen (Höhe der Karte) */
    public int getRows() { return rows; }

    /** @return Anzahl der Spalten (Breite der Karte) */
    public int getCols() { return cols; }

    /**
     * Gibt den Kachel-Wert an einer Position zurück.
     * @param x  Spalte
     * @param y  Zeile
     * @return   0 = frei, 1 = Wand, 2 = Pellet
     */
    public int getTile(int x, int y) { return grid[y][x]; }

    /**
     * Gibt die Karte als Text auf der Konsole aus (nur für Debugging).
     * █ = Wand, · = Pellet, Leerzeichen = freier Weg
     */
    public void printMap() {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell == 1 ? "█" : cell == 2 ? "·" : " ");
            }
            System.out.println();
        }
    }
}
