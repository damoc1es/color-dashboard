package dev.damocles.colordashboard;

import javafx.scene.control.Button;

public class ColorButton extends Button {
    private final Color clr;

    ColorButton(Color color) {
        super(color.toHex());
        clr = color;
        super.setStyle(String.format("-fx-background-color: %s;\n", clr.toHex()) +
                String.format("-fx-text-fill: %s;\n", clr.getComplementary().toHex()) +
                "-fx-padding: 10px;\n" +
                "-fx-font-family: Consolas, monospace;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 1.2em;");
    }

    /**
     * @return assigned color
     */
    public Color getColor() {
        return clr;
    }
}
