package battleship;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BoardWindow {

    private static JFrame frame;
    private static JPanel boardPanel;

    // ✔ FIX java:S1118
    private BoardWindow() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void show(char[][] myMap, char[][] alienMap,
                            List<IPosition> lastMyShots,
                            List<IPosition> lastAlienShots) {

        if (frame == null) {
            frame = new JFrame("Battleship");
            frame.setSize(900, 500);

            // ✔ FIX java:S3252
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            boardPanel = new JPanel(new GridLayout(1, 2));
            frame.add(boardPanel);
            frame.setVisible(true);
        }

        boardPanel.removeAll();

        JPanel myBoard = createBoardPanel(myMap, lastAlienShots);
        JPanel alienBoard = createBoardPanel(alienMap, lastMyShots);

        boardPanel.add(createBoardContainer("Minha Frota", myBoard));
        boardPanel.add(createBoardContainer("Frota Inimiga", alienBoard));

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private static JPanel createBoardContainer(String title, JPanel board) {
        JPanel container = new JPanel(new BorderLayout());

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));

        container.add(label, BorderLayout.NORTH);
        container.add(board, BorderLayout.CENTER);

        return container;
    }

    // ✔ FIX java:S3776 (refactoring)
    private static JPanel createBoardPanel(char[][] map, List<IPosition> lastShots) {
        JPanel panel = new JPanel(new GridLayout(map.length, map[0].length));

        for (int row = 0; row < map.length; row++) {
            addRow(panel, map, lastShots, row);
        }

        return panel;
    }

    private static void addRow(JPanel panel, char[][] map, List<IPosition> lastShots, int row) {
        for (int col = 0; col < map[row].length; col++) {
            panel.add(createCell(map[row][col], row, col, lastShots));
        }
    }

    private static JLabel createCell(char value, int row, int col, List<IPosition> lastShots) {
        JLabel cell = new JLabel("", SwingConstants.CENTER);

        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cell.setOpaque(true);
        cell.setPreferredSize(new Dimension(40, 40));

        applyCellColor(cell, value, isRecentShot(row, col, lastShots));

        return cell;
    }

    private static boolean isRecentShot(int row, int col, List<IPosition> lastShots) {
        for (IPosition p : lastShots) {
            if (p.getRow() == row && p.getColumn() == col) {
                return true;
            }
        }
        return false;
    }

    private static void applyCellColor(JLabel cell, char value, boolean isRecentShot) {
        switch (value) {
            case '#':
                cell.setBackground(Color.GRAY);
                break;

            case '*':
                cell.setBackground(Color.RED);
                highlightRecentShot(cell, isRecentShot);
                break;

            case 'o':
                cell.setBackground(Color.BLUE);
                break;

            default:
                cell.setBackground(Color.WHITE);
        }
    }

    private static void highlightRecentShot(JLabel cell, boolean isRecentShot) {
        if (!isRecentShot) return;

        Timer timer = new Timer(200, e -> {
            cell.setBackground(Color.YELLOW);

            Timer backTimer = new Timer(200, ev -> cell.setBackground(Color.RED));
            backTimer.setRepeats(false);
            backTimer.start();
        });

        timer.setRepeats(false);
        timer.start();
    }

    public static void close() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }
}