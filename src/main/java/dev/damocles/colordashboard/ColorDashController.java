package dev.damocles.colordashboard;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;

import java.io.*;
import java.util.*;

public class ColorDashController {
    public FlowPane colorGrid;
    public TextField fieldColor;
    public Label msgText;

    private ColorRepository repo;

    /**
     * Sets the message from below buttons
     */
    @FXML
    private void setMsg(String message) {
        msgText.setText(message);
    }

    @FXML
    public void initialize() {
        try {
            repo = new ColorRepository(ColorDashboard.colorsFilename);
            updateGrid();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateGrid() {
        ArrayList<Color> colors = repo.getColors();
        colorGrid.getChildren().clear();
        ObservableList<Node> list = colorGrid.getChildren();

        for(Color color : colors) {
            ColorButton colorBtn = new ColorButton(color);
            colorBtn.setOnMouseClicked(mouseEvent -> btnClick(colorBtn.getColor().toHex()));

            list.add(colorBtn);
        }
    }

    /**
     * Sets the field as the hex color string and
     * copies to clipboard the hex color
     */
    public void btnClick(String hexCode) {
        fieldColor.setText(hexCode);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(hexCode);
        clipboard.setContent(content);
    }

    /**
     * Adds the color specified in field (if it exists)
     * otherwise sets message of what happened
     * saves to file changes (including sorting)
     */
    @FXML
    public void onAddColorClick() {
        String clrString = fieldColor.getText();

        if(Color.isValidString(clrString)) {
            Color added = new Color(clrString);

            try {
                if(!repo.store(added)) {
                    setMsg("Color already exists");
                    return;
                }
                updateGrid();
            } catch (IOException e) {
                setMsg("Error updating file.");
            }
        }
        else
            setMsg("Color format not valid.");
    }

    /**
     * Remove the color from field (if it exists)
     * saves to file changes (including sorting)
     */
    @FXML
    public void onDeleteColorClick() {
        String colorString = fieldColor.getText();

        if(Color.isValidString(colorString)) {
            try {
                if(!repo.remove(new Color(colorString)))
                    setMsg("Color not in list.");
                updateGrid();
            } catch (IOException e) {
                setMsg("Error updating file.");
            }
        } else setMsg("Color format not valid.");
    }
}