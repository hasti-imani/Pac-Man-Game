import controller.GameController;
import view.SwingGameView;

import javax.swing.*;

/**
 * Main-Klasse – Einstiegspunkt des Programms
 * ----------------------------------------------------------
 * Hier wird das MVC-Muster zusammengebaut:
 *   1. Controller erstellen (erstellt intern alle Model-Objekte)
 *   2. View erstellen und mit Controller verbinden
 *   3. Controller die View mitteilen
 *   4. Fenster anzeigen
 *   5. Spielschleife starten
 *
 * Reihenfolge ist wichtig: Controller vor View, View vor startGame()!
 */
public class Main {

    public static void main(String[] args) {

        // Swing-Code immer im Event-Dispatch-Thread ausführen (Swing-Regel)
        SwingUtilities.invokeLater(() -> {

            // --- 1) Controller erstellen ---
            // Der Controller erstellt intern: Tilemap, PacMan, GameState, Ghosts
            GameController controller = new GameController();

            // --- 2) View erstellen (bekommt Controller als Parameter) ---
            // Die View nutzt den Controller um Daten abzufragen
            SwingGameView view = new SwingGameView(controller);

            // --- 3) Controller die View mitteilen ---
            // Damit der Controller nach jedem Tick view.refresh() aufrufen kann
            controller.setView(view);

            // --- 4) Fenster (JFrame) erstellen und konfigurieren ---
            JFrame frame = new JFrame("Pac-Man – Schulprojekt");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(view);           // View ins Fenster einfügen
            frame.pack();              // Fenstergröße an Panel anpassen
            frame.setLocationRelativeTo(null); // Fenster zentrieren
            frame.setResizable(false); // Fenstergröße fixieren
            frame.setVisible(true);

            // Fokus auf das Panel setzen, damit Tastatureingaben funktionieren
            view.requestFocusInWindow();

            // --- 5) Spielschleife starten ---
            controller.startGame();
        });
    }
}
