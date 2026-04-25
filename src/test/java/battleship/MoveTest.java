package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Move.
 * Author: ${user.name}
 * Date: 2026-04-25 02:00
 * Cyclomatic Complexity:
 * - constructor: 1
 * - toString: 1
 * - getNumber: 1
 * - getShots: 1
 * - getShotResults: 1
 * - processEnemyFire: 21
 */
class MoveTest {

    private Move move;
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        move = new Move(1, new ArrayList<>(), new ArrayList<>());
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() {
        move = null;
        System.setOut(originalOut);
        outputStream = null;
    }

    @Test
    @DisplayName("O construtor deve guardar o número da jogada, os tiros e os resultados")
    void constructor() {
        List<IPosition> shots = positions(2);
        List<IGame.ShotResult> results = List.of(
                shotResult(true, false, null, false),
                shotResult(true, true, null, false)
        );

        Move localMove = new Move(7, shots, results);

        assertAll(
                () -> assertEquals(7, localMove.getNumber(),
                        "Error: expected move number 7 but got a different number."),
                () -> assertSame(shots, localMove.getShots(),
                        "Error: expected constructor to keep the same shots list reference."),
                () -> assertSame(results, localMove.getShotResults(),
                        "Error: expected constructor to keep the same shotResults list reference.")
        );
    }

    @Test
    @DisplayName("toString deve devolver o resumo da jogada com número, tiros e resultados")
    void toStringTest() {
        move = new Move(5, positions(3), List.of(
                shotResult(true, false, null, false),
                shotResult(true, true, null, false)
        ));

        String actual = move.toString();

        assertEquals("Move{number=5, shots=3, results=2}", actual,
                "Error: expected formatted Move string with correct number, shots size and results size.");
    }

    @Test
    @DisplayName("getNumber deve devolver o número da jogada")
    void getNumber() {
        move = new Move(9, new ArrayList<>(), new ArrayList<>());

        assertEquals(9, move.getNumber(),
                "Error: expected getNumber() to return 9 but got a different value.");
    }

    @Test
    @DisplayName("getShots deve devolver a mesma lista de tiros")
    void getShots() {
        List<IPosition> shots = positions(4);
        move = new Move(1, shots, new ArrayList<>());

        assertSame(shots, move.getShots(),
                "Error: expected getShots() to return the same list instance passed to the constructor.");
    }

    @Test
    @DisplayName("getShotResults deve devolver a mesma lista de resultados dos tiros")
    void getShotResults() {
        List<IGame.ShotResult> results = List.of(
                shotResult(true, false, null, false)
        );
        move = new Move(1, new ArrayList<>(), results);

        assertSame(results, move.getShotResults(),
                "Error: expected getShotResults() to return the same list instance passed to the constructor.");
    }

    @Test
    @DisplayName("processEnemyFire deve ignorar tiros inválidos")
    void processEnemyFire1() throws Exception {
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(false, false, null, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(0, json.get("validShots").asInt(),
                        "Error: expected 0 valid shots when the only shot result is invalid."),
                () -> assertEquals(0, json.get("repeatedShots").asInt(),
                        "Error: expected 0 repeated shots when the only shot result is invalid."),
                () -> assertEquals(0, json.get("missedShots").asInt(),
                        "Error: expected 0 missed shots when invalid shots are ignored."),
                () -> assertEquals(Game.NUMBER_SHOTS, json.get("outsideShots").asInt(),
                        "Error: expected outside shots to equal Game.NUMBER_SHOTS when no valid or repeated shots exist.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve contar tiros repetidos")
    void processEnemyFire2() throws Exception {
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, true, null, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(0, json.get("validShots").asInt(),
                        "Error: expected repeated shots not to be counted as valid shots."),
                () -> assertEquals(1, json.get("repeatedShots").asInt(),
                        "Error: expected repeatedShots to be incremented to 1."),
                () -> assertEquals(0, json.get("missedShots").asInt(),
                        "Error: expected repeated shots not to be counted as missed shots."),
                () -> assertEquals(Game.NUMBER_SHOTS - 1, json.get("outsideShots").asInt(),
                        "Error: expected outsideShots to be Game.NUMBER_SHOTS - 1 after one repeated shot.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve contar tiros válidos na água")
    void processEnemyFire3() throws Exception {
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(1, json.get("validShots").asInt(),
                        "Error: expected one valid shot for a valid non-repeated result."),
                () -> assertEquals(0, json.get("repeatedShots").asInt(),
                        "Error: expected repeatedShots to remain 0 for a non-repeated valid shot."),
                () -> assertEquals(1, json.get("missedShots").asInt(),
                        "Error: expected missedShots to be 1 when ship() is null."),
                () -> assertTrue(json.get("sunkBoats").isArray() && json.get("sunkBoats").isEmpty(),
                        "Error: expected sunkBoats to be empty for a missed shot."),
                () -> assertTrue(json.get("hitsOnBoats").isArray() && json.get("hitsOnBoats").isEmpty(),
                        "Error: expected hitsOnBoats to be empty for a missed shot.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve contar um acerto num navio não afundado")
    void processEnemyFire4() throws Exception {
        IShip frigate = ship("Frigate");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, frigate, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(1, json.get("validShots").asInt(),
                        "Error: expected one valid shot for a valid hit."),
                () -> assertEquals(0, json.get("missedShots").asInt(),
                        "Error: expected missedShots to remain 0 for a hit on a ship."),
                () -> assertEquals(0, json.get("sunkBoats").size(),
                        "Error: expected sunkBoats to be empty when the ship was hit but not sunk."),
                () -> assertEquals(1, json.get("hitsOnBoats").size(),
                        "Error: expected one boat entry in hitsOnBoats for a non-sunk hit."),
                () -> assertEquals("Frigate", json.get("hitsOnBoats").get(0).get("type").asText(),
                        "Error: expected the hit boat type to be Frigate."),
                () -> assertEquals(1, json.get("hitsOnBoats").get(0).get("hits").asInt(),
                        "Error: expected hit count for Frigate to be 1.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve contar um navio afundado")
    void processEnemyFire5() throws Exception {
        IShip destroyer = ship("Destroyer");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, destroyer, true)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(1, json.get("validShots").asInt(),
                        "Error: expected one valid shot for a sunk ship result."),
                () -> assertEquals(1, json.get("sunkBoats").size(),
                        "Error: expected one boat entry in sunkBoats when a ship is sunk."),
                () -> assertEquals("Destroyer", json.get("sunkBoats").get(0).get("type").asText(),
                        "Error: expected the sunk boat type to be Destroyer."),
                () -> assertEquals(1, json.get("sunkBoats").get(0).get("count").asInt(),
                        "Error: expected sunk count for Destroyer to be 1."),
                () -> assertEquals(0, json.get("hitsOnBoats").size(),
                        "Error: expected hitsOnBoats to exclude boats that were sunk.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve agregar vários navios afundados do mesmo tipo")
    void processEnemyFire6() throws Exception {
        IShip submarine = ship("Submarine");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, submarine, true),
                shotResult(true, false, submarine, true)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(2, json.get("validShots").asInt(),
                        "Error: expected two valid shots for two valid sunk results."),
                () -> assertEquals(1, json.get("sunkBoats").size(),
                        "Error: expected sunkBoats to aggregate equal boat categories into one entry."),
                () -> assertEquals("Submarine", json.get("sunkBoats").get(0).get("type").asText(),
                        "Error: expected the aggregated sunk boat type to be Submarine."),
                () -> assertEquals(2, json.get("sunkBoats").get(0).get("count").asInt(),
                        "Error: expected the aggregated sunk count for Submarine to be 2.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve agregar vários acertos no mesmo tipo de navio")
    void processEnemyFire7() throws Exception {
        IShip cruiser = ship("Cruiser");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, cruiser, false),
                shotResult(true, false, cruiser, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(2, json.get("validShots").asInt(),
                        "Error: expected two valid shots for two non-sunk hits."),
                () -> assertEquals(1, json.get("hitsOnBoats").size(),
                        "Error: expected hitsOnBoats to aggregate equal boat categories into one entry."),
                () -> assertEquals("Cruiser", json.get("hitsOnBoats").get(0).get("type").asText(),
                        "Error: expected the aggregated hit boat type to be Cruiser."),
                () -> assertEquals(2, json.get("hitsOnBoats").get(0).get("hits").asInt(),
                        "Error: expected the aggregated hit count for Cruiser to be 2.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve combinar tiros válidos, repetidos, falhados e inválidos")
    void processEnemyFire8() throws Exception {
        IShip frigate = ship("Frigate");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false),
                shotResult(true, false, frigate, false),
                shotResult(true, true, null, false),
                shotResult(false, false, null, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals(2, json.get("validShots").asInt(),
                        "Error: expected two valid shots from one miss and one hit."),
                () -> assertEquals(1, json.get("repeatedShots").asInt(),
                        "Error: expected one repeated shot to be counted."),
                () -> assertEquals(1, json.get("missedShots").asInt(),
                        "Error: expected one missed shot when one valid shot has no ship."),
                () -> assertEquals(Game.NUMBER_SHOTS - 3, json.get("outsideShots").asInt(),
                        "Error: expected outsideShots to be Game.NUMBER_SHOTS minus valid and repeated shots."),
                () -> assertEquals(1, json.get("hitsOnBoats").size(),
                        "Error: expected one hit entry for Frigate in hitsOnBoats.")
        );
    }

    @Test
    @DisplayName("processEnemyFire não deve imprimir nada quando verbose é falso")
    void processEnemyFire9() throws Exception {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(false));

        assertAll(
                () -> assertEquals("", outputStream.toString(),
                        "Error: expected no console output when verbose is false."),
                () -> assertEquals(1, json.get("missedShots").asInt(),
                        "Error: expected the returned JSON to still contain the correct missed shot count.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem singular para um tiro repetido")
    void processEnemyFire10() {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, true, null, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertAll(
                () -> assertTrue(printed.contains("1 tiro repetido"),
                        "Error: expected verbose output to contain '1 tiro repetido' for a single repeated shot."),
                () -> assertTrue(printed.contains("Jogada nº1 ->"),
                        "Error: expected verbose output to start with the move header.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem plural para tiros repetidos")
    void processEnemyFire11() {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, true, null, false),
                shotResult(true, true, null, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertTrue(printed.contains("2 tiros repetidos"),
                "Error: expected verbose output to contain plural repeated-shot wording for two repeated shots.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem singular para um tiro válido")
    void processEnemyFire12() {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertTrue(printed.contains("1 tiro válido"),
                "Error: expected verbose output to contain singular valid-shot wording for one valid shot.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem plural para tiros válidos")
    void processEnemyFire13() {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false),
                shotResult(true, false, null, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertTrue(printed.contains("2 tiros válidos"),
                "Error: expected verbose output to contain plural valid-shot wording for two valid shots.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem de navio afundado")
    void processEnemyFire14() {
        System.setOut(new PrintStream(outputStream));
        IShip destroyer = ship("Destroyer");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, destroyer, true)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertAll(
                () -> assertTrue(printed.contains("1 Destroyer ao fundo"),
                        "Error: expected verbose output to mention one sunk Destroyer."),
                () -> assertFalse(printed.contains("num(a) Destroyer"),
                        "Error: expected a sunk boat not to appear in the non-sunk hits section.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem de acerto em navio não afundado")
    void processEnemyFire15() {
        System.setOut(new PrintStream(outputStream));
        IShip frigate = ship("Frigate");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, frigate, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertAll(
                () -> assertTrue(printed.contains("1 tiro num(a) Frigate"),
                        "Error: expected verbose output to mention one hit on Frigate."),
                () -> assertFalse(printed.contains("ao fundo"),
                        "Error: expected verbose output not to mention a sunk boat when sunk is false.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem singular para um tiro na água")
    void processEnemyFire16() {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertTrue(printed.contains("1 tiro na água"),
                "Error: expected verbose output to contain singular missed-shot wording for one miss.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem plural para tiros na água")
    void processEnemyFire17() {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false),
                shotResult(true, false, null, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertTrue(printed.contains("2 tiros na água"),
                "Error: expected verbose output to contain plural missed-shot wording for two misses.");
    }

    @Test
    @DisplayName("processEnemyFire deve acrescentar tiros repetidos depois dos tiros válidos")
    void processEnemyFire18() {
        System.setOut(new PrintStream(outputStream));
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false),
                shotResult(true, true, null, false)
        ));

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertAll(
                () -> assertTrue(printed.contains("1 tiro válido"),
                        "Error: expected verbose output to contain the valid-shot section."),
                () -> assertTrue(printed.contains(", 1 tiro repetido"),
                        "Error: expected verbose output to append repeated-shot information after valid shots using a comma.")
        );
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem singular para um tiro exterior")
    void processEnemyFire19() {
        Assumptions.assumeTrue(Game.NUMBER_SHOTS >= 1,
                "Game.NUMBER_SHOTS must be at least 1 for this test.");

        System.setOut(new PrintStream(outputStream));

        List<IGame.ShotResult> results = new ArrayList<>();
        for (int i = 0; i < Game.NUMBER_SHOTS - 1; i++) {
            results.add(shotResult(true, false, null, false));
        }
        move = new Move(1, new ArrayList<>(), results);

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertTrue(printed.contains("1 tiro exterior"),
                "Error: expected verbose output to contain singular outside-shot wording when exactly one shot is outside.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir mensagem plural para tiros exteriores")
    void processEnemyFire20() {
        Assumptions.assumeTrue(Game.NUMBER_SHOTS >= 2,
                "Game.NUMBER_SHOTS must be at least 2 for this test.");

        System.setOut(new PrintStream(outputStream));

        List<IGame.ShotResult> results = new ArrayList<>();
        for (int i = 0; i < Game.NUMBER_SHOTS - 2; i++) {
            results.add(shotResult(true, false, null, false));
        }
        move = new Move(1, new ArrayList<>(), results);

        move.processEnemyFire(true);
        String printed = outputStream.toString();

        assertTrue(printed.contains("2 tiros exteriores"),
                "Error: expected verbose output to contain plural outside-shot wording when exactly two shots are outside.");
    }

    @Test
    @DisplayName("processEnemyFire deve excluir tipos de navios afundados de hitsOnBoats")
    void processEnemyFire21() throws Exception {
        System.setOut(new PrintStream(outputStream));
        IShip submarine = ship("Submarine");
        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, submarine, true),
                shotResult(true, false, submarine, false)
        ));

        JsonNode json = readJson(move.processEnemyFire(true));
        String printed = outputStream.toString();

        assertAll(
                () -> assertEquals(1, json.get("sunkBoats").size(),
                        "Error: expected one sunk boat entry for Submarine."),
                () -> assertEquals(0, json.get("hitsOnBoats").size(),
                        "Error: expected hitsOnBoats to exclude Submarine because it already appears in sunkBoats."),
                () -> assertFalse(printed.contains("num(a) Submarine"),
                        "Error: expected verbose output not to print non-sunk hit text for a boat category already present in sunkBoats.")
        );
    }


    @Test
    @DisplayName("processEnemyFire deve tratar verbose true sem tiros válidos nem repetidos")
    void processEnemyFire22() {
        System.setOut(new PrintStream(outputStream));

        move = new Move(1, new ArrayList<>(), new ArrayList<>());

        move.processEnemyFire(true);

        assertTrue(outputStream.toString().contains("exterior"),
                "Error: expected outside shot message when there are no processed shots.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir plural para vários navios afundados do mesmo tipo")
    void processEnemyFire23() {
        System.setOut(new PrintStream(outputStream));

        IShip destroyer = ship("Destroyer");

        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, destroyer, true),
                shotResult(true, false, destroyer, true)
        ));

        move.processEnemyFire(true);

        assertTrue(outputStream.toString().contains("2 Destroyers ao fundo"),
                "Error: expected plural sunk ship output for two Destroyers.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir plural para vários acertos no mesmo navio")
    void processEnemyFire24() {
        System.setOut(new PrintStream(outputStream));

        IShip cruiser = ship("Cruiser");

        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, cruiser, false),
                shotResult(true, false, cruiser, false)
        ));

        move.processEnemyFire(true);

        assertTrue(outputStream.toString().contains("2 tiros num(a) Cruiser"),
                "Error: expected plural hit output for two hits on Cruiser.");
    }

    @Test
    @DisplayName("processEnemyFire deve remover o sinal mais final quando só há navios afundados")
    void processEnemyFire25() {
        System.setOut(new PrintStream(outputStream));

        IShip submarine = ship("Submarine");

        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, submarine, true)
        ));

        move.processEnemyFire(true);

        assertFalse(outputStream.toString().contains("+"),
                "Error: expected final plus sign to be removed when there are only sunk boats.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir tiros exteriores quando a mensagem ainda está vazia")
    void processEnemyFire26() {
        System.setOut(new PrintStream(outputStream));

        move = new Move(1, new ArrayList<>(), new ArrayList<>());

        move.processEnemyFire(true);

        assertTrue(outputStream.toString().contains("exterior"),
                "Error: expected outside shot message when output was initially empty.");
    }

    @Test
    @DisplayName("processEnemyFire deve imprimir plural para tiros repetidos depois de tiros válidos")
    void processEnemyFire27() {
        System.setOut(new PrintStream(outputStream));

        move = new Move(1, new ArrayList<>(), List.of(
                shotResult(true, false, null, false),
                shotResult(true, true, null, false),
                shotResult(true, true, null, false)
        ));

        move.processEnemyFire(true);

        assertTrue(outputStream.toString().contains(", 2 tiros repetidos"),
                "Error: expected plural repeated-shot output after valid shots.");
    }
    private JsonNode readJson(String json) throws Exception {
        return objectMapper.readTree(json);
    }

    private List<IPosition> positions(int count) {
        List<IPosition> positions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            positions.add(null);
        }
        return positions;
    }

    private IShip ship(String category) {
        InvocationHandler handler = (proxy, method, args) -> {
            return switch (method.getName()) {
                case "getCategory" -> category;
                case "toString" -> "Ship[" + category + "]";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> defaultValue(method.getReturnType());
            };
        };

        return (IShip) Proxy.newProxyInstance(
                IShip.class.getClassLoader(),
                new Class[]{IShip.class},
                handler
        );
    }

    private IGame.ShotResult shotResult(boolean valid, boolean repeated, IShip ship, boolean sunk) {
        try {
            Class<?> shotResultClass = IGame.ShotResult.class;

            if (shotResultClass.isInterface()) {
                InvocationHandler handler = (proxy, method, args) -> {
                    return switch (method.getName()) {
                        case "valid" -> valid;
                        case "repeated" -> repeated;
                        case "ship" -> ship;
                        case "sunk" -> sunk;
                        case "toString" -> "ShotResult[valid=" + valid + ", repeated=" + repeated + ", sunk=" + sunk + "]";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == args[0];
                        default -> defaultValue(method.getReturnType());
                    };
                };

                return (IGame.ShotResult) Proxy.newProxyInstance(
                        shotResultClass.getClassLoader(),
                        new Class[]{shotResultClass},
                        handler
                );
            }

            for (Constructor<?> constructor : shotResultClass.getDeclaredConstructors()) {
                if (constructor.getParameterCount() == 4) {
                    constructor.setAccessible(true);
                    return (IGame.ShotResult) constructor.newInstance(valid, repeated, ship, sunk);
                }
            }

            fail("Error: could not instantiate IGame.ShotResult because no compatible 4-argument constructor was found.");
            return null;
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            fail("Error: failed to create IGame.ShotResult instance due to: " + e.getMessage());
            return null;
        }
    }

    private Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == byte.class) {
            return (byte) 0;
        }
        if (returnType == short.class) {
            return (short) 0;
        }
        if (returnType == int.class) {
            return 0;
        }
        if (returnType == long.class) {
            return 0L;
        }
        if (returnType == float.class) {
            return 0f;
        }
        if (returnType == double.class) {
            return 0d;
        }
        if (returnType == char.class) {
            return '\0';
        }
        return null;
    }
}