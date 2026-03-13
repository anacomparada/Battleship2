package battleship;

import javax.swing.*;
import java.awt.*;

public class BoardWindow {

    public static void show(char[][] map) {

        JFrame frame = new JFrame("Battleship Board");
        frame.setSize(500, 500);
        frame.setLayout(new GridLayout(map.length, map[0].length));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {

                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setOpaque(true);

                char value = map[r][c];

                if (value == '#') {
                    cell.setBackground(Color.GRAY); // navio
                }
                else if (value == '*') {
                    cell.setBackground(Color.RED); // acerto
                }
                else if (value == 'o') {
                    cell.setBackground(Color.WHITE); // tiro na água
                }
                else {
                    cell.setBackground(new Color(30,144,255)); // água (azul)
                }

                frame.add(cell);
            }
        }

        frame.setVisible(true);
    }
}