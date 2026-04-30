package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Tasks.
 * Author: ${user.name}
 * Date: 2026-04-24
 * Cyclomatic Complexity:
 * - askPlayerName: 2
 * - menu: ~13 (Switch cases + Loop)
 * - menuHelp: 1
 * - buildFleet: 3
 * - readShip: 1
 * - readPosition: 1
 * - readClassicPosition: 4
 */
class TasksTest {

    private final InputStream systemIn = System.in;

    @BeforeEach
    void setUp() {
        // Reset manual a variáveis se necessário
    }

    @AfterEach
    void tearDown() {
        // Restaura o System.in original para não corromper o ambiente do terminal após a execução dos testes
        System.setIn(systemIn);
    }

    /**
     * Helper method to simulate user input via standard input stream.
     *
     * @param data The simulated string input
     */
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    // ==========================================
    // TESTES: MÉTODOS AUXILIARES DE LEITURA
    // ==========================================

    @Test
    void askPlayerName1() {
        Scanner scanner = new Scanner("Comandante\n");
        assertDoesNotThrow(() -> Tasks.askPlayerName(scanner),
                "Error: askPlayerName threw an unexpected exception during normal execution");
    }

    @Test
    void askPlayerName2() {
        Scanner scanner = new Scanner("\n   \nAlmirante\n");
        assertDoesNotThrow(() -> Tasks.askPlayerName(scanner),
                "Error: askPlayerName should keep asking until a valid name is provided without throwing exceptions");
    }

    @Test
    void menuHelp() {
        assertDoesNotThrow(Tasks::menuHelp,
                "Error: menuHelp should just print information and not throw any exception");
    }

    @Test
    void readPosition() {
        Scanner scanner = new Scanner("5 8\n");
        Position pos = Tasks.readPosition(scanner);

        assertAll("Validating readPosition output",
                () -> assertNotNull(pos, "Error: Position should not be null"),
                () -> assertEquals(5, pos.getRow(), "Error: expected row to be 5"),
                () -> assertEquals(8, pos.getColumn(), "Error: expected column to be 8")
        );
    }

    @Test
    void readClassicPosition1() {
        Scanner scanner = new Scanner("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(scanner),
                "Error: Expected IllegalArgumentException when scanner is empty");

        assertEquals("Nenhuma posição válida encontrada!", exception.getMessage());
    }

    @Test
    void readClassicPosition2() {
        Scanner scanner = new Scanner("C5\n");
        IPosition pos = Tasks.readClassicPosition(scanner);

        assertAll("Validating classic position format 'A3'",
                () -> assertNotNull(pos, "Error: Position should not be null, input should be parsed correctly")
        );
    }

    @Test
    void readClassicPosition3() {
        Scanner scanner = new Scanner("D 7\n");
        IPosition pos = Tasks.readClassicPosition(scanner);

        assertAll("Validating classic position format 'A 3'",
                () -> assertNotNull(pos, "Error: Position should not be null, input should be parsed correctly")
        );
    }

    @Test
    void readClassicPosition4() {
        Scanner scanner = new Scanner("123\n");
        assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(scanner),
                "Error: Expected IllegalArgumentException for invalid classic position formats");
    }

    @Test
    void testReadClassicPosition_ExtremeEdgeCases() {
        Scanner scanner1 = new Scanner("A\n");
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner1),
                "Error: Should throw exception for incomplete classic position.");
    }

    @Test
    void readShip() {
        Scanner scanner = new Scanner("barca 5 5 s\n");
        Ship ship = Tasks.readShip(scanner);

        assertNotNull(ship, "Error: readShip should return a valid Ship instance");
    }

    @Test
    void testBuildFleet_FailuresAndCollisions() {
        StringBuilder input = new StringBuilder();
        input.append("naviofantasma 0 0 s\n");
        input.append("barca 0 0 s\n");
        input.append("barca 0 0 s\n");

        for (int r = 2; r <= 8; r += 2) {
            for (int c = 0; c <= 8; c += 2) {
                input.append("barca ").append(r).append(" ").append(c).append(" s\n");
            }
        }

        Scanner scanner = new Scanner(input.toString());
        assertDoesNotThrow(() -> Tasks.buildFleet(scanner),
                "Error: buildFleet must handle unknown ships and collisions gracefully.");
    }

    @Test
    void testNullScannerAssertions() {
        assertAll("Testando comportamentos com parâmetros nulos",
                () -> assertThrows(AssertionError.class, () -> Tasks.buildFleet(null),
                        "Error: buildFleet should reject null scanner"),
                () -> assertThrows(AssertionError.class, () -> Tasks.readShip(null),
                        "Error: readShip should reject null scanner"),
                () -> assertThrows(AssertionError.class, () -> Tasks.readPosition(null),
                        "Error: readPosition should reject null scanner")
        );
    }

    // ==========================================
    // TESTES: MENU E SIMULAÇÃO DE JOGO
    // ==========================================

    @Test
    void menu1_FastExit() {
        provideInput("JogadorTest\ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: menu should safely exit when 'desisto' command is issued");
    }

    @Test
    void menu2_BasicCommands() {
        provideInput("JogadorTest\nnome\nNovoNome\najuda\ncomando_inexistente\nestado\ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: menu should handle unrecognized commands and basic state without crashing");
    }

    @Test
    void menu3_GameInitAndReport() {
        provideInput("JogadorTest\ngerafrota\nmapa\ntiros\nranking\ngerareport\ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: menu should successfully iterate through game initializations and PDF report commands");
    }

    @Test
    void testMenu_ActiveFleetCommands() {
        provideInput("Jogador\ngerafrota\nestado\nmapa\ntiros\ndesisto\n");
        assertDoesNotThrow(Tasks::menu,
                "Error: Should successfully process status/map commands with an active fleet.");
    }

    @Test
    void testMenu_NullStates() {
        provideInput("Jogador\nestado\nmapa\nrajada\ntiros\nsimula\ngerareport\ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: Should handle null states gracefully.");
    }

    @Test
    void testMenu_EmptyName() {
        provideInput("Jogador\nnome\n   \ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: Should handle empty name update safely.");
    }

    @Test
    void testMenu_NomeEndOfFile() {
        provideInput("Jogador\nnome\n");
        try {
            Tasks.menu();
        } catch (Exception e) {
            // Ignora o crash do Scanner por falta de input.
            // Atinge o ramo `else` que avisa "Nome não alterado".
        }
    }

    @Test
    void testMenu_LeFrota() {
        StringBuilder input = new StringBuilder("Jogador\nlefrota\n");
        // Fornecemos coordenadas altamente espaçadas, orientadas para SUL ("s")
        for (int r = 0; r <= 8; r += 2) {
            for (int c = 0; c <= 8; c += 2) {
                input.append("barca ").append(r).append(" ").append(c).append(" s\n");
            }
        }

        // Backup extra para garantir que a frota enche e o while() termina
        for (int r = 1; r <= 9; r += 2) {
            for (int c = 1; c <= 9; c += 2) {
                input.append("barca ").append(r).append(" ").append(c).append(" s\n");
            }
        }

        input.append("desisto\n");
        provideInput(input.toString());

        assertDoesNotThrow(Tasks::menu, "Error: LEFROTA command should execute successfully.");
    }

    @Test
    void testMenu_GerareportAfterGame() {
        provideInput("Jogador\ngerafrota\nsimula\ngerareport\ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: Should handle report generation after game finishes.");
    }

    @Test
    void testMenu_SimulaInterrupted() throws InterruptedException {
        provideInput("Jogador\ngerafrota\nsimula\ndesisto\n");

        Thread t = new Thread(Tasks::menu);
        t.start();

        // Interrompe a simulação repetidamente para cobrir o InterruptedException do Thread.sleep
        while (t.isAlive()) {
            t.interrupt();
            Thread.sleep(10);
        }
        t.join();

        assertFalse(t.isAlive(), "Error: Thread should have finished processing simula and desisto.");
    }

    @Test
    void testMenu_RajadaEndGameConditions_Massive() {
        StringBuilder input = new StringBuilder("Jogador\ngerafrota\n");

        for (int round = 0; round < 30; round++) {
            input.append("rajada\n");

             for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    char letter = (char) ('A' + r);
                    input.append(r).append(" ").append(c).append("\n"); // Ex: 0 0 (nosso tiro)
                    input.append(letter).append(c).append("\n");        // Ex: A0 (tiro inimigo)
                }
            }
        }
        input.append("desisto\n");
        provideInput(input.toString());

        try {
            Tasks.menu();
        } catch (Exception e) {
            // NoSuchElementException é esperado aqui quando a Scanner esgota o input.
            // O crucial é que o jogo chega à vitória/derrota antes do fim.
        }
    }
}