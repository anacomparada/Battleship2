package battleship;

import javax.swing.*;
import java.awt.*;

public class BoardWindow {

    private static JFrame frame;
    private static JPanel boardPanel;

    public static void show(char[][] myMap, char[][] alienMap) {

        if (frame == null) {

            frame = new JFrame("Battleship Board");
            frame.setSize(900, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            boardPanel = new JPanel();
            boardPanel.setLayout(new GridLayout(1, 2)); // 2 colunas (lado a lado)

            frame.add(boardPanel);
            frame.setVisible(true);
        }

        boardPanel.removeAll();

        // Criar os dois tabuleiros
        JPanel myBoard = createBoardPanel(myMap);
        JPanel alienBoard = createBoardPanel(alienMap);

        // Container com título - Minha Frota
        JPanel myContainer = new JPanel(new BorderLayout());
        JLabel myLabel = new JLabel("Minha Frota", SwingConstants.CENTER);
        myLabel.setFont(new Font("Arial", Font.BOLD, 16));
        myContainer.add(myLabel, BorderLayout.NORTH);
        myContainer.add(myBoard, BorderLayout.CENTER);

        // Container com título - Frota Inimiga
        JPanel alienContainer = new JPanel(new BorderLayout());
        JLabel alienLabel = new JLabel("Frota Inimiga", SwingConstants.CENTER);
        alienLabel.setFont(new Font("Arial", Font.BOLD, 16));
        alienContainer.add(alienLabel, BorderLayout.NORTH);
        alienContainer.add(alienBoard, BorderLayout.CENTER);

        // Adicionar os dois à janela
        boardPanel.add(myContainer);
        boardPanel.add(alienContainer);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    /**
     * Cria um painel de tabuleiro a partir de um mapa
     */
    private static JPanel createBoardPanel(char[][] map) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(map.length, map[0].length));

        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {

                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setOpaque(true);
                cell.setPreferredSize(new Dimension(40, 40));

                char value = map[r][c];

                if (value == '#') {
                    cell.setBackground(Color.GRAY);
                }
                else if (value == '*') {
                    cell.setBackground(Color.RED);

                    // efeito visual de piscar (mantive o teu)
                    Timer timer = new Timer(200, e -> {
                        cell.setBackground(Color.YELLOW);
                        new Timer(200, ev -> cell.setBackground(Color.RED)).start();
                    });
                    timer.setRepeats(false);
                    timer.start();
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
}