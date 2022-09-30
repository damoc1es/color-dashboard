package dev.damocles.colordashboard;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class ColorDashController {
    public FlowPane colorGrid;
    public TextField fieldColor;
    public Label msgText;

    private ColorRepository repo;

    private final ArrayList<Pair<String, String>> undoList = new ArrayList<>();

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
     * saves to file changes
     */
    @FXML
    public void onAddColorClick() {
        String colorString = fieldColor.getText();

        if(Color.isValidString(colorString)) {
            Color added = new Color(colorString);

            try {
                if(!repo.store(added)) {
                    setMsg("Color already exists");
                    return;
                }
                undoList.add(new Pair<>("add", added.toHex()));
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
     * saves to file changes
     */
    @FXML
    public void onDeleteColorClick() {
        String colorString = fieldColor.getText();

        if(Color.isValidString(colorString)) {
            Color deleted = new Color(colorString);

            try {
                if(!repo.remove(deleted)) {
                    setMsg("Color not in list.");
                    return;
                }
                undoList.add(new Pair<>("delete", deleted.toHex()));
                updateGrid();
            } catch (IOException e) {
                setMsg("Error updating file.");
            }
        } else setMsg("Color format not valid.");
    }

    /**
     * Undoes the last action (add/delete)
     * saves to file changes
     */
    @FXML
    public void onUndoClick() {
        if(undoList.isEmpty()) {
            setMsg("No action to undo");
            return;
        }

        Pair<String, String> action = undoList.get(undoList.size()-1);
        try {
            if(Objects.equals(action.getKey(), "add")) {
                repo.remove(new Color(action.getValue()));
            } else if (Objects.equals(action.getKey(), "delete")) {
                repo.store(new Color(action.getValue()));
            }

            updateGrid();
        } catch (IOException e) {
            setMsg("Error updating file.");
        }

        undoList.remove(undoList.size()-1);
    }

    /**
     * Tries to generate a Color that isn't yet stored.
     * @param cap the maximum tries it does before it gives up
     * @return generated Color
     */
    private Color generateRandom(int cap) {
        Random random = new Random();
        Color color;
        int i = 0;
        do {
            int R = random.nextInt(256);
            int G = random.nextInt(256);
            int B = random.nextInt(256);
            color = new Color(String.format("%d,%d,%d", R, G, B));
            i++;
        } while (repo.find(color) && i < cap);
        return color;
    }

    /**
     * Tries to generate and add a new Color
     * saves to file changes
     */
    @FXML
    public void onAddRandomClick() {
        Color generated = generateRandom(10000);

        try {
            if(!repo.store(generated)) {
                setMsg("Too many colors already existing");
                return;
            }
            undoList.add(new Pair<>("add", generated.toHex()));
            updateGrid();
        } catch (IOException e) {
            setMsg("Error updating file.");
        }
    }
}