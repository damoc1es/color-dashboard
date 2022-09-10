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

    /**
     * Sets the message from below buttons
     */
    @FXML
    protected void setMsg(String message) {
        msgText.setText(message);
    }

    /**
     * Loads from file the colors and sorts it according to the Color::compare
     * adding them to the colorGrid
     */
    protected void loadFromFile() {
        try {
            File file = new File(ColorDashboard.colorsFilename);
            Scanner sc = new Scanner(file);
            colorGrid.getChildren().clear();
            ObservableList<Node> list = colorGrid.getChildren();

            ArrayList<Color> arr = new ArrayList<>();

            while (sc.hasNextLine()) {
                String string = sc.nextLine();
                if(Color.isValidString(string)) {
                    Color added = new Color(string);
                    arr.add(added);
                }
            }

            arr.sort(Color::compare);

            for(Color clr : arr) {
                ColorButton colorBtn = new ColorButton(clr);
                colorBtn.setOnMouseClicked(mouseEvent -> btnClick(colorBtn.getColor().toHex()));

                list.add(colorBtn);
            }
        } catch(FileNotFoundException e) {
            setMsg("No previous file found");
        }
    }

    /**
     * Writes to file the existing colors
     */
    protected void writeToFile() {
        try(FileWriter writer = new FileWriter(ColorDashboard.colorsFilename, false) ){
            ObservableList<Node> list = colorGrid.getChildren();

            for(Node x : list) {
                if(x instanceof ColorButton)
                    writer.write(((ColorButton) x).getColor().toHex()+'\n');
            }
        } catch (IOException e) {
            setMsg("IO Exception");
        }
    }

    @FXML
    public void initialize() {
        loadFromFile();
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
    protected void onAddColorClick() {
        String clrString = fieldColor.getText();

        if(Color.isValidString(clrString)) {
            Color added = new Color(clrString);
            ObservableList<Node> list = colorGrid.getChildren();

            boolean duplicate = false;
            for(Node x : list) {
                if(x instanceof ColorButton && Objects.equals(((ColorButton) x).getColor().toHex(), added.toHex()))
                    duplicate = true;
            }
            if(duplicate) {
                setMsg("Color already exists");
                return;
            }
            ColorButton colorBtn = new ColorButton(added);
            colorBtn.setOnMouseClicked(mouseEvent -> btnClick(colorBtn.getColor().toHex()));

            list.add(colorBtn);

            try(FileWriter writer = new FileWriter(ColorDashboard.colorsFilename, true) ){
                writer.write(added.toHex()+'\n');
                setMsg("");
            } catch (IOException e) {
                setMsg("IO Exception");
            }
            loadFromFile();
            writeToFile();
        }
        else
            setMsg("Error! Invalid format.");
    }

    /**
     * Remove the color from field (if it exists)
     * saves to file changes (including sorting)
     */
    @FXML
    public void onDeleteColorClick() {
        String colorString = fieldColor.getText();

        if(Color.isValidString(colorString)) {
            ObservableList<Node> list = colorGrid.getChildren();
            list.removeIf(x -> x instanceof ColorButton && Objects.equals(((ColorButton) x).getColor().toHex(), new Color(colorString).toHex()));
            writeToFile();
        } else setMsg("Color not valid.");
    }
}