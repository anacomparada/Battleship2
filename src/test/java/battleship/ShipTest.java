package battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {

    private Ship ship;

    private static class TestShip extends Ship {
        TestShip(String category, Compass bearing, IPosition pos, int size) {
            super(category, bearing, pos, size);
        }

        void addPosition(IPosition position) {
            this.positions.add(position);
        }
    }

    @BeforeEach
    void setUp() {
        ship = new Barge(Compass.NORTH, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    @Test
    void constructor() {
        assertAll(
                () -> assertNotNull(ship, "Error: expected ship not null"),
                () -> assertEquals("Barca", ship.getCategory(), "Error: expected category Barca"),
                () -> assertEquals(Compass.NORTH, ship.getBearing(), "Error: expected bearing NORTH"),
                () -> assertEquals(1, ship.getSize(), "Error: expected size 1"),
                () -> assertFalse(ship.getPositions().isEmpty(), "Error: expected positions not empty")
        );
    }

    @Test
    void constructorNullCategoryThrows() {
        assertThrows(NullPointerException.class,
                () -> new TestShip(null, Compass.NORTH, new Position(0, 0), 0),
                "Error: expected NullPointerException for null category");
    }

    @Test
    void constructorNullBearingThrows() {
        assertThrows(NullPointerException.class,
                () -> new TestShip("Teste", null, new Position(0, 0), 0),
                "Error: expected NullPointerException for null bearing");
    }

    @Test
    void constructorNullPositionThrows() {
        assertThrows(NullPointerException.class,
                () -> new TestShip("Teste", Compass.NORTH, null, 0),
                "Error: expected NullPointerException for null position");
    }

    @Test
    void buildShip1() {
        Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Barge, "Error: expected Barge for barca");
    }

    @Test
    void buildShip2() {
        Ship s = Ship.buildShip("caravela", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Caravel, "Error: expected Caravel for caravela");
    }

    @Test
    void buildShip3() {
        Ship s = Ship.buildShip("nau", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Carrack, "Error: expected Carrack for nau");
    }

    @Test
    void buildShip4() {
        Ship s = Ship.buildShip("fragata", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Frigate, "Error: expected Frigate for fragata");
    }

    @Test
    void buildShip5() {
        Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Galleon, "Error: expected Galleon for galeao");
    }

    @Test
    void buildShip6() {
        Ship s = Ship.buildShip("unknown", Compass.NORTH, new Position(0, 0));
        assertNull(s, "Error: expected null for unknown ship kind");
    }

    @Test
    void getCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: expected category Barca");
    }

    @Test
    void getSize() {
        assertEquals(1, ship.getSize(), "Error: expected size 1");
    }

    @Test
    void getBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: expected bearing NORTH");
    }

    @Test
    void getPosition() {
        assertAll(
                () -> assertEquals(5, ship.getPosition().getRow(), "Error: expected row 5"),
                () -> assertEquals(5, ship.getPosition().getColumn(), "Error: expected column 5")
        );
    }

    @Test
    void getPositions() {
        List<IPosition> positions = ship.getPositions();

        assertAll(
                () -> assertNotNull(positions, "Error: expected positions not null"),
                () -> assertEquals(1, positions.size(), "Error: expected 1 position"),
                () -> assertEquals(new Position(5, 5), positions.get(0), "Error: expected position (5,5)")
        );
    }

    @Test
    void getAdjacentPositions1() {
        List<IPosition> adj = ship.getAdjacentPositions();

        assertAll(
                () -> assertNotNull(adj, "Error: expected adjacent positions not null"),
                () -> assertEquals(8, adj.size(), "Error: expected 8 adjacent positions")
        );
    }

    @Test
    void getAdjacentPositions2() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(5, 5), 2);

        Position p1 = new Position(5, 5);
        Position p2 = new Position(5, 6);

        testShip.addPosition(p1);
        testShip.addPosition(p2);

        List<IPosition> adj = testShip.getAdjacentPositions();

        assertAll(
                () -> assertFalse(adj.contains(p1), "Error: adjacent positions should not contain p1"),
                () -> assertFalse(adj.contains(p2), "Error: adjacent positions should not contain p2")
        );
    }

    @Test
    void getAdjacentPositions3() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(5, 5), 2);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(5, 6));

        List<IPosition> adj = testShip.getAdjacentPositions();

        long distinct = adj.stream().distinct().count();

        assertEquals(distinct, adj.size(),
                "Error: expected adjacent positions without duplicates");
    }

    @Test
    void getAdjacentPositionsEmptyShip() {
        TestShip emptyShip = new TestShip("Teste", Compass.NORTH, new Position(5, 5), 0);

        List<IPosition> adj = emptyShip.getAdjacentPositions();

        assertAll(
                () -> assertNotNull(adj, "Error: expected adjacent positions not null"),
                () -> assertTrue(adj.isEmpty(), "Error: expected no adjacent positions")
        );
    }

    @Test
    void stillFloating1() {
        assertTrue(ship.stillFloating(), "Error: expected ship to still be floating");
    }

    @Test
    void stillFloating2() {
        ship.sink();
        assertFalse(ship.stillFloating(), "Error: expected ship not floating after sink");
    }

    @Test
    void stillFloatingAllHitLoop() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(5, 5));

        for (IPosition p : galleon.getPositions()) {
            p.shoot();
        }

        assertFalse(galleon.stillFloating(),
                "Error: ship should not float when all positions are hit");
    }

    @Test
    void shoot1() {
        ship.shoot(new Position(5, 5));

        assertTrue(ship.getPositions().get(0).isHit(),
                "Error: expected ship position to be hit");
    }

    @Test
    void shoot2() {
        ship.shoot(new Position(0, 0));

        assertFalse(ship.getPositions().get(0).isHit(),
                "Error: expected ship position not to be hit");
    }

    @Test
    void shootInvalidPosition() {
        assertThrows(AssertionError.class,
                () -> ship.shoot(new Position(-1, -1)),
                "Error: expected AssertionError for invalid position");
    }

    @Test
    void sink() {
        ship.sink();

        for (IPosition pos : ship.getPositions()) {
            assertTrue(pos.isHit(), "Error: expected all positions to be hit");
        }
    }

    @Test
    void occupies1() {
        assertTrue(ship.occupies(new Position(5, 5)),
                "Error: expected ship to occupy position (5,5)");
    }

    @Test
    void occupies2() {
        assertFalse(ship.occupies(new Position(0, 0)),
                "Error: expected ship not to occupy position (0,0)");
    }

    @Test
    void occupiesEmptyShip() {
        TestShip empty = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 0);

        assertFalse(empty.occupies(new Position(0, 0)),
                "Error: empty ship should not occupy any position");
    }

    @Test
    void tooCloseToPosition1() {
        assertTrue(ship.tooCloseTo(new Position(5, 6)),
                "Error: expected position to be too close");
    }

    @Test
    void tooCloseToPosition2() {
        assertFalse(ship.tooCloseTo(new Position(7, 7)),
                "Error: expected position not to be too close");
    }

    @Test
    void tooCloseToPositionEmptyShip() {
        TestShip empty = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 0);

        assertFalse(empty.tooCloseTo(new Position(5, 5)),
                "Error: empty ship should not be too close to any position");
    }

    @Test
    void tooCloseToShip1() {
        Ship nearby = new Barge(Compass.NORTH, new Position(5, 6));

        assertTrue(ship.tooCloseTo(nearby),
                "Error: expected ships to be too close");
    }

    @Test
    void tooCloseToShip2() {
        Ship far = new Barge(Compass.NORTH, new Position(0, 0));

        assertFalse(ship.tooCloseTo(far),
                "Error: expected ships not to be too close");
    }

    @Test
    void tooCloseToShipWithNoPositions() {
        TestShip emptyShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 0);

        assertFalse(ship.tooCloseTo(emptyShip),
                "Error: expected ship not to be too close to ship with no positions");
    }

    @Test
    void tooCloseToBothEmptyShips() {
        TestShip s1 = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 0);
        TestShip s2 = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 0);

        assertFalse(s1.tooCloseTo(s2),
                "Error: two empty ships should not be too close");
    }

    @Test
    void getTopMostPos1() {
        assertEquals(5, ship.getTopMostPos(),
                "Error: expected top most position 5");
    }

    @Test
    void getTopMostPos2() {
        Ship frigate = new Frigate(Compass.NORTH, new Position(5, 5));

        int expected = frigate.getPositions().stream()
                .mapToInt(IPosition::getRow)
                .min()
                .getAsInt();

        assertEquals(expected, frigate.getTopMostPos(),
                "Error: expected minimum row");
    }

    @Test
    void getBottomMostPos1() {
        assertEquals(5, ship.getBottomMostPos(),
                "Error: expected bottom most position 5");
    }

    @Test
    void getBottomMostPos2() {
        Ship frigate = new Frigate(Compass.SOUTH, new Position(5, 5));

        int expected = frigate.getPositions().stream()
                .mapToInt(IPosition::getRow)
                .max()
                .getAsInt();

        assertEquals(expected, frigate.getBottomMostPos(),
                "Error: expected maximum row");
    }

    @Test
    void getLeftMostPos1() {
        assertEquals(5, ship.getLeftMostPos(),
                "Error: expected left most position 5");
    }

    @Test
    void getLeftMostPos2() {
        Ship frigate = new Frigate(Compass.WEST, new Position(5, 5));

        int expected = frigate.getPositions().stream()
                .mapToInt(IPosition::getColumn)
                .min()
                .getAsInt();

        assertEquals(expected, frigate.getLeftMostPos(),
                "Error: expected minimum column");
    }

    @Test
    void getRightMostPos1() {
        assertEquals(5, ship.getRightMostPos(),
                "Error: expected right most position 5");
    }

    @Test
    void getRightMostPos2() {
        Ship frigate = new Frigate(Compass.EAST, new Position(5, 5));

        int expected = frigate.getPositions().stream()
                .mapToInt(IPosition::getColumn)
                .max()
                .getAsInt();

        assertEquals(expected, frigate.getRightMostPos(),
                "Error: expected maximum column");
    }



    @Test
    void buildShipNullKindThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> Ship.buildShip(null, Compass.NORTH, new Position(0, 0)),
                "Error: expected AssertionError when shipKind is null");
    }

    @Test
    void buildShipNullBearingThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> Ship.buildShip("barca", null, new Position(0, 0)),
                "Error: expected AssertionError when bearing is null");
    }

    @Test
    void buildShipNullPositionThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> Ship.buildShip("barca", Compass.NORTH, null),
                "Error: expected AssertionError when position is null");
    }

    @Test
    void occupiesNullThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> ship.occupies(null),
                "Error: expected AssertionError when occupies receives null");
    }

    @Test
    void tooCloseToPositionNullThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> ship.tooCloseTo((IPosition) null),
                "Error: expected AssertionError when position is null");
    }

    @Test
    void tooCloseToShipNullThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> ship.tooCloseTo((IShip) null),
                "Error: expected AssertionError when ship is null");
    }

    @Test
    void shootNullThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> ship.shoot(null),
                "Error: expected AssertionError when shoot receives null");
    }

    @Test
    void shootOutsideBoardThrowsAssertionError() {
        assertThrows(AssertionError.class,
                () -> ship.shoot(new Position(-1, 0)),
                "Error: expected AssertionError when position is outside the board");
    }


    @Test
    void stillFloatingStopsAtFirstNotHit() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(5, 5));

        assertTrue(galleon.stillFloating(),
                "Error: expected stillFloating to return true when first position is not hit");
    }

    @Test
    void getTopMostPosNoChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(6, 5));
        testShip.addPosition(new Position(7, 5));

        assertEquals(5, testShip.getTopMostPos(),
                "Error: expected top most row to remain 5");
    }

    @Test
    void getBottomMostPosNoChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(4, 5));
        testShip.addPosition(new Position(3, 5));

        assertEquals(5, testShip.getBottomMostPos(),
                "Error: expected bottom most row to remain 5");
    }

    @Test
    void getLeftMostPosNoChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(5, 6));
        testShip.addPosition(new Position(5, 7));

        assertEquals(5, testShip.getLeftMostPos(),
                "Error: expected left most column to remain 5");
    }

    @Test
    void getRightMostPosNoChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(5, 4));
        testShip.addPosition(new Position(5, 3));

        assertEquals(5, testShip.getRightMostPos(),
                "Error: expected right most column to remain 5");
    }


    @Test
    void getTopMostPosChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(4, 5));
        testShip.addPosition(new Position(3, 5));

        assertEquals(3, testShip.getTopMostPos(),
                "Error: expected top most row to change to 3");
    }

    @Test
    void getBottomMostPosChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(6, 5));
        testShip.addPosition(new Position(7, 5));

        assertEquals(7, testShip.getBottomMostPos(),
                "Error: expected bottom most row to change to 7");
    }

    @Test
    void getLeftMostPosChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(5, 4));
        testShip.addPosition(new Position(5, 3));

        assertEquals(3, testShip.getLeftMostPos(),
                "Error: expected left most column to change to 3");
    }

    @Test
    void getRightMostPosChangeBranch() {
        TestShip testShip = new TestShip("Teste", Compass.NORTH, new Position(0, 0), 3);

        testShip.addPosition(new Position(5, 5));
        testShip.addPosition(new Position(5, 6));
        testShip.addPosition(new Position(5, 7));

        assertEquals(7, testShip.getRightMostPos(),
                "Error: expected right most column to change to 7");
    }




    @Test
    void toStringTest() {
        String expected = "[Barca n F6]";

        assertEquals(expected, ship.toString(),
                "Error: expected " + expected + " but got " + ship.toString());
    }
}