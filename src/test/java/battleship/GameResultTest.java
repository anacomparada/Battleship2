package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class GameResult.
 * Author: ${user.name}
 * Date: 2026-04-25 02:00
 * Cyclomatic Complexity:
 * - default constructor: 1
 * - parameterized constructor: 1
 * - getPlayerName(): 1
 * - setPlayerName(String): 1
 * - getTotalMoves(): 1
 * - setTotalMoves(int): 1
 * - isPlayerWon(): 1
 * - setPlayerWon(boolean): 1
 * - getDate(): 1
 * - setDate(LocalDateTime): 1
 */
class GameResultTest {

    private GameResult gameResult;

    @BeforeEach
    void setUp() {
        gameResult = new GameResult();
    }

    @AfterEach
    void tearDown() {
        gameResult = null;
    }

    @Test
    @DisplayName("O construtor vazio deve inicializar os atributos com os valores padrão")
    void defaultConstructor() {
        assertAll(
                () -> assertNull(gameResult.getPlayerName(),
                        "Error: expected playerName to be null after default constructor, but got a value."),
                () -> assertEquals(0, gameResult.getTotalMoves(),
                        "Error: expected totalMoves to be 0 after default constructor, but got a different value."),
                () -> assertFalse(gameResult.isPlayerWon(),
                        "Error: expected playerWon to be false after default constructor, but got true."),
                () -> assertNull(gameResult.getDate(),
                        "Error: expected date to be null after default constructor, but got a value.")
        );
    }

    @Test
    @DisplayName("O construtor parametrizado deve inicializar todos os atributos")
    void parameterizedConstructor() {
        LocalDateTime beforeCreation = LocalDateTime.now();

        gameResult = new GameResult("PlayerOne", 12, true);

        LocalDateTime afterCreation = LocalDateTime.now();

        assertAll(
                () -> assertEquals("PlayerOne", gameResult.getPlayerName(),
                        "Error: expected playerName to be PlayerOne, but got a different value."),
                () -> assertEquals(12, gameResult.getTotalMoves(),
                        "Error: expected totalMoves to be 12, but got a different value."),
                () -> assertTrue(gameResult.isPlayerWon(),
                        "Error: expected playerWon to be true, but got false."),
                () -> assertNotNull(gameResult.getDate(),
                        "Error: expected date to be automatically initialized, but got null."),
                () -> assertFalse(gameResult.getDate().isBefore(beforeCreation),
                        "Error: expected date to be equal to or after object creation start time."),
                () -> assertFalse(gameResult.getDate().isAfter(afterCreation),
                        "Error: expected date to be equal to or before object creation end time.")
        );
    }

    @Test
    @DisplayName("getPlayerName deve devolver o nome do jogador")
    void getPlayerName() {
        gameResult = new GameResult("Maria", 5, false);

        assertEquals("Maria", gameResult.getPlayerName(),
                "Error: expected getPlayerName() to return Maria, but got a different value.");
    }

    @Test
    @DisplayName("setPlayerName deve alterar o nome do jogador")
    void setPlayerName() {
        gameResult.setPlayerName("João");

        assertEquals("João", gameResult.getPlayerName(),
                "Error: expected playerName to be João after setPlayerName(), but got a different value.");
    }

    @Test
    @DisplayName("getTotalMoves deve devolver o número total de jogadas")
    void getTotalMoves() {
        gameResult = new GameResult("Maria", 8, true);

        assertEquals(8, gameResult.getTotalMoves(),
                "Error: expected getTotalMoves() to return 8, but got a different value.");
    }

    @Test
    @DisplayName("setTotalMoves deve alterar o número total de jogadas")
    void setTotalMoves() {
        gameResult.setTotalMoves(15);

        assertEquals(15, gameResult.getTotalMoves(),
                "Error: expected totalMoves to be 15 after setTotalMoves(), but got a different value.");
    }

    @Test
    @DisplayName("isPlayerWon deve devolver se o jogador venceu")
    void isPlayerWon() {
        gameResult = new GameResult("Maria", 8, true);

        assertTrue(gameResult.isPlayerWon(),
                "Error: expected isPlayerWon() to return true, but got false.");
    }

    @Test
    @DisplayName("setPlayerWon deve alterar o estado de vitória do jogador")
    void setPlayerWon() {
        gameResult.setPlayerWon(true);

        assertTrue(gameResult.isPlayerWon(),
                "Error: expected playerWon to be true after setPlayerWon(true), but got false.");
    }

    @Test
    @DisplayName("getDate deve devolver a data guardada")
    void getDate() {
        LocalDateTime date = LocalDateTime.of(2026, 4, 25, 2, 0);
        gameResult.setDate(date);

        assertEquals(date, gameResult.getDate(),
                "Error: expected getDate() to return the assigned LocalDateTime, but got a different value.");
    }

    @Test
    @DisplayName("setDate deve alterar a data guardada")
    void setDate() {
        LocalDateTime date = LocalDateTime.of(2026, 4, 25, 2, 30);

        gameResult.setDate(date);

        assertEquals(date, gameResult.getDate(),
                "Error: expected date to be updated after setDate(), but got a different value.");
    }
}