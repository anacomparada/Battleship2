package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Fleet.
 * Author: ${user.name}
 * Date: 2026-04-24
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - createRandom: 3
 * - addShip: 3
 * - getShips: 1
 * - getShipsLike: 2
 * - getFloatingShips: 2
 * - getSunkShips: 2
 * - shipAt: 2
 * - isInsideBoard: 3
 * - colisionRisk: 2
 * - print methods: 1 to 2
 */
class FleetTest {

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet();
    }

    @AfterEach
    void tearDown() {
        fleet = null;
    }

    // ==========================================
    // TESTES ORIGINAIS (Adaptados e Revistos)
    // ==========================================

    @Test
    void testConstructor() {
        assertNotNull(fleet, "Error: Instance of Fleet should not be null.");
        assertTrue(fleet.getShips().isEmpty(), "Error: Fleet should be initialized with empty ships list.");
    }

    @Test
    void testAddShip1() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        assertTrue(fleet.addShip(ship), "Error: Valid ship should be added successfully.");
        assertEquals(1, fleet.getShips().size(), "Error: Fleet should contain one ship after addition.");
    }

    @Test
    void testAddShip2() {
        for (int i = 0; i < Fleet.FLEET_SIZE; i++) {
            fleet.addShip(new Barge(Compass.NORTH, new Position(i, 0)));
        }
        IShip anotherShip = new Barge(Compass.NORTH, new Position(10, 10));
        assertFalse(fleet.addShip(anotherShip), "Error: Should not add ship when fleet size limit is reached.");
    }

    @Test
    void testAddShip3() {
        IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
        assertFalse(fleet.addShip(shipOutside), "Error: Should not add ship outside the board.");
    }

    @Test
    void testAddShip4() {
        IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
        IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
        fleet.addShip(ship1);
        assertFalse(fleet.addShip(ship2), "Error: Should not add ship with a collision risk.");
    }

    @Test
    void testGetShips() {
        assertTrue(fleet.getShips().isEmpty(), "Error: Fleet's ships list should initially be empty.");
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        fleet.addShip(ship);
        assertEquals(1, fleet.getShips().size(), "Error: Fleet should have size 1 after adding a ship.");
        assertEquals(ship, fleet.getShips().get(0), "Error: Fleet's first ship should match the added ship.");
    }

    @Test
    void testGetShipsLike() {
        IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
        IShip ship2 = new Caravel(Compass.NORTH, new Position(2, 1));
        fleet.addShip(ship1);
        fleet.addShip(ship2);

        List<IShip> barges = fleet.getShipsLike("Barca");
        assertEquals(1, barges.size(), "Error: There should be exactly one ship of category 'Barca'.");
        assertEquals(ship1, barges.get(0), "Error: The ship of category 'Barca' does not match.");
    }

    @Test
    void testGetFloatingShips() {
        IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
        IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
        fleet.addShip(ship1);
        fleet.addShip(ship2);

        List<IShip> floatingShips = fleet.getFloatingShips();
        assertEquals(2, floatingShips.size(), "Error: All ships should be floating initially.");

        ship1.getPositions().get(0).shoot();  // Sink ship1
        floatingShips = fleet.getFloatingShips();
        assertEquals(1, floatingShips.size(), "Error: Only one ship should be floating after sinking one.");
        assertEquals(ship2, floatingShips.get(0), "Error: The floating ship should match the expected result.");
    }

    @Test
    void testShipAt() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        fleet.addShip(ship);

        assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Error: Should return the correct ship at the position.");
        assertNull(fleet.shipAt(new Position(5, 5)), "Error: Should return null for empty positions in the fleet.");
    }

    @Test
    void testIsInsideBoard() throws Exception {
        var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
        method.setAccessible(true);

        IShip insideShip = new Barge(Compass.NORTH, new Position(1, 1));
        IShip outsideShip = new Barge(Compass.NORTH, new Position(99, 99));

        assertTrue((Boolean) method.invoke(fleet, insideShip), "Error: Ship inside the board should return true.");
        assertFalse((Boolean) method.invoke(fleet, outsideShip), "Error: Ship outside the board should return false.");
    }

    @Test
    void testColisionRisk() throws Exception {
        var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
        method.setAccessible(true);

        IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
        IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
        fleet.addShip(ship1);

        assertTrue((Boolean) method.invoke(fleet, ship2), "Error: Overlapping ships should be at collision risk.");
        assertFalse((Boolean) method.invoke(fleet, new Barge(Compass.NORTH, new Position(5, 5))),
                "Error: Ships at non-overlapping positions should not have a collision risk.");
    }

    @Test
    void testPrintStatus() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        fleet.addShip(ship);
        assertDoesNotThrow(fleet::printStatus, "Error: printStatus should not throw any exceptions.");
    }

    // ==========================================
    // TESTES COMPLEMENTARES (Parte 1)
    // ==========================================

    @Test
    void testCreateRandom() {
        IFleet randomFleet = Fleet.createRandom();
        assertNotNull(randomFleet, "Error: The randomly created fleet should not be null.");
        assertEquals(IFleet.FLEET_SIZE, randomFleet.getShips().size(), "Error: A completely generated random fleet should have exactly 11 ships.");
    }

    @Test
    void testGetSunkShips() {
        IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
        IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
        fleet.addShip(ship1);
        fleet.addShip(ship2);

        List<IShip> sunkShips = fleet.getSunkShips();
        assertTrue(sunkShips.isEmpty(), "Error: Sunk ships list should be empty initially.");

        ship1.getPositions().get(0).shoot(); // Afundar ship1
        sunkShips = fleet.getSunkShips();

        assertEquals(1, sunkShips.size(), "Error: Only one ship should be sunk after shooting it.");
        assertEquals(ship1, sunkShips.get(0), "Error: The sunk ship should match the expected one.");
    }

    @Test
    void testPrintShips1() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        fleet.addShip(ship);
        assertDoesNotThrow(() -> fleet.printShips(fleet.getShips()),
                "Error: printShips should execute without throwing exceptions for a valid list.");
    }

    @Test
    void testPrintShips2() {
        AssertionError error = assertThrows(AssertionError.class, () -> fleet.printShips(null),
                "Error: printShips should throw an AssertionError when passing a null list.");
        assertNotNull(error, "Error: Exception should not be null.");
    }

    @Test
    void testPrintShipsByCategory() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        fleet.addShip(ship);

        assertDoesNotThrow(() -> fleet.printShipsByCategory("Barca"),
                "Error: printShipsByCategory should not throw exceptions for valid categories.");

        assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null),
                "Error: printShipsByCategory should throw AssertionError when category is null.");
    }

    @Test
    void testPrintFloatingShips() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        fleet.addShip(ship);
        assertDoesNotThrow(() -> fleet.printFloatingShips(),
                "Error: printFloatingShips should not throw exceptions.");
    }

    @Test
    void testPrintAllShips() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        fleet.addShip(ship);
        assertDoesNotThrow(() -> fleet.printAllShips(),
                "Error: printAllShips should not throw exceptions.");
    }

    @Test
    void testAssertionsForNullParameters() {
        assertAll("Null checks using assert keywords",
                () -> assertThrows(AssertionError.class, () -> fleet.addShip(null), "Error: addShip should reject null"),
                () -> assertThrows(AssertionError.class, () -> fleet.getShipsLike(null), "Error: getShipsLike should reject null"),
                () -> assertThrows(AssertionError.class, () -> fleet.shipAt(null), "Error: shipAt should reject null")
        );
    }

    // ==========================================
    // TESTES MÁXIMA COBERTURA BRANCHES (Parte 2)
    // ==========================================

    /**
     * Testa exaustivamente todas as permutações lógicas do (&&) no isInsideBoard.
     * Sem Mockito, usamos classes anónimas para forçar os limites dos navios.
     */
    @Test
    void testIsInsideBoard_AllCompoundBranches() throws Exception {
        var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
        method.setAccessible(true);

        // Falha na 1ª condição: Left < 0
        IShip shipLeftFail = new Barge(Compass.NORTH, new Position(0, 0)) {
            @Override
            public int getLeftMostPos() {
                return -1;
            }
        };
        assertFalse((Boolean) method.invoke(fleet, shipLeftFail), "Error: Should fail if left bounds are < 0");

        // Falha na 2ª condição: Left ok, Right fora do tabuleiro
        IShip shipRightFail = new Barge(Compass.NORTH, new Position(0, 0)) {
            @Override
            public int getLeftMostPos() {
                return 0;
            }

            @Override
            public int getRightMostPos() {
                return Game.BOARD_SIZE;
            }
        };
        assertFalse((Boolean) method.invoke(fleet, shipRightFail), "Error: Should fail if right bounds exceed board");

        // Falha na 3ª condição: Left/Right ok, Top < 0
        IShip shipTopFail = new Barge(Compass.NORTH, new Position(0, 0)) {
            @Override
            public int getLeftMostPos() {
                return 0;
            }

            @Override
            public int getRightMostPos() {
                return Game.BOARD_SIZE - 1;
            }

            @Override
            public int getTopMostPos() {
                return -1;
            }
        };
        assertFalse((Boolean) method.invoke(fleet, shipTopFail), "Error: Should fail if top bounds are < 0");

        // Falha na 4ª condição: Left/Right/Top ok, Bottom fora do tabuleiro
        IShip shipBottomFail = new Barge(Compass.NORTH, new Position(0, 0)) {
            @Override
            public int getLeftMostPos() {
                return 0;
            }

            @Override
            public int getRightMostPos() {
                return Game.BOARD_SIZE - 1;
            }

            @Override
            public int getTopMostPos() {
                return 0;
            }

            @Override
            public int getBottomMostPos() {
                return Game.BOARD_SIZE;
            }
        };
        assertFalse((Boolean) method.invoke(fleet, shipBottomFail), "Error: Should fail if bottom bounds exceed board");
    }

    /**
     * Testa estritamente a interrupção (short-circuit) pela capacidade máxima da frota.
     * Instancia navios reais bem espaçados para evitar o risco de colisão.
     */
    @Test
    void testAddShip_StrictCapacityLimit() {
        int maxCapacity = Fleet.FLEET_SIZE + 1; // <= permite adicionar até atingir FLEET_SIZE + 1

        for (int i = 0; i <= maxCapacity; i++) {

            // Criamos uma "Barge" anónima que contorna as regras físicas do jogo
            // para podermos isolar e testar APENAS o limite matemático da frota.
            IShip ship = new Barge(Compass.NORTH, new Position(5, 5)) {
                // Dizemos que o navio ocupa apenas a posição central (5,5) para estar sempre seguro no tabuleiro
                @Override
                public int getLeftMostPos() {
                    return 5;
                }

                @Override
                public int getRightMostPos() {
                    return 5;
                }

                @Override
                public int getTopMostPos() {
                    return 5;
                }

                @Override
                public int getBottomMostPos() {
                    return 5;
                }

                // Forçamos o navio a dizer SEMPRE que não está perto de nenhum outro
                @Override
                public boolean tooCloseTo(IShip other) {
                    return false;
                }
            };

            if (i < maxCapacity) {
                assertTrue(fleet.addShip(ship), "Error: Ship should be added while under capacity limit.");
            } else {
                // Aqui atinge o limite máximo da frota, o "if" principal no addShip falha logo
                assertFalse(fleet.addShip(ship), "Error: Ship addition should be blocked by fleet size limit.");
            }
        }
    }
}