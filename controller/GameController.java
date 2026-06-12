package controller;

import model.GameState;
import model.Ghost;
import model.PacMan;
import model.Tilemap;
import view.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * GameController-Klasse (Controller)
 * ----------------------------------------------------------
 * Der Controller ist das Bindeglied zwischen Model und View.
 *
 * Aufgaben des Controllers:
 *   1. Spielschleife steuern (Tick-basiert)
 *   2. Eingaben vom Spieler entgegennehmen und ans Model weitergeben
 *   3. Kollisionen zwischen PacMan, Geistern und Pellets prüfen
 *   4. View aktualisieren (repaint anfordern)
 *
 * WICHTIG – MVC-Regeln:
 *   ✅ Controller darf Model-Methoden aufrufen
 *   ✅ Controller darf View aktualisieren
 *   ❌ Controller zeichnet NICHTS selbst
 *   ❌ Controller speichert keine Spielerdaten (das macht das Model)
 */
public class GameController {

    // --- MVC-Komponenten ---
    private final Tilemap   tilemap;    // Model: Labyrinth
    private final PacMan    pacMan;     // Model: Spielfigur
    private final GameState gameState;  // Model: Spielzustand
    private final List<Ghost> ghosts;  // Model: Geister
    private GameView view;             // View: wird später gesetzt

    // --- Spielgeschwindigkeit ---
    // Gibt an, wie viele Millisekunden zwischen jedem Spielschritt liegen
    private static final int TICK_MS = 200;

    // --- Spielschleife ---
    private Thread gameLoop; // Hintergrundthread für den Spielablauf
    private boolean running;  // true = Spiel läuft gerade

    /**
     * Konstruktor: Erstellt alle Model-Objekte und startet das Spiel vor.
     */
    public GameController() {
        // --- Tilemap erstellen (Standard-Labyrinth) ---
        this.tilemap = new Tilemap();

        // --- PacMan auf Startposition setzen (Spalte 1, Zeile 1) ---
        this.pacMan = new PacMan(1, 1);

        // --- GameState mit allen Pellets aus der Karte initialisieren ---
        this.gameState = new GameState(tilemap.getAllPellets());

        // --- Geister erstellen und auf Startpositionen setzen ---
        this.ghosts = new ArrayList<>();
        ghosts.add(new Ghost(5, 4)); // Geist 1: Mitte der Karte
        ghosts.add(new Ghost(8, 7)); // Geist 2: andere Ecke
    }

    /**
     * Verbindet den Controller mit der View.
     * Muss aufgerufen werden, bevor startGame() gestartet wird.
     *
     * @param view  Die GameView, die das Spiel anzeigt
     */
    public void setView(GameView view) {
        this.view = view;
    }

    /**
     * Startet die Spielschleife in einem eigenen Thread.
     * Der Thread ruft in regelmäßigen Abständen tick() auf.
     */
    public void startGame() {
        running  = true;

        // Neuen Hintergrundthread erstellen
        gameLoop = new Thread(() -> {
            while (running && !gameState.isGameOver()) {
                tick(); // Einen Spielschritt ausführen

                try {
                    // Kurze Pause zwischen den Schritten (Spielgeschwindigkeit)
                    Thread.sleep(TICK_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Thread-Unterbrechung korrekt behandeln
                    break;
                }
            }
        });

        gameLoop.setDaemon(true); // Thread endet automatisch wenn das Hauptprogramm endet
        gameLoop.start();
    }

    /**
     * Stoppt die Spielschleife (z.B. bei Game Over oder Fensterschließen).
     */
    public void stopGame() {
        running = false;
        if (gameLoop != null) {
            gameLoop.interrupt(); // Thread sofort unterbrechen
        }
    }

    /**
     * Ein Spielschritt (Tick): PacMan bewegen, Geister bewegen, Kollisionen prüfen.
     * Wird von der Spielschleife aufgerufen.
     */
    private void tick() {
        // --- 1) PacMan bewegen ---
        pacMan.move(tilemap);

        // --- 2) Pellet-Kollision prüfen ---
        checkPelletCollision();

        // --- 3) Alle Geister bewegen ---
        for (Ghost ghost : ghosts) {
            ghost.move(tilemap);
        }

        // --- 4) Geist-Kollision prüfen ---
        checkGhostCollision();

        // --- 5) View aktualisieren (neu zeichnen lassen) ---
        if (view != null) {
            view.refresh();
        }
    }

    /**
     * Prüft, ob PacMan auf einem Pellet steht und sammelt es ein.
     * Aktualisiert GameState UND Tilemap.
     */
    private void checkPelletCollision() {
        int px = pacMan.getX();
        int py = pacMan.getY();

        // GameState prüft die Pellet-Liste und erhöht den Score
        if (gameState.collectPellet(px, py)) {
            // Kachel auf der Karte ebenfalls entfernen (für korrekte Darstellung)
            tilemap.removePellet(px, py);
        }
    }

    /**
     * Prüft, ob PacMan und ein Geist auf derselben Kachel stehen.
     * Wenn ja: Leben verlieren.
     */
    private void checkGhostCollision() {
        int px = pacMan.getX();
        int py = pacMan.getY();

        for (Ghost ghost : ghosts) {
            // Gleiche Position = Kollision
            if (ghost.getX() == px && ghost.getY() == py) {
                gameState.loseLife();

                // Optional: PacMan zurück auf Startposition setzen
                // (Erweiterung für später – braucht reset()-Methode in PacMan)
                break; // Nur einmal Leben verlieren pro Tick
            }
        }
    }

    /**
     * Verarbeitet eine Tasteneingabe vom Spieler.
     * Wird von der View aufgerufen, wenn eine Taste gedrückt wird.
     *
     * @param dir  Gewünschte neue Richtung für PacMan
     */
    public void handleInput(PacMan.Direction dir) {
        // Eingabe nur verarbeiten wenn das Spiel noch läuft
        if (!gameState.isGameOver()) {
            pacMan.setNextDirection(dir); // Model informieren
        }
    }

    // --- Getter für die View (View liest nur, ändert nichts) ---

    /** @return Die Tilemap (für Labyrinth-Darstellung) */
    public Tilemap getTilemap()     { return tilemap;   }

    /** @return PacMan-Objekt (für Position und Richtung) */
    public PacMan getPacMan()       { return pacMan;    }

    /** @return GameState (für Score, Leben, Game-Over) */
    public GameState getGameState() { return gameState; }

    /** @return Liste aller Geister (für deren Positionen) */
    public List<Ghost> getGhosts()  { return ghosts;   }
}
