package dev.damocles.colordashboard;

import java.util.Vector;

public class Color {
    int R, G, B;
    Color(String colorString) {
        Vector<Integer> vector = turnToRGB(colorString);
        if(vector.size() == 3) {
            R = vector.elementAt(0);
            G = vector.elementAt(1);
            B = vector.elementAt(2);
        } else {
            R = G = B = 0;
        }
    }

    public int getR() { return R; }
    public int getG() { return G; }
    public int getB() { return B; }

    public String toHex() {
        String hex = "#";

        if(Integer.toString(R, 16).length() == 1)
            hex = hex + "0";
        hex += Integer.toString(R, 16);

        if(Integer.toString(G, 16).length() == 1)
            hex = hex + "0";
        hex += Integer.toString(G, 16);

        if(Integer.toString(B, 16).length() == 1)
            hex = hex + "0";
        hex += Integer.toString(B, 16);

        return hex;
    }

    static Vector<Integer> turnToRGB(String colorString) {
        Vector<Integer> vector = new Vector<>();
        if(!isValidString(colorString))
            return vector;

        if(colorString.matches("^#?[0-9a-fA-F]{6}$")) {
            if(colorString.charAt(0) == '#')
                colorString = colorString.substring(1);
            int R = Integer.parseInt(colorString.substring(0,2), 16);
            int G = Integer.parseInt(colorString.substring(2,4), 16);
            int B = Integer.parseInt(colorString.substring(4,6), 16);
            vector.add(R);
            vector.add(G);
            vector.add(B);
        } else if(colorString.matches("^[0-9]+,[0-9]+,[0-9]+$")) {
            String[] RGB = colorString.split(",");
            vector.add(Integer.parseInt(RGB[0]));
            vector.add(Integer.parseInt(RGB[1]));
            vector.add(Integer.parseInt(RGB[2]));
        }
        return vector;
    }
    /**
     * Checks if the color string is valid hex color OR rgb value list
     */
    static boolean isValidString(String colorString) {
        if(colorString.matches("^#?[0-9a-fA-F]{6}$")) {
            return true;
        } else if(colorString.matches("^[0-9]+,[0-9]+,[0-9]+$")) {
            String[] RGB = colorString.split(",");
            //System.out.println(RGB[0]);
            //System.out.println(RGB[1]);
            //System.out.println(RGB[2]);
            for(int i=0; i<3; i++)
                if(Integer.parseInt(RGB[0]) < 0 || Integer.parseInt(RGB[0]) > 255)
                    return false;
            return true;
        }
        return false;
    }
}
