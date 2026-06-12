package view;

/**
 * GameView-Interface (View)
 * ----------------------------------------------------------
 * Definiert den Vertrag, den jede View erfüllen muss.
 *
 * Warum ein Interface?
 *   → Der Controller kennt nur dieses Interface, nicht die konkrete View.
 *   → Dadurch kann man später leicht eine andere View einbauen
 *     (z.B. Konsolen-View zum Testen, Swing-View für das echte Spiel).
 *
 * MVC-Regel: Der Controller ruft nur refresh() auf.
 *            Die View holt sich dann selbst die Daten vom Controller.
 */
public interface GameView {

    /**
     * Fordert die View auf, sich neu zu zeichnen.
     * Wird vom Controller nach jedem Spielschritt aufgerufen.
     */
    void refresh();
}
