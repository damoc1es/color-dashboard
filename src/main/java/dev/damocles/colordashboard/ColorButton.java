package dev.damocles.colordashboard;

import javafx.scene.control.Button;

public class ColorButton extends Button {
    private final Color clr;
    ColorButton(Color color) {
        super(color.toHex());
        clr = color;
        super.setStyle(String.format("-fx-background-color: %s;\n", clr.toHex()) +
                "-fx-padding: 10px;\n" +
                "-fx-font-family: monospace;");
    }

    public Color getColor() {
        return clr;
    }
}
