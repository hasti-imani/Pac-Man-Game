package view;

import controller.GameController;
import model.Ghost;
import model.PacMan;
import model.Tilemap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * SwingGameView-Klasse (View)
 * ----------------------------------------------------------
 * Zeichnet das Spiel mit Java Swing auf den Bildschirm.
 *
 * Aufgaben der View:
 *   1. Labyrinth (Tilemap) zeichnen
 *   2. PacMan zeichnen
 *   3. Geister zeichnen
 *   4. Score und Leben anzeigen
 *   5. Game-Over / Gewonnen-Bildschirm anzeigen
 *   6. Tasteneingaben entgegennehmen und an Controller weitergeben
 *
 * WICHTIG – MVC-Regeln:
 *   ✅ View liest Daten vom Controller (über Getter)
 *   ✅ View gibt Eingaben an Controller weiter
 *   ❌ View ändert NIEMALS direkt das Model
 *   ❌ View enthält KEINE Spiellogik
 */
public class SwingGameView extends JPanel implements GameView {

    // --- Verbindung zum Controller ---
    private final GameController controller;

    // --- Darstellungsgrößen ---
    private static final int TILE_SIZE = 40; // Kachelgröße in Pixel

    // --- Farben ---
    private static final Color COLOR_WALL       = new Color(0, 0, 180);   // Blau wie im Original
    private static final Color COLOR_PELLET     = new Color(255, 220, 180); // Helles Beige
    private static final Color COLOR_PACMAN     = Color.YELLOW;
    private static final Color COLOR_GHOST      = Color.RED;
    private static final Color COLOR_BACKGROUND = Color.BLACK;
    private static final Color COLOR_TEXT       = Color.WHITE;

    /**
     * Konstruktor: Panel einrichten und Tastatursteuerung hinzufügen.
     * @param controller  Der GameController (gibt Zugriff auf alle Model-Daten)
     */
    public SwingGameView(GameController controller) {
        this.controller = controller;

        // --- Panel-Größe berechnen (Kacheln × Pixelgröße) ---
        Tilemap map = controller.getTilemap();
        int width  = map.getCols() * TILE_SIZE;
        int height = map.getRows() * TILE_SIZE + 40; // +40 für HUD (Score/Leben)

        setPreferredSize(new Dimension(width, height));
        setBackground(COLOR_BACKGROUND);
        setFocusable(true); // Nötig damit das Panel Tastatureingaben bekommt

        // --- Tastatursteuerung ---
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyInput(e.getKeyCode());
            }
        });
    }

    /**
     * Verarbeitet Tastatureingaben und leitet sie an den Controller weiter.
     * PFEILTASTEN und WASD werden unterstützt.
     *
     * @param keyCode  Java-Tastencode (aus KeyEvent)
     */
    private void handleKeyInput(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> controller.handleInput(PacMan.Direction.UP);
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> controller.handleInput(PacMan.Direction.DOWN);
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> controller.handleInput(PacMan.Direction.LEFT);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> controller.handleInput(PacMan.Direction.RIGHT);
        }
    }

    /**
     * Wird vom Controller aufgerufen, damit die View neu zeichnet.
     * repaint() löst automatisch paintComponent() aus.
     */
    @Override
    public void refresh() {
        // repaint() ist thread-sicher und kann aus jedem Thread aufgerufen werden
        SwingUtilities.invokeLater(this::repaint);
    }

    /**
     * Haupt-Zeichenmethode von Swing.
     * Wird automatisch aufgerufen wenn repaint() ausgelöst wird.
     *
     * @param g  Graphics-Objekt von Swing (Zeichenwerkzeug)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Hintergrund löschen

        // Für bessere Darstellung: Graphics2D verwenden
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- In richtiger Reihenfolge zeichnen ---
        drawTilemap(g2);   // 1. Labyrinth (unterste Schicht)
        drawPellets(g2);   // 2. Pellets
        drawPacMan(g2);    // 3. PacMan
        drawGhosts(g2);    // 4. Geister (oberste Schicht)
        drawHUD(g2);       // 5. HUD: Score und Leben
        drawGameOver(g2);  // 6. Game-Over / Sieg (nur wenn nötig)
    }

    /**
     * Zeichnet das Labyrinth: Wände als blaue Rechtecke, freie Felder bleiben schwarz.
     */
    private void drawTilemap(Graphics2D g) {
        Tilemap map = controller.getTilemap();

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                int tile = map.getTile(col, row); // getTile(x=col, y=row)

                if (tile == 1) {
                    // Wand zeichnen
                    g.setColor(COLOR_WALL);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                    // Leichte Umrandung für 3D-Effekt
                    g.setColor(COLOR_WALL.brighter());
                    g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
                }
                // Tile 0 und 2 bleiben schwarz (Hintergrundfarbe)
            }
        }
    }

    /**
     * Zeichnet alle Pellets als kleine Kreise auf den freien Feldern.
     */
    private void drawPellets(Graphics2D g) {
        Tilemap map = controller.getTilemap();

        g.setColor(COLOR_PELLET);

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                if (map.hasPellet(col, row)) {
                    // Kleines Pellet in der Mitte der Kachel zeichnen
                    int pelletSize = TILE_SIZE / 5; // Pellet = 1/5 der Kachelgröße
                    int offsetX    = (TILE_SIZE - pelletSize) / 2; // Zentriert
                    int offsetY    = (TILE_SIZE - pelletSize) / 2;
                    g.fillOval(
                        col * TILE_SIZE + offsetX,
                        row * TILE_SIZE + offsetY,
                        pelletSize,
                        pelletSize
                    );
                }
            }
        }
    }

    /**
     * Zeichnet PacMan als gelben Kreis mit einem "Mund" (Tortenstück).
     */
    private void drawPacMan(Graphics2D g) {
        PacMan pac = controller.getPacMan();

        // Pixel-Position berechnen (Kachel-Koordinaten × Kachelgröße)
        int px = pac.getX() * TILE_SIZE;
        int py = pac.getY() * TILE_SIZE;

        // Startwinkel des Mundes je nach Richtung
        int mouthAngle = getMouthAngle(pac.getDirection());

        g.setColor(COLOR_PACMAN);
        // fillArc: x, y, breite, höhe, startwinkel, bogenmaß (360 - Mundöffnung)
        g.fillArc(px + 2, py + 2, TILE_SIZE - 4, TILE_SIZE - 4, mouthAngle + 30, 300);
    }

    /**
     * Gibt den Startwinkel für PacMans Mund je nach Bewegungsrichtung zurück.
     * (Java: 0° = rechts, 90° = oben, gegen den Uhrzeigersinn)
     *
     * @param dir  PacMans aktuelle Richtung
     * @return     Winkel in Grad
     */
    private int getMouthAngle(PacMan.Direction dir) {
        return switch (dir) {
            case RIGHT -> 0;
            case LEFT  -> 180;
            case UP    -> 90;
            case DOWN  -> 270;
            case NONE  -> 0; // Standard: nach rechts schauen
        };
    }

    /**
     * Zeichnet alle Geister als rote Kreise.
     */
    private void drawGhosts(Graphics2D g) {
        List<Ghost> ghostList = controller.getGhosts();

        for (Ghost ghost : ghostList) {
            int gx = ghost.getX() * TILE_SIZE;
            int gy = ghost.getY() * TILE_SIZE;

            // Geist-Körper: roter Kreis (oben rund)
            g.setColor(COLOR_GHOST);
            g.fillOval(gx + 2, gy + 2, TILE_SIZE - 4, TILE_SIZE - 4);

            // Augen: zwei kleine weiße Punkte
            g.setColor(Color.WHITE);
            g.fillOval(gx + 8,  gy + 10, 7, 7);
            g.fillOval(gx + 24, gy + 10, 7, 7);

            // Pupillen: kleine blaue Punkte
            g.setColor(Color.BLUE);
            g.fillOval(gx + 10, gy + 12, 4, 4);
            g.fillOval(gx + 26, gy + 12, 4, 4);
        }
    }

    /**
     * Zeichnet das HUD (Head-Up-Display) unterhalb des Labyrinths.
     * Zeigt Score und verbleibende Leben an.
     */
    private void drawHUD(Graphics2D g) {
        Tilemap map   = controller.getTilemap();
        int     score = controller.getGameState().getScore();
        int     lives = controller.getGameState().getLives();

        // HUD-Bereich unterhalb des Labyrinths
        int hudY = map.getRows() * TILE_SIZE + 5;

        g.setColor(COLOR_TEXT);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Score links
        g.drawString("Score: " + score, 10, hudY + 20);

        // Leben rechts (als Pac-Man-Symbole)
        int livesX = map.getCols() * TILE_SIZE - 120;
        g.drawString("Leben: " + lives, livesX, hudY + 20);
    }

    /**
     * Zeichnet den Game-Over oder Gewonnen-Bildschirm (nur wenn das Spiel vorbei ist).
     */
    private void drawGameOver(Graphics2D g) {
        if (!controller.getGameState().isGameOver()) return; // Noch nicht vorbei

        // Halbtransparenter dunkler Hintergrund
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Nachricht auswählen
        String message = controller.getGameState().hasWon()
            ? "Du hast gewonnen! 🎉"
            : "Game Over!";

        // Großer Text zentriert
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(controller.getGameState().hasWon() ? Color.GREEN : Color.RED);

        FontMetrics fm   = g.getFontMetrics();
        int         textX = (getWidth()  - fm.stringWidth(message)) / 2;
        int         textY = (getHeight() / 2);

        g.drawString(message, textX, textY);

        // Hinweistext darunter
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        String hint   = "Score: " + controller.getGameState().getScore();
        int    hintX  = (getWidth() - g.getFontMetrics().stringWidth(hint)) / 2;
        g.drawString(hint, hintX, textY + 40);
    }
}
