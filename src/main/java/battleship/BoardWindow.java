package battleship;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BoardWindow {

    private static JFrame frame;
    private static JPanel boardPanel;

    public static void show(char[][] myMap, char[][] alienMap,
                            List<IPosition> lastMyShots,
                            List<IPosition> lastAlienShots) {

        if (frame == null) {
            frame = new JFrame("Battleship");
            frame.setSize(900, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            boardPanel = new JPanel();
            boardPanel.setLayout(new GridLayout(1, 2));

            frame.add(boardPanel);
            frame.setVisible(true);
        }

        boardPanel.removeAll();

        JPanel myBoard = createBoardPanel(myMap, lastAlienShots);
        JPanel alienBoard = createBoardPanel(alienMap, lastMyShots);

        JPanel myContainer = new JPanel(new BorderLayout());
        JLabel myLabel = new JLabel("Minha Frota", SwingConstants.CENTER);
        myLabel.setFont(new Font("Arial", Font.BOLD, 16));
        myContainer.add(myLabel, BorderLayout.NORTH);
        myContainer.add(myBoard, BorderLayout.CENTER);

        JPanel alienContainer = new JPanel(new BorderLayout());
        JLabel alienLabel = new JLabel("Frota Inimiga", SwingConstants.CENTER);
        alienLabel.setFont(new Font("Arial", Font.BOLD, 16));
        alienContainer.add(alienLabel, BorderLayout.NORTH);
        alienContainer.add(alienBoard, BorderLayout.CENTER);

        boardPanel.add(myContainer);
        boardPanel.add(alienContainer);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private static JPanel createBoardPanel(char[][] map, List<IPosition> lastShots) {

        JPanel panel = new JPanel(new GridLayout(map.length, map[0].length));

        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {

                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setOpaque(true);
                cell.setPreferredSize(new Dimension(40, 40));

                char value = map[r][c];

                boolean isRecentShot = false;
                for (IPosition p : lastShots) {
                    if (p.getRow() == r && p.getColumn() == c) {
                        isRecentShot = true;
                        break;
                    }
                }

                if (value == '#') {
                    cell.setBackground(Color.GRAY);
                }
                else if (value == '*') {
                    cell.setBackground(Color.RED);

                    if (isRecentShot) {
                        Timer timer = new Timer(200, e -> {
                            cell.setBackground(Color.YELLOW);

                            Timer backTimer = new Timer(200, ev -> cell.setBackground(Color.RED));
                            backTimer.setRepeats(false);
                            backTimer.start();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
                else if (value == 'o') {
                    cell.setBackground(Color.BLUE);
                }
                else {
                    cell.setBackground(Color.WHITE);
                }

                panel.add(cell);
            }
        }

        return panel;
    }

    public static void close() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }
}