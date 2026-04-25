package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Test class for Scoreboard.
 *
 * Strategy: the real scoreboard file (data/scoreboard.json) is backed up
 * before each test and restored afterwards, so tests never corrupt real data.
 *
 * Cyclomatic Complexity for each method:
 * - saveResult:         2  (try/catch)
 * - displayScoreboard:  4  (empty check + sortBy branch + sort comparator)
 * - interactiveDisplay: 3  (hasNextInt + v==2 branch)
 * - loadResults:        3  (file exists + try/catch)
 */
public class ScoreboardTest {

    private static final String FILE_PATH   = "data/scoreboard.json";
    private static final String BACKUP_PATH = "data/scoreboard_backup_test.json";

    // ---------------------------------------------------------------
    // Backup / restore around every test
    // ---------------------------------------------------------------

    @BeforeEach
    void backupScoreboard() throws IOException {
        File file = new File(FILE_PATH);
        File backup = new File(BACKUP_PATH);
        if (file.exists()) {
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        // Start each test with a clean (deleted) scoreboard
        Files.deleteIfExists(file.toPath());
    }

    @AfterEach
    void restoreScoreboard() throws IOException {
        File file   = new File(FILE_PATH);
        File backup = new File(BACKUP_PATH);
        Files.deleteIfExists(file.toPath());
        if (backup.exists()) {
            Files.move(backup.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private GameResult makeResult(String name, boolean won, int moves, LocalDateTime date) {
        GameResult r = new GameResult();
        r.setPlayerName(name);
        r.setPlayerWon(won);
        r.setTotalMoves(moves);
        r.setDate(date);
        return r;
    }

    private GameResult makeResult(String name, boolean won, int moves) {
        return makeResult(name, won, moves, LocalDateTime.now());
    }

    // ---------------------------------------------------------------
    // saveResult
    // ---------------------------------------------------------------

    @Test
    void testSaveResultCreatesFile() {
        Scoreboard.saveResult(makeResult("Alice", true, 10));
        assertTrue(new File(FILE_PATH).exists(), "Scoreboard file should be created after saveResult()");
    }

    @Test
    void testSaveResultDoesNotThrow() {
        assertDoesNotThrow(() -> Scoreboard.saveResult(makeResult("Bob", false, 20)),
                "saveResult() should not throw for a valid result");
    }

    @Test
    void testSaveResultAccumulates() {
        Scoreboard.saveResult(makeResult("Alice", true,  10));
        Scoreboard.saveResult(makeResult("Bob",   false, 15));
        Scoreboard.saveResult(makeResult("Carol", true,   8));

        // displayScoreboard should render all 3 without throwing
        assertDoesNotThrow(() -> Scoreboard.displayScoreboard(1),
                "displayScoreboard() should handle 3 accumulated results");
    }

    @Test
    void testSaveResultPersistsPlayerName() throws IOException {
        Scoreboard.saveResult(makeResult("Zara", true, 5));
        String content = Files.readString(Path.of(FILE_PATH));
        assertTrue(content.contains("Zara"), "Saved file should contain the player name");
    }

    // ---------------------------------------------------------------
    // loadResults (tested indirectly via displayScoreboard / saveResult)
    // ---------------------------------------------------------------

    @Test
    void testLoadResultsWhenFileAbsent() {
        // File was deleted in @BeforeEach — displayScoreboard must not throw
        assertDoesNotThrow(() -> Scoreboard.displayScoreboard(1),
                "displayScoreboard() should handle a missing file gracefully");
    }

    @Test
    void testLoadResultsWithCorruptFile() throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), "{ this is not valid json ]]]");

        // Should not throw — loadResults returns empty list on parse error
        assertDoesNotThrow(() -> Scoreboard.displayScoreboard(1),
                "displayScoreboard() should handle a corrupt JSON file gracefully");
    }

    // ---------------------------------------------------------------
    // displayScoreboard — empty list branch
    // ---------------------------------------------------------------

    @Test
    void testDisplayScoreboardEmptyList() {
        // No results saved — should print the empty message without throwing
        assertDoesNotThrow(() -> Scoreboard.displayScoreboard(1),
                "displayScoreboard() should not throw when scoreboard is empty");
    }

    // ---------------------------------------------------------------
    // displayScoreboard — sortBy == 1 (score)
    // ---------------------------------------------------------------

    @Test
    void testDisplayScoreboardSortByScore() {
        Scoreboard.saveResult(makeResult("Alice", true,  20));
        Scoreboard.saveResult(makeResult("Bob",   true,   5));
        Scoreboard.saveResult(makeResult("Carol", false, 10));

        assertDoesNotThrow(() -> Scoreboard.displayScoreboard(1),
                "displayScoreboard(1) should not throw with multiple results");
    }

    @Test
    void testDisplayScoreboardSortByScoreWinsFirst() {
        // Captures stdout to verify wins appear before losses
        Scoreboard.saveResult(makeResult("Loser",  false, 3));
        Scoreboard.saveResult(makeResult("Winner", true,  30));

        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        Scoreboard.displayScoreboard(1);

        System.setOut(original);
        String output = baos.toString();

        int winnerIdx = output.indexOf("Winner");
        int loserIdx  = output.indexOf("Loser");
        assertTrue(winnerIdx < loserIdx,
                "Winner should appear before Loser when sorted by score");
    }

    // ---------------------------------------------------------------
    // displayScoreboard — sortBy == 2 (date)
    // ---------------------------------------------------------------

    @Test
    void testDisplayScoreboardSortByDate() {
        LocalDateTime older  = LocalDateTime.now().minusDays(5);
        LocalDateTime newer  = LocalDateTime.now();

        Scoreboard.saveResult(makeResult("Old",  true, 10, older));
        Scoreboard.saveResult(makeResult("New",  true,  5, newer));

        assertDoesNotThrow(() -> Scoreboard.displayScoreboard(2),
                "displayScoreboard(2) should not throw");
    }

    @Test
    void testDisplayScoreboardSortByDateMostRecentFirst() {
        LocalDateTime older = LocalDateTime.now().minusDays(10);
        LocalDateTime newer = LocalDateTime.now();

        Scoreboard.saveResult(makeResult("OldPlayer", true, 10, older));
        Scoreboard.saveResult(makeResult("NewPlayer", true,  5, newer));

        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        Scoreboard.displayScoreboard(2);

        System.setOut(original);
        String output = baos.toString();

        int newIdx = output.indexOf("NewPlayer");
        int oldIdx = output.indexOf("OldPlayer");
        assertTrue(newIdx < oldIdx,
                "Most recent player should appear first when sorted by date");
    }

    // ---------------------------------------------------------------
    // interactiveDisplay
    // ---------------------------------------------------------------

    @Test
    void testInteractiveDisplayChoice1() {
        Scoreboard.saveResult(makeResult("Alice", true, 10));
        Scanner scanner = new Scanner("1\n");
        assertDoesNotThrow(() -> Scoreboard.interactiveDisplay(scanner),
                "interactiveDisplay() should not throw when user enters '1'");
    }

    @Test
    void testInteractiveDisplayChoice2() {
        Scoreboard.saveResult(makeResult("Alice", true, 10));
        Scanner scanner = new Scanner("2\n");
        assertDoesNotThrow(() -> Scoreboard.interactiveDisplay(scanner),
                "interactiveDisplay() should not throw when user enters '2'");
    }

    @Test
    void testInteractiveDisplayInvalidChoice() {
        Scoreboard.saveResult(makeResult("Alice", true, 10));
        // Non-integer input — hasNextInt() returns false, defaults to sortBy=1
        Scanner scanner = new Scanner("abc\n");
        assertDoesNotThrow(() -> Scoreboard.interactiveDisplay(scanner),
                "interactiveDisplay() should default to sort-by-score for non-integer input");
    }

    @Test
    void testInteractiveDisplayOutOfRangeChoice() {
        Scoreboard.saveResult(makeResult("Alice", true, 10));
        // Integer but not 2 — should default to sortBy=1
        Scanner scanner = new Scanner("99\n");
        assertDoesNotThrow(() -> Scoreboard.interactiveDisplay(scanner),
                "interactiveDisplay() should default to sort-by-score for out-of-range input");
    }

    @Test
    void testInteractiveDisplayEmptyScoreboard() {
        Scanner scanner = new Scanner("1\n");
        assertDoesNotThrow(() -> Scoreboard.interactiveDisplay(scanner),
                "interactiveDisplay() should handle an empty scoreboard without throwing");
    }
}