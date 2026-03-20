package battleship;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Manages the Battleship Hall of Fame / Scoreboard.
 *
 * <p>Results are persisted as JSON in {@code data/scoreboard.json}.
 * The class exposes methods to save new results, display the scoreboard
 * (sorted by score or by date), and interactively ask the user how to sort.
 *
 * <p>Sort modes:
 * <ul>
 *   <li>1 – by score (fewest moves = best, wins before losses)</li>
 *   <li>2 – by date  (most recent first)</li>
 * </ul>
 */
public class Scoreboard {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final String FILE_PATH = "data/scoreboard.json";
    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";

    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Persists a new game result to the scoreboard file.
     *
     * @param result the {@link GameResult} to save
     */
    public static void saveResult(GameResult result) {
        List<GameResult> results = loadResults();
        results.add(result);
        try {
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, results);
            System.out.println("\n[Scoreboard] Resultado guardado com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao guardar o resultado: " + e.getMessage());
        }
    }

    /**
     * Displays the scoreboard sorted by the given criterion.
     *
     * @param sortBy 1 = por pontuação (jogadas), 2 = por data
     */
    public static void displayScoreboard(int sortBy) {
        List<GameResult> results = loadResults();

        if (results.isEmpty()) {
            System.out.println("\nO Hall of Fame está vazio. Joga uma partida para entrar na história!\n");
            return;
        }

        // ── Sort ────────────────────────────────────────────────────────────
        if (sortBy == 1) {
            // Wins first; then fewest moves is best
            results.sort(Comparator
                    .comparing(GameResult::isPlayerWon).reversed()
                    .thenComparingInt(GameResult::getTotalMoves));
        } else {
            // Most recent first
            results.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DATE_PATTERN);

        // ── Render ──────────────────────────────────────────────────────────
        String sortLabel = (sortBy == 1) ? "pontuação" : "data";
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         ⚓  HALL OF FAME  ⚓          ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("  Ordenado por: " + sortLabel);
        System.out.println();

        String table = AsciiTable.getTable(results, Arrays.asList(
                new Column().header("#")
                            .with(r -> String.valueOf(results.indexOf(r) + 1)),
                new Column().header("Jogador")
                            .with(GameResult::getPlayerName),
                new Column().header("Resultado")
                            .with(r -> r.isPlayerWon() ? "VITÓRIA" : "DERROTA"),
                new Column().header("Jogadas")
                            .with(r -> String.valueOf(r.getTotalMoves())),
                new Column().header("Data")
                            .with(r -> r.getDate().format(fmt))
        ));

        System.out.println(table);
        System.out.println();
    }

    /**
     * Interactive scoreboard prompt: asks the user how to sort and displays the table.
     *
     * @param in the active {@link Scanner}
     */
    public static void interactiveDisplay(Scanner in) {
        System.out.println("\nOrdenar o Hall of Fame por:");
        System.out.println("  1 - Pontuação (vitórias primeiro, depois menos jogadas)");
        System.out.println("  2 - Data (mais recente primeiro)");
        System.out.print("Escolha (1 ou 2): ");

        int choice = 1;
        if (in.hasNextInt()) {
            int v = in.nextInt();
            if (v == 2) choice = 2;
        }
        displayScoreboard(choice);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Loads all saved results from the JSON file.
     *
     * @return list of {@link GameResult}, empty if the file doesn't exist or fails to parse
     */
    private static List<GameResult> loadResults() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try {
            return MAPPER.readValue(file, new TypeReference<List<GameResult>>() {});
        } catch (IOException e) {
            System.err.println("Erro ao ler os resultados: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
