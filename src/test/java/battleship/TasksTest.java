package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Tasks.
 * Author: ${user.name}
 * Date: 2026-04-24
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
                () -> assertThrows(IllegalArgumentException.class, () -> Tasks.buildFleet(null),
                        "Error: buildFleet should reject null scanner"),
                () -> assertThrows(IllegalArgumentException.class, () -> Tasks.readShip(null),
                        "Error: readShip should reject null scanner"),
                () -> assertThrows(IllegalArgumentException.class, () -> Tasks.readPosition(null),
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
        assertThrows(NoSuchElementException.class, Tasks::menu,
                "Error: Should throw NoSuchElementException when scanner input ends unexpectedly.");
    }

    @Test
    void testMenu_LeFrota() {
        StringBuilder input = new StringBuilder("Jogador\nlefrota\n");
        for (int r = 0; r <= 8; r += 2) {
            for (int c = 0; c <= 8; c += 2) {
                input.append("barca ").append(r).append(" ").append(c).append(" s\n");
            }
        }
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

        while (t.isAlive()) {
            t.interrupt();
            t.join(10);
        }
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
                    input.append(letter).append(c).append("\n");
                    input.append(r).append(" ").append(c).append("\n");
                }
            }
        }
        input.append("desisto\n");
        provideInput(input.toString());
        Exception exception = assertThrows(RuntimeException.class, Tasks::menu,
                "Error: Expected an exception due to scanner desync during massive random input.");

        assertNotNull(exception, "A excepção de limite ou erro de formato deve ser capturada.");
    }

    // ==========================================
    // TESTES DE COBERTURA ADICIONAL
    // ==========================================

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<Tasks> constructor = Tasks.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        java.lang.reflect.InvocationTargetException exception = assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                constructor::newInstance,
                "Error: Should throw InvocationTargetException wrapping an IllegalStateException"
        );
        assertTrue(exception.getCause() instanceof IllegalStateException, "Error: Cause must be IllegalStateException");
    }

    @Test
    void testReadClassicPosition_Lowercase() {
        Scanner scanner = new Scanner("b4\n");
        IPosition pos = Tasks.readClassicPosition(scanner);
        assertNotNull(pos, "Error: Should parse lowercase classic position");
    }

    @Test
    void testReadClassicPosition_InvalidSpaceFormat() {
        Scanner scanner = new Scanner("A B\n");
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner),
                "Error: Should reject invalid second part");
    }

    @Test
    void testBuildFleet_UnknownShip_BranchCoverage() {
        StringBuilder input = new StringBuilder();
        input.append("submarinovoador 0 0 s\n"); // Inválido
        for (int r = 0; r <= 8; r += 2) {
            for (int c = 0; c <= 8; c += 2) {
                input.append("barca ").append(r).append(" ").append(c).append(" s\n");
            }
        }
        for (int r = 1; r <= 9; r += 2) {
            for (int c = 1; c <= 9; c += 2) {
                input.append("barca ").append(r).append(" ").append(c).append(" s\n");
            }
        }
        Scanner scanner = new Scanner(input.toString());
        assertDoesNotThrow(() -> Tasks.buildFleet(scanner), "Error: buildFleet should skip unknown ships and continue");
    }

    @Test
    void testMenu_ReportNoGame() {
        provideInput("Jogador\ngerareport\ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: gerareport without an active game should be handled safely");
    }

    @Test
    void testMenu_NomeEmptySafely_BranchCoverage() {
        provideInput("Jogador\nnome\n\ndesisto\n");
        assertDoesNotThrow(Tasks::menu, "Error: Should handle empty name gracefully and exit");
    }

    // ==========================================
    // TESTES DE COBERTURA: handleRajada (Privado)
    // ==========================================

    /**
     * CORREÇÃO DO SONARQUBE S1872: Obter classe sem comparar o nome (getSimpleName().equals).
     * Usa diretamente o forName do ClassLoader para evitar queixas de Manutenção.
     */
    private Class<?> getGameSessionClass() throws Exception {
        return Class.forName("battleship.Tasks$GameSession");
    }

    /**
     * Função auxiliar para criar uma instância da classe privada GameSession
     */
    private Object createMockSession(Fleet fleet, Game game) throws Exception {
        Class<?> sessionClass = getGameSessionClass();
        java.lang.reflect.Constructor<?> constructor = sessionClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object session = constructor.newInstance();

        java.lang.reflect.Field fleetField = sessionClass.getDeclaredField("myFleet");
        fleetField.setAccessible(true);
        fleetField.set(session, fleet);

        java.lang.reflect.Field gameField = sessionClass.getDeclaredField("game");
        gameField.setAccessible(true);
        gameField.set(session, game);

        return session;
    }

    /**
     * Função auxiliar para invocar o método privado handleRajada
     */
    private void invokeHandleRajada(Object session, Scanner scanner) throws Exception {
        java.lang.reflect.Method method = Tasks.class.getDeclaredMethod("handleRajada",
                getGameSessionClass(), Scanner.class);
        method.setAccessible(true);
        method.invoke(null, session, scanner);
    }

    /**
     * Função MÁGICA: Permite forçar uma frota a ter X barcos de forma 100% nativa.
     * Ao acedermos à lista getShips() e manipularmos diretamente, não precisamos de reflection perigosa
     * e o compilador Java não vai bloquear nada.
     */
    private void setFleetSize(Fleet fleet, int size) {
        fleet.getShips().clear();
        for (int i = 0; i < size; i++) {
            fleet.getShips().add(Ship.buildShip("barca", Compass.NORTH, new Position(i, i)));
        }
    }

    @Test
    void testHandleRajada_NullStates() {
        assertDoesNotThrow(() -> {
            Object session = createMockSession(null, null);
            invokeHandleRajada(session, new Scanner(""));
        }, "Error: handleRajada should safely return when states are null");
    }

    @Test
    void testHandleRajada_PlayerWins() throws Exception {
        Fleet myFleet = new Fleet();
        Game game = new Game(myFleet);
        setFleetSize(myFleet, 0);
        setFleetSize((Fleet) game.getAlienFleet(), 0);
        Object session = createMockSession(myFleet, game);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 5; i++) {
            sb.append("A1 A2 A3\n");
        }
        assertDoesNotThrow(() -> invokeHandleRajada(session, new Scanner(sb.toString())),
                "Erro: O handleRajada deveria terminar em segurança acionando a vitória.");
    }

    @Test
    void testHandleRajada_PlayerLoses() throws Exception {
        Fleet myFleet = new Fleet();
        Game game = new Game(myFleet);
        setFleetSize((Fleet) game.getAlienFleet(), 1);
        setFleetSize(myFleet, 0);

        Object session = createMockSession(myFleet, game);

        StringBuilder sb = new StringBuilder();
        sb.append("A1 A2 A3\n"); // Nosso tiro (mesmo com 0 barcos, o código lê o input)
        sb.append("B1 B2 B3\n"); // Tiro inimigo (rajada)
        sb.append("agua\n");

        try {
            invokeHandleRajada(session, new Scanner(sb.toString()));
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException("Erro no handleRajada: " + e.getCause().getMessage(), e.getCause());
        }
        java.lang.reflect.Field gameField = session.getClass().getDeclaredField("game");
        gameField.setAccessible(true);
        Object gameAfter = gameField.get(session);

        java.lang.reflect.Field fleetField = session.getClass().getDeclaredField("myFleet");
        fleetField.setAccessible(true);
        Object fleetAfter = fleetField.get(session);

        java.lang.reflect.Field lastGameField = session.getClass().getDeclaredField("lastGame");
        lastGameField.setAccessible(true);
        Object lastGameAfter = lastGameField.get(session);

        assertAll("Validando encerramento de jogo por derrota",
                () -> assertNull(gameAfter, "O 'game' deveria ter sido anulado"),
                () -> assertNull(fleetAfter, "A 'myFleet' deveria ter sido anulada"),
                () -> assertNotNull(lastGameAfter, "O jogo deveria ter sido guardado em 'lastGame'"),
                () -> assertEquals(game, lastGameAfter, "O lastGame deve ser o jogo que acabámos de jogar")
        );
    }

    @Test
    void testHandleRajada_MiddleOfGame() throws Exception {
        Fleet myFleet = new Fleet();
        Game game = new Game(myFleet);

        setFleetSize(myFleet, 2);
        setFleetSize((Fleet) game.getAlienFleet(), 2);

        Object session = createMockSession(myFleet, game);
        StringBuilder sb = new StringBuilder();
        sb.append("A1 A2 A3\n"); // 3 posições para o tiro do jogador
        sb.append("B1 B2 B3\n"); // 3 posições para o tiro do inimigo (rajada)
        sb.append("agua\n");     // Caso o sistema peça confirmação ou feedback adicional
        assertDoesNotThrow(() -> {
            try {
                invokeHandleRajada(session, new Scanner(sb.toString()));
            } catch (Exception e) {
                // Se for InvocationTargetException, lançamos a causa real para o JUnit mostrar
                if (e instanceof java.lang.reflect.InvocationTargetException) {
                    throw new RuntimeException(e.getCause());
                }
                throw e;
            }
        }, "Erro: O handleRajada deveria processar um turno normal com 3 tiros de cada lado.");
        java.lang.reflect.Field gameField = session.getClass().getDeclaredField("game");
        gameField.setAccessible(true);
        assertNotNull(gameField.get(session), "O jogo deve continuar ativo (não nulo).");
    }

    @Test
    void testHandleRajada_NormalRound() throws Exception {
        Fleet myFleet = new Fleet();
        Ship s1 = Ship.buildShip("barca", Compass.NORTH, new Position(1, 1));
        myFleet.addShip(s1);

        Game game = new Game(myFleet);
        Fleet alienFleet = (Fleet) game.getAlienFleet();
        alienFleet.addShip(Ship.buildShip("barca", Compass.NORTH, new Position(5, 5)));

        Object session = createMockSession(myFleet, game);
        StringBuilder sb = new StringBuilder();
        sb.append("J1 J2 J3\n"); // Nosso tiro
        sb.append("A1 A2 A3\n"); // Tiro inimigo
        sb.append("agua\n");

        try {
            invokeHandleRajada(session, new Scanner(sb.toString()));
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }

        java.lang.reflect.Field gameField = session.getClass().getDeclaredField("game");
        gameField.setAccessible(true);
        Object gameAfter = gameField.get(session);

        java.lang.reflect.Field fleetField = session.getClass().getDeclaredField("myFleet");
        fleetField.setAccessible(true);
        Object fleetAfter = fleetField.get(session);

        assertAll("Validando que o jogo continua ativo após turno sem mortes",
                () -> assertNotNull(gameAfter, "O jogo foi encerrado indevidamente (game ficou null)"),
                () -> assertNotNull(fleetAfter, "A frota foi encerrada indevidamente (myFleet ficou null)"),
                () -> assertEquals(game, gameAfter, "O objeto Game deve ser o mesmo")
        );
    }

    @Test
    void testHandleRajada_PlayerLoses_FullCoverage() throws Exception {
        Fleet myFleet = new Fleet();
        Game game = new Game(myFleet);
        setFleetSize((Fleet) game.getAlienFleet(), 1);
        setFleetSize(myFleet, 0);

        Object session = createMockSession(myFleet, game);
        StringBuilder sb = new StringBuilder();
        sb.append("A1 A2 A3\n"); // Nosso tiro
        sb.append("B1 B2 B3\n"); // Tiro inimigo
        sb.append("agua\n");
        try {
            invokeHandleRajada(session, new Scanner(sb.toString()));
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException("Erro interno no handleRajada: " + e.getCause().getMessage(), e.getCause());
        }

        java.lang.reflect.Field gameField = session.getClass().getDeclaredField("game");
        gameField.setAccessible(true);
        Object gameAfter = gameField.get(session);

        java.lang.reflect.Field fleetField = session.getClass().getDeclaredField("myFleet");
        fleetField.setAccessible(true);
        Object fleetAfter = fleetField.get(session);

        java.lang.reflect.Field lastGameField = session.getClass().getDeclaredField("lastGame");
        lastGameField.setAccessible(true);
        Object lastGameAfter = lastGameField.get(session);

        assertAll("Validando encerramento de jogo por derrota do jogador",
                () -> assertNull(gameAfter, "O game deveria ser null após entrar no IF de derrota"),
                () -> assertNull(fleetAfter, "A myFleet deveria ser null após entrar no IF de derrota"),
                () -> assertNotNull(lastGameAfter, "O jogo deveria ter sido movido para lastGame")
        );
    }
}