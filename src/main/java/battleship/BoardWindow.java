package battleship;

import javax.swing.*;
import java.awt.*;

public class BoardWindow {

    private static JFrame frame;
    private static JPanel boardPanel;

    public static void show(char[][] map) {

        if (frame == null) {

            frame = new JFrame("Battleship Board");
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            boardPanel = new JPanel();
            boardPanel.setLayout(new GridLayout(map.length, map[0].length));

            frame.add(boardPanel);
            frame.setVisible(true);
        }

        boardPanel.removeAll();

        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {

                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setOpaque(true);
                cell.setPreferredSize(new Dimension(40,40));

                char value = map[r][c];

                if (value == '#') {
                    cell.setBackground(Color.GRAY);
                }
                else if (value == '*') {
                    cell.setBackground(Color.RED);

                    Timer timer = new Timer(200, e -> {
                        cell.setBackground(Color.yellow);
                        new Timer(200, ev -> cell.setBackground(Color.RED)).start();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                else if (value == 'o') {
                    cell.setBackground(Color.WHITE);
                }
                else {
                    cell.setBackground(new Color(30,144,255));
                }

                boardPanel.add(cell);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }
}