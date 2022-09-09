package dev.damocles.colordashboard;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class ColorDashController {
    public FlowPane colorGrid;
    public TextField fieldColor;
    public Label msgText;

    @FXML
    protected void setMsg(String message) {
        msgText.setText(message);
    }

    protected void loadFromFile() {
        try {
            File file = new File(ColorDashboard.colorsFilename);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String string = sc.nextLine();
                if(Color.isValidString(string)) {
                    Color added = new Color(string);
                    ObservableList<Node> list = colorGrid.getChildren();
                    list.add(new ColorButton(added));
                }
            }
        } catch(FileNotFoundException e) {
            setMsg("No previous file found");
        }

    }

    @FXML
    public void initialize() {
        loadFromFile();
    }

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

            list.add(new ColorButton(added));

            try(FileWriter writer = new FileWriter(ColorDashboard.colorsFilename, true) ){
                writer.write(added.toHex()+'\n');
                setMsg("");
            } catch (IOException e) {
                setMsg("IO Exception");
            }
        }
        else
            setMsg("Error! Invalid format.");
    }
}