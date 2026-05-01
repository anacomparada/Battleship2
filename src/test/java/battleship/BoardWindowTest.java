package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for BoardWindow.
 *
 * NOTE: BoardWindow is a Swing UI class. Tests focus on observable,
 * non-visual behaviour: frame lifecycle (show/close), null-safety,
 * and the internal shot-highlighting logic that drives cell colours.
 *
 * Cyclomatic Complexity for each method:
 * - show:               5
 * - createBoardPanel:   5
 * - close:              2
 */
class BoardWindowTest {

    private char[][] emptyMap;
    private List<IPosition> emptyShots;

    @BeforeEach
    void setUp() {
        BoardWindow.close();

        emptyMap = new char[][]{
                {' ', ' '},
                {' ', ' '}
        };

        emptyShots = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        BoardWindow.close();
    }

    // ---------------------------------------------------------------
    // close()
    // ---------------------------------------------------------------

    @Test
    void testCloseWithoutShow() {
        assertDoesNotThrow(BoardWindow::close,
                "close() should be safe to call even if show() was never called");
    }

    @Test
    void testCloseAfterShow() {
        BoardWindow.show(emptyMap, emptyMap, emptyShots, emptyShots);
        assertDoesNotThrow(BoardWindow::close,
                "close() should not throw after show()");
    }

    @Test
    void testCloseIdempotent() {
        BoardWindow.show(emptyMap, emptyMap, emptyShots, emptyShots);
        BoardWindow.close();

        assertDoesNotThrow(BoardWindow::close,
                "Calling close() twice should not throw");
    }

    // ---------------------------------------------------------------
    // show() — frame lifecycle
    // ---------------------------------------------------------------

    @Test
    void testShowDoesNotThrow() {
        assertDoesNotThrow(
                () -> BoardWindow.show(emptyMap, emptyMap, emptyShots, emptyShots),
                "show() should not throw with valid arguments");
    }

    @Test
    void testShowCalledTwiceDoesNotThrow() {
        assertDoesNotThrow(() -> {
            BoardWindow.show(emptyMap, emptyMap, emptyShots, emptyShots);
            BoardWindow.show(emptyMap, emptyMap, emptyShots, emptyShots);
        }, "Calling show() twice should not throw");
    }

    @Test
    void testShowAfterClose() {
        BoardWindow.show(emptyMap, emptyMap, emptyShots, emptyShots);
        BoardWindow.close();

        assertDoesNotThrow(
                () -> BoardWindow.show(emptyMap, emptyMap, emptyShots, emptyShots),
                "show() should work correctly after a previous close()");
    }

    // ---------------------------------------------------------------
    // show() — map content
    // ---------------------------------------------------------------

    @Test
    void testShowWithAllCellTypes() {
        char[][] map = {
                {'#', '*'},
                {'o', ' '}
        };

        assertDoesNotThrow(
                () -> BoardWindow.show(map, map, emptyShots, emptyShots),
                "show() should handle all cell types");
    }

    @Test
    void testShowWithHitCellAndRecentShot() {
        char[][] map = {
                {'*', ' '},
                {' ', ' '}
        };

        List<IPosition> recentShots = new ArrayList<>();
        recentShots.add(new Position(0, 0));

        assertDoesNotThrow(
                () -> BoardWindow.show(map, map, recentShots, emptyShots),
                "show() should handle recent shot");
    }

    @Test
    void testShowWithRecentShotOnNonHitCell() {
        List<IPosition> recentShots = new ArrayList<>();
        recentShots.add(new Position(0, 0));

        assertDoesNotThrow(
                () -> BoardWindow.show(emptyMap, emptyMap, recentShots, emptyShots),
                "show() should handle recent shot on non-hit cell");
    }

    @Test
    void testShowWithMultipleRecentShots() {
        char[][] map = {
                {'*', '*'},
                {'*', ' '}
        };

        List<IPosition> shots = new ArrayList<>();
        shots.add(new Position(0, 0));
        shots.add(new Position(0, 1));
        shots.add(new Position(1, 0));

        assertDoesNotThrow(
                () -> BoardWindow.show(map, map, shots, emptyShots),
                "show() should handle multiple recent shots");
    }

    // ---------------------------------------------------------------
    // show() — different maps
    // ---------------------------------------------------------------

    @Test
    void testShowWithDifferentMaps() {
        char[][] myMap    = {{'#', 'o'}, {' ', '*'}};
        char[][] alienMap = {{' ', '#'}, {'*', 'o'}};

        assertDoesNotThrow(
                () -> BoardWindow.show(myMap, alienMap, emptyShots, emptyShots),
                "show() should handle different maps");
    }

    // ---------------------------------------------------------------
    // large board
    // ---------------------------------------------------------------

    @Test
    void testShowWithFullSizeBoard() {
        char[][] map = new char[Game.BOARD_SIZE][Game.BOARD_SIZE];

        for (int r = 0; r < Game.BOARD_SIZE; r++)
            for (int c = 0; c < Game.BOARD_SIZE; c++)
                map[r][c] = ' ';

        assertDoesNotThrow(
                () -> BoardWindow.show(map, map, emptyShots, emptyShots),
                "show() should handle full board");
    }
}