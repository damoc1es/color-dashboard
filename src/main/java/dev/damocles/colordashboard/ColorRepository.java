package dev.damocles.colordashboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class ColorRepository {
    private final String filename;
    private final ArrayList<Color> colors = new ArrayList<>();

    ColorRepository(String filename) throws IOException {
        this.filename = filename;
        loadFromFile();
    }

    /**
     * Writes to file the existing colors
     * @throws IOException if it failed writing to file
     */
    private void writeToFile() throws IOException {
        colors.sort(Color::compare);
        try(FileWriter writer = new FileWriter(filename, false) ){
            for(Color x : colors) {
                writer.write(x.toHex()+'\n');
            }
        }
    }

    /**
     * Loads from file the colors; or creates the file if it did not exist
     * @throws IOException if it failed opening the file
     */
    private void loadFromFile() throws IOException {
        File file = new File(filename);
        try {
            Scanner scanner = new Scanner(file);

            colors.clear();
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if(Color.isValidString(line)) {
                    Color added =  new Color(line);
                    colors.add(added);
                }
            }

        } catch (FileNotFoundException e) {
            file.createNewFile();
        }
    }

    /**
     * @param x color to be found
     * @return whether color x exists in repo
     */
    public boolean find(Color x) {
        for(Color color : colors)
            if(Objects.equals(color.toHex(), x.toHex()))
                return true;

        return false;
    }

    /**
     * @param added color to be added
     * @return whether it has been added successfully (no duplicates)
     * @throws IOException if it failed updating the file
     */
    public boolean store(Color added) throws IOException {
        if(find(added))
            return false;

        colors.add(added);
        writeToFile();
        return true;
    }

    /**
     * @param removed color to be removed
     * @return whether it has been removed successfully (it existed)
     * @throws IOException if it failed updating the file
     */
    public boolean remove(Color removed) throws IOException {
        if(!find(removed))
            return false;

        colors.removeIf(x -> Objects.equals(x.toHex(), removed.toHex()));
        writeToFile();
        return true;
    }

    /**
     * @return the array of colors stored
     */
    public ArrayList<Color> getColors() {
        return colors;
    }
}
