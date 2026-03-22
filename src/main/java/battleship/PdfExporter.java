package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

public class PdfExporter {

    /**
     * Classe interna auxiliar para guardar o estado da página e da posição Y
     * enquanto mudamos de página dinamicamente.
     */
    private static class PdfState {
        PDPage page;
        PDPageContentStream contentStream;
        float y;

        PdfState(PDPage page, PDPageContentStream contentStream, float y) {
            this.page = page;
            this.contentStream = contentStream;
            this.y = y;
        }
    }

    public static void exportGameReport(IGame game, String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yPosition = 750;

            // --- CABEÇALHO DO DOCUMENTO ---
            contentStream.setLineWidth(1.5f);
            contentStream.setStrokingColor(Color.DARK_GRAY);
            contentStream.moveTo(50, yPosition - 20);
            contentStream.lineTo(550, yPosition - 20);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.setNonStrokingColor(new Color(0, 51, 102));
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Relatório Oficial - Batalha Naval");
            contentStream.endText();

            yPosition -= 50;

            // Criamos o nosso objeto de estado para gerir a paginação facilmente
            PdfState state = new PdfState(page, contentStream, yPosition);

            // --- 1. AS MINHAS JOGADAS ---
            printMoveSection(document, state, "As Minhas Rajadas (Ataques ao PC)", game.getMyMoves());

            state.y -= 20; // Espaço extra entre as secções

            // --- 2. JOGADAS DO INIMIGO ---
            printMoveSection(document, state, "Histórico de Rajadas (Inimigo / PC)", game.getAlienMoves());

            // Fechar a stream final e guardar
            state.contentStream.close();
            document.save(filePath);

            System.out.println("Sucesso! O relatório detalhado de AMBOS os jogadores foi guardado em: " + filePath);

        } catch (IOException e) {
            System.err.println("Ocorreu um erro ao gerar o PDF: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar que processa qualquer lista de jogadas, aplicando cores e gerindo
     * a criação de novas páginas automaticamente quando o texto chega ao fundo.
     */
    private static void printMoveSection(PDDocument document, PdfState state, String title, List<IMove> moves) throws IOException {

        // Verifica se precisamos de uma página nova antes de imprimir o título
        checkPagination(document, state, 100);

        // Título da Secção
        state.contentStream.beginText();
        state.contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        state.contentStream.setNonStrokingColor(Color.BLACK);
        state.contentStream.newLineAtOffset(50, state.y);
        state.contentStream.showText(title);
        state.contentStream.endText();
        state.y -= 25;

        if (moves == null || moves.isEmpty()) {
            state.contentStream.beginText();
            state.contentStream.setFont(PDType1Font.HELVETICA, 12);
            state.contentStream.newLineAtOffset(50, state.y);
            state.contentStream.showText("Nenhuma jogada registada nesta categoria.");
            state.contentStream.endText();
            state.y -= 25;
            return;
        }

        // Listar as Rajadas
        for (IMove move : moves) {
            checkPagination(document, state, 100);

            state.contentStream.beginText();
            state.contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            state.contentStream.setNonStrokingColor(Color.DARK_GRAY);
            state.contentStream.newLineAtOffset(50, state.y);
            state.contentStream.showText("Rajada nº " + move.getNumber());
            state.contentStream.endText();
            state.y -= 15;

            List<IPosition> shots = move.getShots();
            List<IGame.ShotResult> results = move.getShotResults();

            // Listar cada tiro da rajada
            for (int i = 0; i < shots.size(); i++) {
                checkPagination(document, state, 50);

                IPosition pos = shots.get(i);
                IGame.ShotResult res = results.get(i);

                String outcome;
                Color textColor;

                // Lógica de Cores e Resultados
                if (!res.valid()) {
                    outcome = "Fora do limite";
                    textColor = Color.LIGHT_GRAY;
                } else if (res.repeated()) {
                    outcome = "Tiro repetido";
                    textColor = Color.ORANGE;
                } else if (res.ship() == null) {
                    outcome = "Água";
                    textColor = new Color(0, 102, 204); // Azul Água
                } else if (res.sunk()) {
                    outcome = "AFUNDOU: " + res.ship().getCategory().toUpperCase() + "!";
                    textColor = Color.RED;
                } else {
                    outcome = "Acertou em: " + res.ship().getCategory();
                    textColor = new Color(153, 0, 0); // Vermelho Escuro
                }

                state.contentStream.beginText();
                state.contentStream.setFont(PDType1Font.HELVETICA, 11);
                state.contentStream.setNonStrokingColor(textColor);
                state.contentStream.newLineAtOffset(70, state.y); // Indentação de 70 para ficar alinhado à frente
                state.contentStream.showText("-> Alvo " + pos.toString() + " : " + outcome);
                state.contentStream.endText();
                state.y -= 14;
            }
            state.y -= 10; // Espaço em branco no fim da rajada
        }
    }

    /**
     * Verifica se o limite inferior da página foi atingido.
     * Se sim, cria uma página nova automaticamente.
     */
    private static void checkPagination(PDDocument document, PdfState state, float limit) throws IOException {
        if (state.y < limit) {
            state.contentStream.close();
            state.page = new PDPage();
            document.addPage(state.page);
            state.contentStream = new PDPageContentStream(document, state.page);
            state.y = 750; // Volta ao topo da página nova
        }
    }
}